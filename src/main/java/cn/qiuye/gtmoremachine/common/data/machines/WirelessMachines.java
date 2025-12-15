package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine.WirelessMultiMachines;
import cn.qiuye.gtmoremachine.common.machine.electric.WirelessCWUMonitor;
import cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyInterface;
import cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyMonitor;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessCWUHatchMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessCWUHatchPartMachine;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import net.minecraft.network.chat.Component;

import static cn.qiuye.gtmoremachine.common.data.machines.utils.WirelessMachinesUtils.registerWirelessEnergyHatch;
import static cn.qiuye.gtmoremachine.common.data.machines.utils.WirelessMachinesUtils.registerWirelessLaserHatch;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class WirelessMachines {

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.LV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV);
    public static final int[] WIRELL_ENERGY_HIGH_TIERS = GTValues.tiersBetween(GTValues.EV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV);

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static final MachineDefinition WIRELESS_CWU_MONITOR = GTMMREGISTRATE
            .machine("wireless_cwu_monitor", WirelessCWUMonitor::new)
            .langValue("Wireless CWU Monitor")
            .rotationState(RotationState.NON_Y_AXIS)
            .workableTieredHullModel(GTmm.id("block/machines/wireless_monitor"))
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition WIRELESS_ENERGY_MONITOR = GTMMREGISTRATE
            .machine("wireless_energy_monitor", WirelessEnergyMonitor::new)
            .langValue("Wireless Energy Monitor")
            .rotationState(RotationState.NON_Y_AXIS)
            .workableTieredHullModel(GTmm.id("block/machines/wireless_monitor"))
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition WIRELESS_ENERGY_INTERFACE = GTMMREGISTRATE
            .machine("wireless_energy_interface", WirelessEnergyInterface::new)
            .langValue("Wireless Energy Interface")
            .rotationState(RotationState.ALL)
            .overlayTieredHullModel("energy_input_hatch")
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition WIRELESS_COMPUTATION_HATCH_TRANSMITTER = GTMMREGISTRATE
            .machine("wireless_computation_transmitter_hatch", (holder) -> new WirelessCWUHatchMachine(holder, true))
            .langValue("Wireless Computation Data Reception Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .tooltips(Component.translatable("gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.1"),
                    Component.translatable("gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.2"))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition WIRELESS_COMPUTATION_HATCH_RECEIVER = GTMMREGISTRATE
            .machine("wireless_computation_receiver_hatch", (holder) -> new WirelessCWUHatchMachine(holder, false))
            .langValue("Wireless Computation Data Transmission Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .tooltips(Component.translatable("gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.1"),
                    Component.translatable("gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.2"))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_RECEPTION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition WIRELESS_CWU_HATCH_PART_TRANSMITTER = GTMMREGISTRATE
            .machine("wireless_cwu_transmitter_hatch", (holder) -> new WirelessCWUHatchPartMachine(holder, true))
            .langValue("Wireless Computation Data Reception Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition WIRELESS_CWU_HATCH_PART_RECEIVER = GTMMREGISTRATE
            .machine("wireless_cwu_receiver_hatch", (holder) -> new WirelessCWUHatchPartMachine(holder, false))
            .langValue("Wireless Computation Data Transmission Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_RECEPTION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH = registerWirelessEnergyHatch(IO.IN, 2, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_4A = registerWirelessEnergyHatch(IO.IN, 4, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_16A = registerWirelessEnergyHatch(IO.IN, 16, PartAbility.INPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_64A = registerWirelessEnergyHatch(IO.IN, 64, PartAbility.INPUT_ENERGY, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_256A = registerWirelessLaserHatch(IO.IN, 256, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_1024A = registerWirelessLaserHatch(IO.IN, 1024, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_4096A = registerWirelessLaserHatch(IO.IN, 4096, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_16384A = registerWirelessLaserHatch(IO.IN, 16384, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_65536A = registerWirelessLaserHatch(IO.IN, 65536, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_262144A = registerWirelessLaserHatch(IO.IN, 262144, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_1048576A = registerWirelessLaserHatch(IO.IN, 1048576, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_4194304A = registerWirelessLaserHatch(IO.IN, 4194304, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_16777216A = registerWirelessLaserHatch(IO.IN, 16777216, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_33554432A = registerWirelessLaserHatch(IO.IN, 33554432, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_67108863A = registerWirelessLaserHatch(IO.IN, 67108863, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);

    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH = registerWirelessEnergyHatch(IO.OUT, 2, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_4A = registerWirelessEnergyHatch(IO.OUT, 4, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16A = registerWirelessEnergyHatch(IO.OUT, 16, PartAbility.OUTPUT_ENERGY, ALL_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_64A = registerWirelessEnergyHatch(IO.OUT, 64, PartAbility.OUTPUT_ENERGY, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_256A = registerWirelessLaserHatch(IO.OUT, 256, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_1024A = registerWirelessLaserHatch(IO.OUT, 1024, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_4096A = registerWirelessLaserHatch(IO.OUT, 4096, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16384A = registerWirelessLaserHatch(IO.OUT, 16384, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_65536A = registerWirelessLaserHatch(IO.OUT, 65536, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_262144A = registerWirelessLaserHatch(IO.OUT, 262144, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_1048576A = registerWirelessLaserHatch(IO.OUT, 1048576, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_4194304A = registerWirelessLaserHatch(IO.OUT, 4194304, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16777216A = registerWirelessLaserHatch(IO.OUT, 16777216, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_33554432A = registerWirelessLaserHatch(IO.OUT, 33554432, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_67108863A = registerWirelessLaserHatch(IO.OUT, 67108863, PartAbility.INPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);

    public static void init() {
        WirelessMultiMachines.init();
    }
}
