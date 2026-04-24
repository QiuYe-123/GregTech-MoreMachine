package cn.qiuye.gtmoremachine.api.pattern;

import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AdvancedBlockNoAEPattern extends BlockPattern {

    static Direction[] FACINGS = { Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.UP,
            Direction.DOWN };
    static Direction[] FACINGS_H = { Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST };

    public AdvancedBlockNoAEPattern(TraceabilityPredicate[][][] predicatesIn, RelativeDirection[] structureDir, int[][] aisleRepetitions, int[] centerOffset) {
        super(predicatesIn, structureDir, aisleRepetitions, centerOffset);
    }

    public static AdvancedBlockNoAEPattern getAdvancedBlockPattern(BlockPattern blockPattern) {
        try {
            Class<?> clazz = BlockPattern.class;
            // blockMatches
            Field blockMatchesField = clazz.getDeclaredField("blockMatches");
            blockMatchesField.setAccessible(true);
            TraceabilityPredicate[][][] blockMatches = (TraceabilityPredicate[][][]) blockMatchesField.get(blockPattern);
            // centerOffset
            Field centerOffsetField = clazz.getDeclaredField("centerOffset");
            centerOffsetField.setAccessible(true);
            int[] centerOffset = (int[]) centerOffsetField.get(blockPattern);

            return new AdvancedBlockNoAEPattern(blockMatches, blockPattern.structureDir, blockPattern.aisleRepetitions, centerOffset);
        } catch (Exception ignored) {}
        return null;
    }

    public void autoBuild(Player player, MultiblockState worldState,
                          AdvancedTerminalBehavior.AutoBuildSetting autoBuildSetting) {
        Level world = player.level();
        if (autoBuildSetting.isDemolitionMode()) {
            autoDemolish(player, worldState, autoBuildSetting);
            return;
        }
        int minZ = -centerOffset[4];
        worldState.clean();
        MultiblockControllerMachine controller = worldState.getController();
        BlockPos centerPos = controller.self().getBlockPos();
        Direction facing = controller.self().getFrontFacing();
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        boolean isUseMirror = autoBuildSetting.isFlipMode();

        Object2IntOpenHashMap<SimplePredicate> cacheGlobal = worldState.getGlobalCount();
        Object2IntOpenHashMap<SimplePredicate> cacheLayer = worldState.getLayerCount();
        Object2ObjectOpenHashMap<BlockPos, MetaMachine> blocks = new Object2ObjectOpenHashMap<>();
        ObjectOpenHashSet<BlockPos> placeBlockPos = new ObjectOpenHashSet<>();
        if (controller.isFormed() && autoBuildSetting.isReplaceMode()) controller.onStructureInvalid();

        int[] repeat = new int[this.fingerLength];
        for (int h = 0; h < this.fingerLength; h++) {
            var minH = aisleRepetitions[h][0];
            var maxH = aisleRepetitions[h][1];
            if (minH != maxH) {
                repeat[h] = Math.max(minH, Math.min(maxH, autoBuildSetting.getRepeatCount()));
            } else repeat[h] = minH;
        }

        int aisleIndex = 0;
        for (int currentZ = minZ; aisleIndex < this.fingerLength; aisleIndex++) {
            for (int actualRepeats = 0; actualRepeats < repeat[aisleIndex]; actualRepeats++) {
                cacheLayer.clear();
                int yIndex = 0;
                for (int y = -this.centerOffset[1]; yIndex < this.thumbLength; y++) {
                    int xIndex = 0;
                    for (int x = -this.centerOffset[0]; xIndex < this.palmLength; x++) {
                        TraceabilityPredicate[][] slice = this.blockMatches[aisleIndex];
                        if (slice != null) {
                            TraceabilityPredicate[] row = slice[yIndex];
                            if (row != null) {
                                TraceabilityPredicate predicate = row[xIndex];
                                label_check_predicate:
                                if (predicate != null && !predicate.isAir()) {
                                    BlockPos worldPos = this.setActualRelativeOffset(x, y, currentZ, facing, upwardsFacing, isUseMirror)
                                            .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                                    worldState.update(worldPos, predicate);
                                    Item originalItem = null;
                                    BlockState currentState = world.getBlockState(worldPos);
                                    Block currentBlock = currentState.getBlock();

                                    if (currentBlock != Blocks.AIR) {
                                        if (!autoBuildSetting.isReplaceMode() || !autoBuildSetting.getBlocks().contains(currentBlock)) {
                                            placeBlockPos.add(worldPos);
                                            for (var sp : predicate.limited) {
                                                sp.testLimited(worldState);
                                            }
                                            break label_check_predicate;
                                        }
                                        originalItem = currentBlock.asItem();
                                    }

                                    boolean foundCandidate = false;
                                    Block[] candidatesToPlace = new Block[0];

                                    for (var sp : predicate.limited) {
                                        Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get())
                                                .map(info -> info.getBlockState().getBlock())
                                                .toArray(Block[]::new);
                                        Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                        if (placeableCandidates.length > 0 && sp.minLayerCount > 0) {
                                            int currentLayerCount = cacheLayer.getInt(sp);
                                            if (currentLayerCount < sp.minLayerCount &&
                                                    (sp.maxLayerCount == -1 || currentLayerCount < sp.maxLayerCount)) {
                                                cacheLayer.addTo(sp, 1);
                                                candidatesToPlace = placeableCandidates;
                                                foundCandidate = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!foundCandidate) {
                                        for (var sp : predicate.limited) {
                                            Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get())
                                                    .map(info -> info.getBlockState().getBlock())
                                                    .toArray(Block[]::new);
                                            Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                            if (placeableCandidates.length > 0 && sp.minCount > 0) {
                                                int currentCount = cacheGlobal.getInt(sp);
                                                if (currentCount < sp.minCount &&
                                                        (sp.maxCount == -1 || currentCount < sp.maxCount)) {
                                                    cacheGlobal.addTo(sp, 1);
                                                    candidatesToPlace = placeableCandidates;
                                                    foundCandidate = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!foundCandidate) {
                                        for (var sp : predicate.limited) {
                                            Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get())
                                                    .map(info -> info.getBlockState().getBlock())
                                                    .toArray(Block[]::new);
                                            Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                            if (placeableCandidates.length > 0 &&
                                                    (sp.maxLayerCount == -1 || cacheLayer.getOrDefault(sp, Integer.MAX_VALUE) != sp.maxLayerCount) &&
                                                    (sp.maxCount == -1 || cacheGlobal.getOrDefault(sp, Integer.MAX_VALUE) > sp.maxCount)) {
                                                cacheLayer.addTo(sp, 1);
                                                cacheGlobal.addTo(sp, 1);
                                                candidatesToPlace = ArrayUtils.addAll(candidatesToPlace, placeableCandidates);
                                            }
                                        }

                                        for (var sp : predicate.common) {
                                            Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get())
                                                    .map(info -> info.getBlockState().getBlock())
                                                    .toArray(Block[]::new);
                                            Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                            if (placeableCandidates.length > 0) {
                                                candidatesToPlace = ArrayUtils.addAll(candidatesToPlace, placeableCandidates);
                                            }
                                        }
                                    }

                                    List<Block> blocksToPlace = autoBuildSetting.apply(candidatesToPlace, predicate.isSingle());
                                    if (blocksToPlace.isEmpty()) {
                                        break label_check_predicate;
                                    }

                                    if (!autoBuildSetting.isReplaceMode() || originalItem == null ||
                                            !blocksToPlace.get(0).asItem().equals(originalItem)) {
                                        List<ItemStack> itemCandidates = blocksToPlace.stream()
                                                .map(ItemStack::new)
                                                .toList();

                                        Triplet<ItemStack, IItemHandler, Integer> matchedItem = foundItem(player, itemCandidates,
                                                item -> item instanceof BlockItem);
                                        ItemStack stackToPlace = matchedItem.getA();
                                        IItemHandler sourceHandler = matchedItem.getB();
                                        int sourceSlot = matchedItem.getC();

                                        if (stackToPlace != null) {
                                            boolean isCreative = player.isCreative();
                                            if (isCreative || (sourceHandler != null &&
                                                    !sourceHandler.extractItem(sourceSlot, 1, true).isEmpty())) {
                                                BlockState oldState = world.getBlockState(worldPos);
                                                boolean isReplacing = autoBuildSetting.isReplaceMode() && originalItem != null;
                                                if (isReplacing) {
                                                    world.setBlock(worldPos, Blocks.AIR.defaultBlockState(), 2);
                                                }
                                                BlockItem blockItem = (BlockItem) stackToPlace.getItem();
                                                BlockPlaceContext context = new BlockPlaceContext(
                                                        world, player, InteractionHand.MAIN_HAND, stackToPlace,
                                                        BlockHitResult.miss(player.getEyePosition(0), Direction.UP, worldPos));
                                                InteractionResult result = blockItem.place(context);
                                                if (result.consumesAction()) {
                                                    if (!isCreative) {
                                                        ItemStack extracted = sourceHandler.extractItem(sourceSlot, 1, false);
                                                        if (extracted.isEmpty()) {
                                                            world.setBlock(worldPos, isReplacing ? oldState : Blocks.AIR.defaultBlockState(), 3);
                                                            continue;
                                                        }
                                                    }
                                                    if (isReplacing) {
                                                        ItemStack originalStack = new ItemStack(originalItem, 1);
                                                        if (!player.addItem(originalStack)) {
                                                            player.drop(originalStack, false);
                                                        }
                                                    }
                                                    if (world.getBlockEntity(worldPos) instanceof MetaMachine metaMachine) {
                                                        blocks.put(worldPos, metaMachine);
                                                    }
                                                    placeBlockPos.add(worldPos);
                                                } else if (isReplacing) {
                                                    world.setBlock(worldPos, oldState, 3);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        xIndex++;
                    }
                    yIndex++;
                }
                currentZ++;
            }
        }

        Direction controllerFacing = controller.self().getFrontFacing();
        blocks.forEach((pos, machine) -> resetFacing(pos, machine.getBlockState(), controllerFacing,
                (p, dir) -> !placeBlockPos.contains(p.relative(dir)) && machine.isFacingValid(dir),
                newState -> world.setBlock(pos, newState, 18)));
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
        boolean isFlipped = autoBuildSetting.isFlipMode();

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

                        BlockPos pos = setActualRelativeOffset(x, y, z, facing, upwardsFacing, isFlipped)
                                .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
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
            giveItemsToPlayer(player, collectedItems);
        }
    }

    /**
     * 将物品给予玩家，如果物品栏满了则掉落在地上
     */
    private void giveItemsToPlayer(Player player, List<ItemStack> items) {
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

    public static Triplet<ItemStack, IItemHandler, Integer> foundItem(Player player,
                                                                      List<ItemStack> candidates,
                                                                      Predicate<Item> test) {
        ItemStack found = null;
        IItemHandler handler = null;
        int foundSlot = -1;
        if (!player.isCreative()) {
            var foundHandler = getMatchStackWithHandler(candidates,
                    player.getCapability(ForgeCapabilities.ITEM_HANDLER), test);
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
                                                                        Predicate<Item> test) {
        IItemHandler handler = cap.resolve().orElse(null);
        if (handler == null) return null;
        for (int i = 0; i < handler.getSlots(); i++) {
            @NotNull
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            @NotNull
            LazyOptional<IItemHandler> stackCap = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (stackCap.isPresent()) {
                var rt = getMatchStackWithHandler(candidates, stackCap, test);
                if (rt != null) return rt;
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
