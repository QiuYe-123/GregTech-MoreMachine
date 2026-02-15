package cn.qiuye.gtmoremachine.api.pattern;

import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;

public class AdvancedBlockPattern extends BlockPattern {

    static Direction[] FACINGS = { Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.UP,
            Direction.DOWN };
    static Direction[] FACINGS_H = { Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST };

    public final int[][] aisleRepetitions;
    public final RelativeDirection[] structureDir;
    protected final TraceabilityPredicate[][][] blockMatches; // [z][y][x]
    protected final int fingerLength; // z size
    protected final int thumbLength; // y size
    protected final int palmLength; // x size
    protected final int[] centerOffset; // x, y, z, minZ, maxZ

    public AdvancedBlockPattern(TraceabilityPredicate[][][] predicatesIn, RelativeDirection[] structureDir, int[][] aisleRepetitions, int[] centerOffset) {
        super(predicatesIn, structureDir, aisleRepetitions, centerOffset);
        this.blockMatches = predicatesIn;
        this.fingerLength = predicatesIn.length;
        this.structureDir = structureDir;
        this.aisleRepetitions = aisleRepetitions;

        if (this.fingerLength > 0) {
            this.thumbLength = predicatesIn[0].length;

            if (this.thumbLength > 0) {
                this.palmLength = predicatesIn[0][0].length;
            } else {
                this.palmLength = 0;
            }
        } else {
            this.thumbLength = 0;
            this.palmLength = 0;
        }

        this.centerOffset = centerOffset;
    }

    public static AdvancedBlockPattern getAdvancedBlockPattern(BlockPattern blockPattern) {
        try {
            Class<?> clazz = BlockPattern.class;
            // blockMatches
            Field blockMatchesField = clazz.getDeclaredField("blockMatches");
            blockMatchesField.setAccessible(true);
            TraceabilityPredicate[][][] blockMatches = (TraceabilityPredicate[][][]) blockMatchesField.get(blockPattern);
            // structureDir
            Field structureDirField = clazz.getDeclaredField("structureDir");
            structureDirField.setAccessible(true);
            RelativeDirection[] structureDir = (RelativeDirection[]) structureDirField.get(blockPattern);
            // aisleRepetitions
            Field aisleRepetitionsField = clazz.getDeclaredField("aisleRepetitions");
            aisleRepetitionsField.setAccessible(true);
            int[][] aisleRepetitions = (int[][]) aisleRepetitionsField.get(blockPattern);
            // centerOffset
            Field centerOffsetField = clazz.getDeclaredField("centerOffset");
            centerOffsetField.setAccessible(true);
            int[] centerOffset = (int[]) centerOffsetField.get(blockPattern);

            return new AdvancedBlockPattern(blockMatches, structureDir, aisleRepetitions, centerOffset);
        } catch (Exception ignored) {}
        return null;
    }

    public void autoBuild(Player player, MultiblockState worldState,
                          AdvancedTerminalBehavior.AutoBuildSetting autoBuildSetting) {
        Level world = player.level();
        if (autoBuildSetting.isUseDemolish()) {
            autoDemolish(player, worldState, autoBuildSetting);
            return;
        }

        int minZ = -centerOffset[4];
        clearWorldState(worldState);
        MultiblockControllerMachine controller = worldState.getController();
        BlockPos centerPos = controller.self().getBlockPos();
        Direction facing = controller.self().getFrontFacing();
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        boolean isUseMirror = autoBuildSetting.isUseMirror();
        boolean isUseAE = autoBuildSetting.isUseAE();

        Object2IntOpenHashMap<SimplePredicate> cacheGlobal = new Object2IntOpenHashMap<>(worldState.getGlobalCount());
        Object2IntOpenHashMap<SimplePredicate> cacheLayer = new Object2IntOpenHashMap<>(worldState.getLayerCount());
        Object2ObjectOpenHashMap<BlockPos, Object> blocks = new Object2ObjectOpenHashMap<>();
        ObjectOpenHashSet<BlockPos> placeBlockPos = new ObjectOpenHashSet<>();
        blocks.put(centerPos, controller);
        if (controller.isFormed() && autoBuildSetting.isReplaceMode()) controller.onStructureInvalid();

        int[] repeat = new int[this.fingerLength];
        for (int h = 0; h < this.fingerLength; h++) {
            var minH = aisleRepetitions[h][0];
            var maxH = aisleRepetitions[h][1];
            if (minH != maxH) {
                repeat[h] = Math.max(minH, Math.min(maxH, autoBuildSetting.getRepeatCount()));
            } else repeat[h] = minH;
        }

        for (int c = 0, z = minZ++, r; c < this.fingerLength; c++) {
            for (r = 0; r < repeat[c]; r++) {
                cacheLayer.clear();
                for (int b = 0, y = -centerOffset[1]; b < this.thumbLength; b++, y++) {
                    for (int a = 0, x = -centerOffset[0]; a < this.palmLength; a++, x++) {
                        TraceabilityPredicate predicate = this.blockMatches[c][b][a];
                        if (predicate.isAny()) continue;
                        BlockPos pos = setActualRelativeOffset(x, y, z, facing, upwardsFacing, isUseMirror)
                                .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                        updateWorldState(worldState, pos, predicate);
                        ItemStack itemStack = null;
                        if (!world.isEmptyBlock(pos)) {
                            Block block = world.getBlockState(pos).getBlock();
                            if (autoBuildSetting.getBlocks().contains(block) && autoBuildSetting.isReplaceMode()) {
                                itemStack = block.asItem().getDefaultInstance();
                            } else {
                                blocks.put(pos, world.getBlockState(pos));
                                for (SimplePredicate limit : predicate.limited) limit.testLimited(worldState);
                                continue;
                            }
                        }

                        boolean find = false;
                        BlockInfo[] infos = new BlockInfo[0];
                        for (var limit : predicate.limited) {
                            if (limit.minLayerCount > 0 && autoBuildSetting.isPlaceHatch(limit.candidates.get())) {
                                int curr = cacheLayer.getInt(limit);
                                if (curr < limit.minLayerCount &&
                                        (limit.maxLayerCount == -1 || curr < limit.maxLayerCount)) {
                                    cacheLayer.addTo(limit, 1);
                                } else continue;
                            } else continue;
                            infos = limit.candidates == null ? null : limit.candidates.get();
                            find = true;
                            break;
                        }
                        if (!find) {
                            for (var limit : predicate.limited) {
                                if (limit.minCount > 0 && autoBuildSetting.isPlaceHatch(limit.candidates.get())) {
                                    int curr = cacheGlobal.getInt(limit);
                                    if (curr < limit.minCount && (limit.maxCount == -1 || curr < limit.maxCount)) {
                                        cacheGlobal.addTo(limit, 1);
                                    } else continue;
                                } else continue;
                                infos = limit.candidates == null ? null : limit.candidates.get();
                                find = true;
                                break;
                            }
                        }
                        if (!find) { // no limited
                            for (SimplePredicate limit : predicate.limited) {
                                if (!autoBuildSetting.isPlaceHatch(limit.candidates.get())) continue;
                                if (limit.maxLayerCount != -1 &&
                                        cacheLayer.getOrDefault(limit, Integer.MAX_VALUE) == limit.maxLayerCount) {
                                    continue;
                                }
                                if (limit.maxCount != -1 &&
                                        cacheGlobal.getOrDefault(limit, Integer.MAX_VALUE) == limit.maxCount) {
                                    continue;
                                }
                                cacheLayer.addTo(limit, 1);
                                cacheGlobal.addTo(limit, 1);
                                infos = ArrayUtils.addAll(infos, limit.candidates == null ? null : limit.candidates.get());
                            }
                            for (SimplePredicate common : predicate.common) {
                                if (common.candidates != null && predicate.common.size() > 1 && !autoBuildSetting.isPlaceHatch(common.candidates.get())) {
                                    continue;
                                }
                                infos = ArrayUtils.addAll(infos, common.candidates == null ? null : common.candidates.get());
                            }
                        }

                        List<ItemStack> candidates = autoBuildSetting.apply(infos);

                        if (autoBuildSetting.isReplaceMode() && itemStack != null &&
                                ItemStack.isSameItem(candidates.get(0), itemStack))
                            continue;

                        // check inventory
                        Triplet<ItemStack, IItemHandler, Integer> result = foundItem(player, candidates, item -> item instanceof BlockItem, isUseAE);
                        ItemStack found = result.getA();
                        IItemHandler handler = result.getB();
                        int foundSlot = result.getC();

                        if (found == null) continue;

                        // check can get old coilBlock
                        IItemHandler holderHandler = null;
                        int holderSlot = -1;
                        if (autoBuildSetting.isReplaceMode() && itemStack != null) {
                            Pair<IItemHandler, Integer> holderResult = foundHolderSlot(player, itemStack);
                            holderHandler = holderResult.getFirst();
                            holderSlot = holderResult.getSecond();

                            if (holderHandler != null && holderSlot < 0) {
                                continue;
                            }
                        }

                        if (autoBuildSetting.isReplaceMode() && itemStack != null) {
                            world.removeBlock(pos, true);
                            if (holderHandler != null) holderHandler.insertItem(holderSlot, itemStack, false);
                        }

                        BlockItem itemBlock = (BlockItem) found.getItem();
                        BlockPlaceContext context = new BlockPlaceContext(world, player, InteractionHand.MAIN_HAND,
                                found, BlockHitResult.miss(player.getEyePosition(0), Direction.UP, pos));
                        InteractionResult interactionResult = itemBlock.place(context);
                        if (interactionResult != InteractionResult.FAIL) {
                            placeBlockPos.add(pos);
                            if (handler != null) handler.extractItem(foundSlot, 1, false);
                        }
                        if (world.getBlockEntity(pos) instanceof MetaMachine metaMachine) {
                            blocks.put(pos, metaMachine);
                        } else blocks.put(pos, world.getBlockState(pos));
                    }
                }
                z++;
            }
        }
        Direction frontFacing = controller.self().getFrontFacing();
        blocks.object2ObjectEntrySet().fastForEach((entry -> {
            // adjust facing
            var pos = entry.getKey();
            var block = entry.getValue();
            if (!(block instanceof MultiblockControllerMachine)) {
                if (block instanceof BlockState && placeBlockPos.contains(pos)) {
                    resetFacing(pos, (BlockState) block, frontFacing, (p, f) -> {
                        Object object = blocks.get(p.relative(f));
                        return object == null ||
                                (object instanceof BlockState && ((BlockState) object).getBlock() == Blocks.AIR);
                    }, state -> world.setBlock(pos, state, 3));
                } else if (block instanceof MetaMachine machine) {
                    resetFacing(pos, machine.getBlockState(), frontFacing, (p, f) -> {
                        Object object = blocks.get(p.relative(f));
                        if (object == null || (object instanceof BlockState blockState && blockState.isAir())) {
                            return machine.isFacingValid(f);
                        }
                        return false;
                    }, state -> world.setBlock(pos, state, 3));
                }
            }
        }));
        controller.checkPattern();
    }

    /**
     * 自动拆除多方块结构
     * 
     * @param player           执行拆除的玩家
     * @param worldState       多方块状态
     * @param autoBuildSetting 总参数
     */
    public void autoDemolish(Player player, MultiblockState worldState, AdvancedTerminalBehavior.AutoBuildSetting autoBuildSetting) {
        Level world = player.level();
        int minZ = -centerOffset[4];
        MultiblockControllerMachine controller = worldState.getController();
        if (controller == null) {
            return;
        }
        BlockPos centerPos = controller.self().getBlockPos();
        Direction facing = controller.self().getFrontFacing();
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        boolean isUseMirror = autoBuildSetting.isUseMirror();

        // 使用与构建逻辑相同的重复次数计算方式
        int[] repeat = new int[this.fingerLength];
        for (int h = 0; h < this.fingerLength; h++) {
            var minH = aisleRepetitions[h][0];
            var maxH = aisleRepetitions[h][1];
            if (minH != maxH) {
                repeat[h] = Math.max(minH, Math.min(maxH, autoBuildSetting.getRepeatCount()));
            } else {
                repeat[h] = minH;
            }
        }

        List<ItemStack> collectedItems = new ArrayList<>();
        for (int c = 0, z = minZ++, r; c < this.fingerLength; c++) {
            for (r = 0; r < repeat[c]; r++) {
                for (int b = 0, y = -centerOffset[1]; b < this.thumbLength; b++, y++) {
                    for (int a = 0, x = -centerOffset[0]; a < this.palmLength; a++, x++) {
                        TraceabilityPredicate predicate = this.blockMatches[c][b][a];
                        if (predicate.isAny()) continue;
                        BlockPos pos = setActualRelativeOffset(x, y, z, facing, upwardsFacing, isUseMirror)
                                .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                        // 跳过控制器位置，不拆除控制器
                        if (pos.equals(centerPos)) {
                            continue;
                        }
                        if (!world.isEmptyBlock(pos)) {
                            BlockState blockState = world.getBlockState(pos);
                            if (!player.isCreative()) {
                                List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel) world, pos, world.getBlockEntity(pos));
                                collectedItems.addAll(drops);
                            }
                            world.removeBlock(pos, false);
                        }
                    }
                }
                z++;
            }
        }
        if (!collectedItems.isEmpty() && !player.isCreative()) {
            giveItemsToPlayer(player, collectedItems, autoBuildSetting);
        }
        controller.checkPattern();
    }

    /**
     * 将物品给予玩家，如果物品栏满了则掉落在地上
     */
    private void giveItemsToPlayer(Player player, List<ItemStack> items, AdvancedTerminalBehavior.AutoBuildSetting autoBuildSetting) {
        if (player.isCreative()) {
            return;
        }
        boolean useAE = autoBuildSetting.isUseAE();
        if (useAE) {
            // 只在非创造模式下才尝试插入AE网络
            IItemHandler handler = player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    @NotNull
                    ItemStack stack = handler.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    if (stack.getItem() instanceof WirelessTerminalItem terminalItem && stack.hasTag() && stack.getTag().contains("accessPoint", 10)) {
                        IGrid grid = terminalItem.getLinkedGrid(stack, player.level(), player);
                        if (grid != null) {
                            MEStorage storage = grid.getStorageService().getInventory();
                            for (ItemStack candidate : items) {
                                if (!candidate.isEmpty()) {
                                    storage.insert(AEItemKey.of(candidate), candidate.getCount(), Actionable.MODULATE, null);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            IItemHandler playerInventory = player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            for (ItemStack stack : items) {
                if (stack.isEmpty()) continue;
                // 尝试将物品放入玩家物品栏
                ItemStack remaining = stack.copy();
                for (int slot = 0; slot < playerInventory.getSlots() && !remaining.isEmpty(); slot++) {
                    remaining = playerInventory.insertItem(slot, remaining, false);
                }
                // 如果物品栏满了，掉落剩余物品在地上
                if (!remaining.isEmpty()) {
                    player.drop(remaining, false);
                }
            }
        }
    }

    public static Triplet<ItemStack, IItemHandler, Integer> foundItem(Player player,
                                                                      List<ItemStack> candidates,
                                                                      Predicate<Item> test,
                                                                      boolean isUseAE) {
        ItemStack found = null;
        IItemHandler handler = null;
        int foundSlot = -1;
        if (!player.isCreative()) {
            var foundHandler = getMatchStackWithHandler(candidates,
                    player.getCapability(ForgeCapabilities.ITEM_HANDLER), test, player, isUseAE);
            if (foundHandler != null) {
                foundSlot = foundHandler.firstInt();
                handler = foundHandler.second();
                found = handler.getStackInSlot(foundSlot).copy();
            }
        } else {
            for (ItemStack candidate : candidates) {
                found = candidate.copy();
                if (!found.isEmpty() && test.test(found.getItem())) break;
                found = null;
            }
        }
        return new Triplet<>(found, handler, foundSlot);
    }

    private Pair<IItemHandler, Integer> foundHolderSlot(Player player, ItemStack coilItemStack) {
        IItemHandler handler = null;
        int foundSlot = -1;
        if (!player.isCreative()) {
            handler = player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            for (int i = 0; i < handler.getSlots(); i++) {
                @NotNull
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) {
                    if (foundSlot < 0) foundSlot = i;
                } else if (ItemStack.isSameItemSameTags(coilItemStack, stack) && (stack.getCount() + 1) <= stack.getMaxStackSize()) {
                    foundSlot = i;
                }
            }
        }

        return new Pair<>(handler, foundSlot);
    }

    private void clearWorldState(MultiblockState worldState) {
        try {
            Class<?> clazz = Class.forName("com.gregtechceu.gtceu.api.pattern.MultiblockState");
            Method method = clazz.getDeclaredMethod("clean");
            method.setAccessible(true);
            method.invoke(worldState);
        } catch (Exception ignored) {}
    }

    private void updateWorldState(MultiblockState worldState, BlockPos posIn, TraceabilityPredicate predicate) {
        try {
            Class<?> clazz = Class.forName("com.gregtechceu.gtceu.api.pattern.MultiblockState");
            Method method = clazz.getDeclaredMethod("update", BlockPos.class, TraceabilityPredicate.class);
            method.setAccessible(true);
            method.invoke(worldState, posIn, predicate);
        } catch (Exception ignored) {}
    }

    private BlockPos setActualRelativeOffset(int x, int y, int z, Direction facing, Direction upwardsFacing,
                                             boolean isFlipped) {
        int[] c0 = new int[] { x, y, z }, c1 = new int[3];
        if (facing == Direction.UP || facing == Direction.DOWN) {
            Direction of = facing == Direction.DOWN ? upwardsFacing : upwardsFacing.getOpposite();
            for (int i = 0; i < 3; i++) {
                switch (structureDir[i].getActualDirection(of)) {
                    case UP -> c1[1] = c0[i];
                    case DOWN -> c1[1] = -c0[i];
                    case WEST -> c1[0] = -c0[i];
                    case EAST -> c1[0] = c0[i];
                    case NORTH -> c1[2] = -c0[i];
                    case SOUTH -> c1[2] = c0[i];
                }
            }
            int xOffset = upwardsFacing.getStepX();
            int zOffset = upwardsFacing.getStepZ();
            int tmp;
            if (xOffset == 0) {
                tmp = c1[2];
                c1[2] = zOffset > 0 ? c1[1] : -c1[1];
                c1[1] = zOffset > 0 ? -tmp : tmp;
            } else {
                tmp = c1[0];
                c1[0] = xOffset > 0 ? c1[1] : -c1[1];
                c1[1] = xOffset > 0 ? -tmp : tmp;
            }
            if (isFlipped) {
                if (upwardsFacing == Direction.NORTH || upwardsFacing == Direction.SOUTH) {
                    c1[0] = -c1[0]; // flip X-axis
                } else {
                    c1[2] = -c1[2]; // flip Z-axis
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                switch (structureDir[i].getActualDirection(facing)) {
                    case UP -> c1[1] = c0[i];
                    case DOWN -> c1[1] = -c0[i];
                    case WEST -> c1[0] = -c0[i];
                    case EAST -> c1[0] = c0[i];
                    case NORTH -> c1[2] = -c0[i];
                    case SOUTH -> c1[2] = c0[i];
                }
            }
            if (upwardsFacing == Direction.WEST || upwardsFacing == Direction.EAST) {
                int xOffset = upwardsFacing == Direction.EAST ? facing.getClockWise().getStepX() :
                        facing.getClockWise().getOpposite().getStepX();
                int zOffset = upwardsFacing == Direction.EAST ? facing.getClockWise().getStepZ() :
                        facing.getClockWise().getOpposite().getStepZ();
                int tmp;
                if (xOffset == 0) {
                    tmp = c1[2];
                    c1[2] = zOffset > 0 ? -c1[1] : c1[1];
                    c1[1] = zOffset > 0 ? tmp : -tmp;
                } else {
                    tmp = c1[0];
                    c1[0] = xOffset > 0 ? -c1[1] : c1[1];
                    c1[1] = xOffset > 0 ? tmp : -tmp;
                }
            } else if (upwardsFacing == Direction.SOUTH) {
                c1[1] = -c1[1];
                if (facing.getStepX() == 0) {
                    c1[0] = -c1[0];
                } else {
                    c1[2] = -c1[2];
                }
            }
            if (isFlipped) {
                if (upwardsFacing == Direction.NORTH || upwardsFacing == Direction.SOUTH) {
                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        c1[0] = -c1[0]; // flip X-axis
                    } else c1[2] = -c1[2]; // flip Z-axis
                } else c1[1] = -c1[1]; // flip Y-axis
            }
        }
        return new BlockPos(c1[0], c1[1], c1[2]);
    }

    @Nullable
    private static IntObjectPair<IItemHandler> getMatchStackWithHandler(List<ItemStack> candidates,
                                                                        LazyOptional<IItemHandler> cap,
                                                                        Predicate<Item> test,
                                                                        Player player,
                                                                        boolean isUseAE) {
        IItemHandler handler = cap.resolve().orElse(null);
        if (handler == null) return null;
        for (int i = 0; i < handler.getSlots(); i++) {
            @NotNull
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            @NotNull
            LazyOptional<IItemHandler> stackCap = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (stackCap.isPresent()) {
                var rt = getMatchStackWithHandler(candidates, stackCap, test, player, isUseAE);
                if (rt != null) return rt;
            } else if (isUseAE && stack.getItem() instanceof WirelessTerminalItem terminalItem && stack.hasTag() && stack.getTag().contains("accessPoint", 10)) {
                IGrid grid = terminalItem.getLinkedGrid(stack, player.level(), player);
                if (grid != null) {
                    MEStorage storage = grid.getStorageService().getInventory();
                    for (ItemStack candidate : candidates) {
                        if (storage.extract(AEItemKey.of(candidate), 1, Actionable.MODULATE, null) > 0) {
                            NonNullList<ItemStack> stacks = NonNullList.withSize(1, candidate);
                            IItemHandler handler1 = new ItemStackHandler(stacks);
                            return IntObjectPair.of(0, handler1);
                        }
                    }
                }

            } else if (candidates.stream().anyMatch(candidate -> ItemStack.isSameItemSameTags(candidate, stack)) &&
                    !stack.isEmpty() && test.test(stack.getItem())) {
                        return IntObjectPair.of(i, handler);
                    }
        }
        return null;
    }

    private void resetFacing(BlockPos pos, BlockState blockState, Direction facing,
                             BiPredicate<BlockPos, Direction> checker, Consumer<BlockState> consumer) {
        if (blockState.hasProperty(BlockStateProperties.FACING)) {
            tryFacings(blockState, pos, checker, consumer, BlockStateProperties.FACING,
                    facing == null ? FACINGS : ArrayUtils.addAll(new Direction[] { facing }, FACINGS));
        } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            tryFacings(blockState, pos, checker, consumer, BlockStateProperties.HORIZONTAL_FACING,
                    facing == null || facing.getAxis() == Direction.Axis.Y ? FACINGS_H :
                            ArrayUtils.addAll(new Direction[] { facing }, FACINGS_H));
        }
    }

    private void tryFacings(BlockState blockState, BlockPos pos, BiPredicate<BlockPos, Direction> checker,
                            Consumer<BlockState> consumer, Property<Direction> property, Direction[] facings) {
        Direction found = null;
        for (Direction facing : facings) {
            if (checker.test(pos, facing)) {
                found = facing;
                break;
            }
        }
        if (found == null) found = Direction.NORTH;
        consumer.accept(blockState.setValue(property, found));
    }
}
