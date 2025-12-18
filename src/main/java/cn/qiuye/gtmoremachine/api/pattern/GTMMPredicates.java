package cn.qiuye.gtmoremachine.api.pattern;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DemodulationHubMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DemodulationHubMachine.ComponentMatchWrapper;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;
import java.util.Comparator;

public class GTMMPredicates {

    public static TraceabilityPredicate EnergyCommunicationUnit() {
        return new TraceabilityPredicate(blockWorldState -> {
            BlockState state = blockWorldState.getBlockState();
            for (var entry : GTMMAPI.ECU.entrySet()) {
                if (state.is(entry.getValue().get())) {
                    var ecu = entry.getKey();
                    Object currentCoil = blockWorldState.getMatchContext().getOrPut("ECUType", ecu);
                    if (!currentCoil.equals(ecu)) {
                        blockWorldState.setError(new PatternStringError("gtmoremachine.multiblock.pattern.error.ecutypes"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> GTMMAPI.ECU.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> new BlockInfo(entry.getValue().get().defaultBlockState(), null))
                .toArray(BlockInfo[]::new)).addTooltips(Component.translatable("gtceu.multiblock.pattern.error.batteries"));
    }

    public static TraceabilityPredicate WirelessEnergyCapacityComponent() {
        return new TraceabilityPredicate(blockWorldState -> {
            BlockState state = blockWorldState.getBlockState();
            for (var entry : GTMMAPI.WECC.entrySet()) {
                if (state.is(entry.getValue().get())) {
                    var wecc = entry.getKey();
                    if (wecc.getTier() != -1 && wecc.getCapacity().compareTo(BigInteger.ZERO) > 0) {
                        String key = DemodulationHubMachine.CAPACITY_COMPONENT_HEADER + wecc.getCapacityComponentName();
                        ComponentMatchWrapper wrapper = blockWorldState.getMatchContext().get(key);
                        if (wrapper == null) wrapper = new ComponentMatchWrapper(wecc);
                        blockWorldState.getMatchContext().set(key, wrapper.increment());
                    }
                    return true;
                }
            }
            return false;
        }, () -> GTMMAPI.WECC.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> new BlockInfo(entry.getValue().get().defaultBlockState(), null))
                .toArray(BlockInfo[]::new)).addTooltips(Component.translatable("gtmoremachine.multiblock.pattern.error.wecc"));
    }
}
