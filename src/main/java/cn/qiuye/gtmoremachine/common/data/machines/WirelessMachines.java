package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyMonitor;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import static cn.qiuye.gtmoremachine.common.data.machines.WirelessMachinesUtils.registerWirelessEnergyHatch;
import static cn.qiuye.gtmoremachine.common.data.machines.WirelessMachinesUtils.registerWirelessLaserHatch;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.api.GTValues.*;

public class WirelessMachines {

    public static final int[] ALL_TIERS = GTValues.tiersBetween(LV, GTCEuAPI.isHighTier() ? MAX : UHV);
    public static final int[] WIRELL_ENERGY_HIGH_TIERS = GTValues.tiersBetween(EV, GTCEuAPI.isHighTier() ? MAX : UHV);

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static final MachineDefinition WIRELESS_ENERGY_MONITOR = GTMMREGISTRATE
            .machine("wireless_energy_monitor", WirelessEnergyMonitor::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .workableTieredHullModel(GTmm.id("block/machines/wireless_energy_monitor"))
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH = registerWirelessEnergyHatch(IO.IN, 2, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_4A = registerWirelessEnergyHatch(IO.IN, 4, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_16A = registerWirelessEnergyHatch(IO.IN, 16, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_64A = registerWirelessEnergyHatch(IO.IN, 64, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_256A = registerWirelessLaserHatch(IO.IN, 256, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_1024A = registerWirelessLaserHatch(IO.IN, 1024, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_4096A = registerWirelessLaserHatch(IO.IN, 4096, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_16384A = registerWirelessLaserHatch(IO.IN, 16384, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_65536A = registerWirelessLaserHatch(IO.IN, 65536, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);

    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH = registerWirelessEnergyHatch(IO.OUT, 2, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_4A = registerWirelessEnergyHatch(IO.OUT, 4, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16A = registerWirelessEnergyHatch(IO.OUT, 16, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_64A = registerWirelessEnergyHatch(IO.OUT, 64, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_256A = registerWirelessLaserHatch(IO.OUT, 256, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_1024A = registerWirelessLaserHatch(IO.OUT, 1024, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_4096A = registerWirelessLaserHatch(IO.OUT, 4096, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16384A = registerWirelessLaserHatch(IO.OUT, 16384, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_65536A = registerWirelessLaserHatch(IO.OUT, 65536, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);

    public static void init() {}
}
