package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.block.machine.electric.WirelessEnergyMonitor;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class WirelessMachines {

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static final MachineDefinition WIRELESS_ENERGY_MONITOR = GTMMREGISTRATE
            .machine("wireless_energy_monitor", WirelessEnergyMonitor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .workableTieredHullModel(GTmm.id("block/machines/wireless_energy_monitor"))
            .tier(GTValues.IV)
            .register();

    public static void init() {}
}
