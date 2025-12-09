package cn.qiuye.gtmoremachine.common.item

import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer
import cn.qiuye.gtmoremachine.config.GTMMConfig

import com.gregtechceu.gtceu.api.GTValues
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper
import com.gregtechceu.gtceu.api.item.component.IInteractionItem
import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.common.machine.electric.BatteryBufferMachine
import com.gregtechceu.gtceu.common.machine.multiblock.electric.PowerSubstationMachine

import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter

import java.math.BigInteger

class WirelessEnergyBindingToolBehavior : IInteractionItem {
    override fun onItemUseFirst(stack: ItemStack?, context: UseOnContext): InteractionResult {
        if (context.level.isClientSide) return InteractionResult.PASS
        if (!GTMMConfig.INSTANCE.isWirelessRateEnable) return InteractionResult.PASS

        val pos = context.clickedPos
        val rate = getRate(context.level, pos)
        if (rate <= BigInteger.ZERO) return InteractionResult.PASS

        val container = WirelessEnergyContainer.getOrCreateContainer(context.player!!.uuid)
        container.setRate(rate)
        container.setBindPos(GlobalPos.of(context.level.dimension(), pos))

        context.player?.sendSystemMessage(
            Component.translatable(
                "item.gtmoremachine.wireless_transfer.tooltip.bind.1",
                Component.translatable(context.level.getBlockState(pos).block.descriptionId),
                pos.toShortString(),
            ),
        )
        return InteractionResult.CONSUME
    }

    companion object {
        fun getRate(level: BlockGetter?, pos: BlockPos): BigInteger {
            val machine = level?.let { MetaMachine.getMachine(it, pos) } ?: return BigInteger.ZERO

            return when (machine) {
                is BatteryBufferMachine -> calculateBatteryRate(machine)
                is PowerSubstationMachine -> if (machine.isFormed) {
                    machine.energyInfo.capacity() /
                        BigInteger.valueOf(4096)
                } else {
                    BigInteger.ZERO
                }
                else -> BigInteger.ZERO
            }
        }

        private fun calculateBatteryRate(machine: BatteryBufferMachine): BigInteger {
            val inv = machine.batteryInventory
            var rate = BigInteger.ZERO
            for (i in 0 until inv.slots) {
                GTCapabilityHelper.getElectricItem(inv.getStackInSlot(i))?.let {
                    rate += BigInteger.valueOf(GTValues.VEX[it.tier])
                }
            }
            return rate
        }
    }
}
