package cn.qiuye.gtmoremachine.api.pattern

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine
import com.gregtechceu.gtceu.api.registry.GTRegistries

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block

object Hatch {

    @JvmStatic
    val BlockSet: Set<Block> = HashSet<Block>().apply {
        GTRegistries.MACHINES.forEach { d ->
            val block = d.block
            val machine = d.createMetaMachine(
                d.blockEntityType.create(BlockPos.ZERO, block.defaultBlockState()) as IMachineBlockEntity,
            )
            if (machine is MultiblockPartMachine) {
                add(block)
            }
        }
    }
}
