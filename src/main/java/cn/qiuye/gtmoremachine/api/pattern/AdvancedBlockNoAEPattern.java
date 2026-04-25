package cn.qiuye.gtmoremachine.api.pattern;

import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IDropSaveMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

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
            Field blockMatchesField = clazz.getDeclaredField("blockMatches");
            blockMatchesField.setAccessible(true);
            TraceabilityPredicate[][][] blockMatches = (TraceabilityPredicate[][][]) blockMatchesField.get(blockPattern);
            Field centerOffsetField = clazz.getDeclaredField("centerOffset");
            centerOffsetField.setAccessible(true);
            int[] centerOffset = (int[]) centerOffsetField.get(blockPattern);

            return new AdvancedBlockNoAEPattern(blockMatches, blockPattern.structureDir, blockPattern.aisleRepetitions, centerOffset);
        } catch (Exception ignored) {}
        return null;
    }

    public void autoBuild(Player player, MultiblockState worldState,
                          AdvancedTerminalBehavior.AutoBuildSetting autoBuildSetting) {
        Level worldlevel = player.level();
        int minZ = -centerOffset[4];
        worldState.clean();
        MultiblockControllerMachine controller = worldState.getController();
        BlockPos centerPos = controller.getBlockPos();
        Direction facing = controller.getFrontFacing();
        Direction upwardsFacing = controller.getUpwardsFacing();
        Object2IntOpenHashMap<SimplePredicate> cacheGlobal = worldState.getGlobalCount();
        Object2IntOpenHashMap<SimplePredicate> cacheLayer = worldState.getLayerCount();
        Long2ObjectOpenHashMap<MetaMachine> blocks = new Long2ObjectOpenHashMap<>();
        LongOpenHashSet posset = new LongOpenHashSet(1024, 0.5f);

        int[] repeat = new int[this.fingerLength];
        for (int h = 0; h < this.fingerLength; h++) {
            var minH = this.aisleRepetitions[h][0];
            var maxH = this.aisleRepetitions[h][1];
            if (minH != maxH) {
                repeat[h] = Math.max(minH, Math.min(maxH, autoBuildSetting.getRepeatCount()));
            } else repeat[h] = minH;
        }

        List<ItemEntity> itemStacks = new ArrayList<>();

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
                                    var worldPos = this.setActualRelativeOffset(x, y, currentZ, facing, upwardsFacing, autoBuildSetting.isFlipMode())
                                            .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                                    worldState.update(worldPos, predicate);
                                    long posLong = worldPos.asLong();
                                    Item originalItem = null;
                                    BlockState currentState = worldlevel.getBlockState(worldPos);
                                    Block currentBlock = currentState.getBlock();

                                    if (currentBlock != Blocks.AIR) {
                                        if (autoBuildSetting.isDemolitionMode()) {
                                            if (predicate.isAny()) {
                                                break label_check_predicate;
                                            }
                                            if (currentBlock instanceof LiquidBlock) {
                                                PatternMatchUtils.forceRemoveBlock(worldlevel, worldPos);
                                                break label_check_predicate;
                                            }

                                            if (worldPos.equals(centerPos)) {
                                                posset.add(posLong);
                                                break label_check_predicate;
                                            }

                                            if (!PatternMatchUtils.matchesDemolitionPredicate(currentBlock, predicate)) {
                                                break label_check_predicate;
                                            }
                                            ItemStack dropStack = new ItemStack(currentBlock);
                                            if (currentState.hasBlockEntity() && worldlevel.getBlockEntity(worldPos) instanceof MetaMachine metaMachine) {
                                                metaMachine.modifyDrops(Collections.singletonList(dropStack));
                                                if (metaMachine instanceof IDropSaveMachine dropSave && dropSave.saveBreak()) {
                                                    dropSave.saveToItem(dropStack.getOrCreateTag());
                                                }
                                            }

                                            if (!player.isCreative() && !player.addItem(dropStack)) {
                                                itemStacks.add(player.drop(dropStack, false));
                                            }
                                            worldlevel.setBlockAndUpdate(worldPos, Blocks.AIR.defaultBlockState());
                                            break label_check_predicate;
                                        }

                                        if (!autoBuildSetting.isReplaceMode() || !autoBuildSetting.getBlocks().contains(currentBlock)) {
                                            posset.add(posLong);
                                            for (var sp : predicate.limited) {
                                                sp.testLimited(worldState);
                                            }
                                            break label_check_predicate;
                                        }
                                        originalItem = currentBlock.asItem();

                                    } else if (autoBuildSetting.isDemolitionMode()) break label_check_predicate;

                                    boolean foundCandidate = false;
                                    Block[] candidatesToPlace = new Block[0];

                                    for (var sp : predicate.limited) {
                                        Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get()).map(i -> i.getBlockState().getBlock()).toArray(Block[]::new);
                                        Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                        if (placeableCandidates.length > 0 && sp.minLayerCount > 0) {
                                            int currentLayerCount = cacheLayer.getInt(sp);
                                            if (currentLayerCount < sp.minLayerCount && (sp.maxLayerCount == -1 || currentLayerCount < sp.maxLayerCount)) {
                                                cacheLayer.addTo(sp, 1);
                                                candidatesToPlace = placeableCandidates;
                                                foundCandidate = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!foundCandidate) {
                                        for (var sp : predicate.limited) {
                                            Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get()).map(i -> i.getBlockState().getBlock()).toArray(Block[]::new);
                                            Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                            if (placeableCandidates.length > 0 && sp.minCount > 0) {
                                                int currentLayerCount = cacheGlobal.getInt(sp);
                                                if (currentLayerCount < sp.minCount && (sp.maxCount == -1 || currentLayerCount < sp.maxCount)) {
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
                                            Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get()).map(i -> i.getBlockState().getBlock()).toArray(Block[]::new);
                                            Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                            if (placeableCandidates.length > 0 && (sp.maxLayerCount == -1 || cacheLayer.getOrDefault(sp, Integer.MAX_VALUE) != sp.maxLayerCount) && (sp.maxCount == -1 || cacheGlobal.getOrDefault(sp, Integer.MAX_VALUE) > sp.maxCount)) {
                                                cacheLayer.addTo(sp, 1);
                                                cacheGlobal.addTo(sp, 1);
                                                candidatesToPlace = ArrayUtils.addAll(candidatesToPlace, placeableCandidates);
                                            }
                                        }

                                        for (var sp : predicate.common) {
                                            Block[] candidates = sp.candidates == null ? null : Arrays.stream(sp.candidates.get()).map(i -> i.getBlockState().getBlock()).toArray(Block[]::new);
                                            Block[] placeableCandidates = autoBuildSetting.getPlaceableCandidates(candidates, predicate.isSingle());
                                            if (placeableCandidates.length > 0) {
                                                candidatesToPlace = ArrayUtils.addAll(candidatesToPlace, placeableCandidates);
                                            }
                                        }
                                    }
                                    List<Block> stacks = autoBuildSetting.apply(candidatesToPlace);
                                    if (stacks.isEmpty()) {
                                        break label_check_predicate;
                                    }
                                    if (!autoBuildSetting.isReplaceMode() || originalItem == null || !stacks.get(0).asItem().equals(originalItem)) {

                                        List<ItemStack> itemCandidates = stacks.stream()
                                                .map(ItemStack::new)
                                                .toList();

                                        Triplet<ItemStack, IItemHandler, Integer> foundItem = findItemFromPlayer(player, itemCandidates);
                                        ItemStack stackToPlace = foundItem.getA();
                                        IItemHandler sourceHandler = foundItem.getB();
                                        int sourceSlot = foundItem.getC();

                                        if (stackToPlace != null) {
                                            boolean isCreative = player.isCreative();
                                            if (isCreative || (sourceHandler != null && !sourceHandler.extractItem(sourceSlot, 1, true).isEmpty())) {
                                                BlockState oldState = worldlevel.getBlockState(worldPos);
                                                boolean isReplacing = autoBuildSetting.isReplaceMode() && originalItem != null;
                                                if (isReplacing) {
                                                    worldlevel.setBlock(worldPos, Blocks.AIR.defaultBlockState(), 2);
                                                }
                                                BlockItem blockItem = (BlockItem) stackToPlace.getItem();
                                                BlockPlaceContext context = new BlockPlaceContext(
                                                        worldlevel, player, InteractionHand.MAIN_HAND, stackToPlace,
                                                        BlockHitResult.miss(player.getEyePosition(0), Direction.UP, worldPos));
                                                InteractionResult result = blockItem.place(context);
                                                if (result.consumesAction()) {
                                                    if (!isCreative) {
                                                        ItemStack extracted = sourceHandler.extractItem(sourceSlot, 1, false);
                                                        if (extracted.isEmpty()) {
                                                            worldlevel.setBlock(worldPos, isReplacing ? oldState : Blocks.AIR.defaultBlockState(), 3);
                                                            break label_check_predicate;
                                                        }
                                                    }
                                                    if (isReplacing) {
                                                        ItemStack originalStack = new ItemStack(originalItem, 1);
                                                        if (!player.addItem(originalStack)) {
                                                            itemStacks.add(player.drop(originalStack, false));
                                                        }
                                                    }
                                                    if (worldlevel.getBlockEntity(worldPos) instanceof MetaMachine metaMachine) {
                                                        blocks.put(posLong, metaMachine);
                                                    }
                                                    posset.add(posLong);
                                                } else if (isReplacing) {
                                                    worldlevel.setBlock(worldPos, oldState, 3);
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

        for (ItemEntity entity : itemStacks) {
            if (entity != null) {
                entity.setNoPickUpDelay();
                entity.setPos(player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                worldlevel.addFreshEntity(entity);
            }
        }

        Direction controllerFacing = controller.self().getFrontFacing();
        blocks.long2ObjectEntrySet().fastForEach(entry -> {
            long posLong = entry.getLongKey();
            MetaMachine machine = entry.getValue();
            BlockPos pos = BlockPos.of(posLong);
            resetFacing(pos, machine.getBlockState(), controllerFacing,
                    (p, dir) -> !posset.contains(p.relative(dir).asLong()) && machine.isFacingValid(dir),
                    newState -> worldlevel.setBlock(pos, newState, 18));
        });
    }

    private static Triplet<ItemStack, IItemHandler, Integer> findItemFromPlayer(Player player, List<ItemStack> candidates) {
        if (!player.isCreative()) {
            IntObjectPair<IItemHandler> result = findItemRecursively(candidates, player.getCapability(ForgeCapabilities.ITEM_HANDLER));
            if (result != null) {
                IItemHandler handler = result.right();
                int slot = result.leftInt();
                return new Triplet<>(handler.getStackInSlot(slot).copy(), handler, slot);
            }
        } else {
            for (ItemStack candidate : candidates) {
                if (!candidate.isEmpty() && candidate.getItem() instanceof BlockItem) {
                    return new Triplet<>(candidate.copy(), null, -1);
                }
            }
        }
        return new Triplet<>(null, null, -1);
    }

    @Nullable
    private static IntObjectPair<IItemHandler> findItemRecursively(List<ItemStack> candidates, LazyOptional<IItemHandler> capability) {
        IItemHandler handler = capability.resolve().orElse(null);
        if (handler == null) return null;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            LazyOptional<IItemHandler> subCap = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (subCap.isPresent()) {
                IntObjectPair<IItemHandler> subResult = findItemRecursively(candidates, subCap);
                if (subResult != null) return subResult;
            } else if (candidates.stream().anyMatch(c -> ItemStack.isSameItemSameTags(c, stack)) &&
                    stack.getItem() instanceof BlockItem) {
                        return IntObjectPair.of(slot, handler);
                    }
        }
        return null;
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
                    c1[0] = -c1[0];
                } else {
                    c1[2] = -c1[2];
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
                        c1[0] = -c1[0];
                    } else c1[2] = -c1[2];
                } else c1[1] = -c1[1];
            }
        }
        return new BlockPos(c1[0], c1[1], c1[2]);
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
