package cn.qiuye.gtmoremachine.api.pattern;

import cn.qiuye.gtmoremachine.common.block.BlockMap;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * 高级终端搭建与拆除流程共享的结构匹配辅助方法。
 * Pattern matching helpers shared by the advanced terminal build and demolition flows.
 */
public final class PatternMatchUtils {

    private PatternMatchUtils() {}

    /**
     * 检查当前方块是否可以视为该结构位置的有效拆除目标。
     * Demolition matching is intentionally independent from tier block selection and hatch placement filters.
     *
     * @param currentBlock 当前世界中实际存在的方块 the block currently found in the world
     * @param predicate    当前结构位置对应的谓词 the structure predicate for the current position
     * @return 当前方块是否属于该位置任一允许候选集合 whether the block belongs to any allowed candidate set for this position
     */
    public static boolean matchesDemolitionPredicate(Block currentBlock, TraceabilityPredicate predicate) {
        for (var sp : predicate.common) {
            if (matchesDemolitionCandidates(currentBlock, sp.candidates == null ? null : Arrays.stream(sp.candidates.get())
                    .map(info -> info.getBlockState().getBlock())
                    .toArray(Block[]::new))) {
                return true;
            }
        }
        for (var sp : predicate.limited) {
            if (matchesDemolitionCandidates(currentBlock, sp.candidates == null ? null : Arrays.stream(sp.candidates.get())
                    .map(info -> info.getBlockState().getBlock())
                    .toArray(Block[]::new))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前方块是否命中某一组候选方块。
     * A match is accepted when the block is exactly the same candidate, or when both blocks belong to the same
     * {@link BlockMap} category such as a tiered hatch or coil family.
     *
     * @param currentBlock 当前世界中实际存在的方块 the block currently found in the world
     * @param candidates   从某一个谓词分支收集到的候选方块 the candidate blocks collected from one predicate branch
     * @return 当前方块是否命中该候选集合 whether the block matches the candidate set
     */
    public static boolean matchesDemolitionCandidates(Block currentBlock, @Nullable Block[] candidates) {
        if (candidates == null || candidates.length == 0) {
            return false;
        }
        String currentCategory = BlockMap.getCategory(currentBlock);
        for (Block candidate : candidates) {
            if (candidate == currentBlock) {
                return true;
            }
            if (currentCategory != null && currentCategory.equals(BlockMap.getCategory(candidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 直接从区块分段中移除方块，同时保留常规的方块移除回调。
     * This is used by demolition mode for blocks like liquids that cannot be handled through normal placement logic.
     *
     * @param level 包含目标方块的世界 the level containing the block
     * @param pos   需要清除的方块坐标 the block position to clear
     */
    public static void forceRemoveBlock(Level level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) return;

        LevelChunk chunk = level.getChunkAt(pos);
        int y = pos.getY();
        int sectionIndex = chunk.getSectionIndex(y);
        LevelChunkSection section = chunk.getSections()[sectionIndex];
        if (section.hasOnlyAir()) return;

        int localX = pos.getX() & 15;
        int localY = y & 15;
        int localZ = pos.getZ() & 15;

        BlockState oldState = section.getBlockState(localX, localY, localZ);
        if (oldState.isAir()) return;

        section.setBlockState(localX, localY, localZ, Blocks.AIR.defaultBlockState());
        oldState.onRemove(level, pos, Blocks.AIR.defaultBlockState(), false);

        if (oldState.hasBlockEntity()) {
            level.removeBlockEntity(pos);
        }

        chunk.setUnsaved(true);
    }
}
