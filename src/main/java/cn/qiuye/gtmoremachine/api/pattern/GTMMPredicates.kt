package cn.qiuye.gtmoremachine.api.pattern

import cn.qiuye.gtmoremachine.api.GTMMAPI
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.common.block.CapacityComponentBlock
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DimensionalRelayNodeMachine
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DimensionalRelayNodeMachine.ComponentMatchWrapper

import com.gregtechceu.gtceu.api.pattern.MultiblockState
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError

import com.lowdragmc.lowdraglib.utils.BlockInfo

import net.minecraft.network.chat.Component

import java.math.BigInteger
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.ToIntFunction

@Suppress("FunctionName")
object GTMMPredicates {

    @JvmStatic
    fun EnergyCommunicationUnit(): TraceabilityPredicate {
        return TraceabilityPredicate(
            Predicate { blockWorldState: MultiblockState ->
                val blockState = blockWorldState.getBlockState()
                for (entry in GTMMAPI.ECU.entries) {
                    if (blockState.`is`(entry.value.get())) {
                        val stats = entry.key
                        val currentFilter = blockWorldState.matchContext.getOrPut("ECUType", stats)
                        if (currentFilter != stats) {
                            blockWorldState.setError(PatternStringError("gtceu.multiblock.pattern.error.filters"))
                            return@Predicate false
                        }
                        return@Predicate true
                    }
                }
                false
            },
        ) {
            GTMMAPI.ECU.values.stream()
                .map { blockSupplier -> BlockInfo.fromBlockState(blockSupplier.get().defaultBlockState()) }
                .toArray { size -> arrayOfNulls<BlockInfo>(size) }
        }
            .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.filters"))
    }

    @JvmStatic
    fun WirelessEnergyCapacityComponent(): TraceabilityPredicate {
        return TraceabilityPredicate(
            Predicate { blockWorldState: MultiblockState ->
                val blockState = blockWorldState.getBlockState()
                for (entry in GTMMAPI.WECC.entries) {
                    if (blockState.`is`(entry.value.get())) {
                        val stats = entry.key
                        if (stats.tier != -1 && stats.capacity > BigInteger.ZERO) {
                            val key =
                                DimensionalRelayNodeMachine.CAPACITY_COMPONENT_HEADER +
                                    stats.capacityComponentName
                            var wrapper =
                                blockWorldState.matchContext.get<ComponentMatchWrapper>(
                                    key,
                                )
                            if (wrapper != null) wrapper = ComponentMatchWrapper(stats)
                            blockWorldState.matchContext.set(key, wrapper.increment())
                        }
                        return@Predicate true
                    }
                }
                false
            },
        ) {
            GTMMAPI.WECC.entries.stream()
                .sorted(
                    Comparator.comparingInt(
                        ToIntFunction {
                                entry: MutableMap.MutableEntry<
                                    ICapacityComponentData,
                                    Supplier<CapacityComponentBlock>,
                                    >,
                            ->
                            entry.key.getTier()
                        },
                    ),
                )
                .map {
                        entry: MutableMap.MutableEntry<
                            ICapacityComponentData,
                            Supplier<CapacityComponentBlock>,
                            >,
                    ->
                    BlockInfo(entry.value.get().defaultBlockState(), null)
                }
                .toArray<BlockInfo> { size ->
                    arrayOfNulls<BlockInfo>(size)
                }
        }
            .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.filters"))
    }
}
