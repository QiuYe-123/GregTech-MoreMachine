package cn.qiuye.gtmoremachine.api.pattern

import cn.qiuye.gtmoremachine.api.GTMMAPI

import com.gregtechceu.gtceu.api.pattern.MultiblockState
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError

import com.lowdragmc.lowdraglib.utils.BlockInfo

import net.minecraft.network.chat.Component

import java.util.function.Predicate

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
                        val currentFilter = blockWorldState.matchContext.getOrPut("WECCType", stats)
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
            GTMMAPI.WECC.values.stream()
                .map { blockSupplier -> BlockInfo.fromBlockState(blockSupplier.get().defaultBlockState()) }
                .toArray { size -> arrayOfNulls<BlockInfo>(size) }
        }
            .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.filters"))
    }
}
