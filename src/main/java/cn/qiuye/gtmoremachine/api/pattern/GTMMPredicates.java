package cn.qiuye.gtmoremachine.api.pattern;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.ICCData;
import cn.qiuye.gtmoremachine.common.block.WECCBlock;
import cn.qiuye.gtmoremachine.common.machine.multiblock.electric.DemodulationHubMachine;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;

@GTMMDataGeneratorScanned
public class GTMMPredicates {

    private static final String PATTERN_ERROR_PREFIX = "gtmoremachine.multiblock.pattern.error";
    @GTMMRegisterLanguage(en = "§cAll heating Energy Communication Unit must be the same", cn = "§c必须使用同种能源通讯单元")
    private static final String ERROR_ECUTYPES = PATTERN_ERROR_PREFIX + ".ecutypes";
    @GTMMRegisterLanguage(en = "§cAll heating Wireless Energy Capacity Component must be the same", cn = "§c必须使用相同的电网容量组件")
    private static final String ERROR_WECC = PATTERN_ERROR_PREFIX + ".wecc";

    public static TraceabilityPredicate EnergyCommunicationUnit() {
        return new TraceabilityPredicate(blockWorldState -> {
            BlockState state = blockWorldState.getBlockState();
            for (var entry : GTMMAPI.ECU.entrySet()) {
                if (state.is(entry.getValue().get())) {
                    var ecu = entry.getKey();
                    Object currentCoil = blockWorldState.getMatchContext().getOrPut("ECUType", ecu);
                    if (!currentCoil.equals(ecu)) {
                        blockWorldState.setError(new PatternStringError(ERROR_ECUTYPES));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> GTMMAPI.ECU.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> new BlockInfo(entry.getValue().get().defaultBlockState(), null))
                .toArray(BlockInfo[]::new)).addTooltips(Component.translatable(ERROR_ECUTYPES));
    }

    public static TraceabilityPredicate WirelessEnergyCapacityComponent() {
        return new TraceabilityPredicate(blockWorldState -> {
            BlockState state = blockWorldState.getBlockState();
            for (Map.Entry<ICCData, Supplier<WECCBlock>> entry : GTMMAPI.WECC.entrySet()) {
                if (state.is(entry.getValue().get())) {
                    ICCData wecc = entry.getKey();
                    if (wecc.getTier() != -1 && wecc.getCapacity().compareTo(BigInteger.ZERO) > 0) {
                        String key = DemodulationHubMachine.CAPACITY_COMPONENT_HEADER + wecc.getCCName();
                        DemodulationHubMachine.ComponentMatchWrapper wrapper = blockWorldState.getMatchContext().get(key);
                        if (wrapper == null) wrapper = new DemodulationHubMachine.ComponentMatchWrapper(wecc);
                        blockWorldState.getMatchContext().set(key, wrapper.increment());
                    }
                    return true;
                }
            }
            return false;
        }, () -> GTMMAPI.WECC.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                .map(entry -> new BlockInfo(entry.getValue().get().defaultBlockState(), null))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable(ERROR_WECC));
    }
}
