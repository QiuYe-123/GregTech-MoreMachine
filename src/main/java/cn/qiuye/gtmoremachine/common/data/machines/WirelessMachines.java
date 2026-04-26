package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
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
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;

@GTMMDataGeneratorScanned
public class WirelessMachines {

    private static final String WIRELESS_ENERGY_MONITOR_PREFIX = "gtmoremachine.machine.wireless_energy_monitor";
    private static final String WIRELESS_CWU_MONITOR_PREFIX = "gtmoremachine.machine.wireless_cwu_monitor";
    private static final String WIRELESS_MONITOR_PREFIX = "gtmoremachine.machine.wireless_monitor";
    private static final String WIRELESS_ENERGY_HATCH_PREFIX = "gtmoremachine.machine.wireless_energy_hatch";
    private static final String WIRELESS_ENERGY_INTERFACE_PREFIX = "gtmoremachine.machine.wireless_energy_interface";
    private static final String WIRELESS_ENERGY_COVER_PREFIX = "gtmoremachine.machine.wireless_energy_cover";
    private static final String WIRELESS_COMPUTATION_TRANSMITTER_PREFIX = "gtmoremachine.machine.wireless_computation_transmitter_hatch";
    private static final String WIRELESS_COMPUTATION_RECEIVER_PREFIX = "gtmoremachine.machine.wireless_computation_receiver_hatch";
    @GTMMRegisterLanguage(en = "You can monitor the total energy and useage.", cn = "监控无线能源的总量和使用量")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip";
    @GTMMRegisterLanguage(en = "Total Energy: %s EU (%s A %s§r)", cn = "能源总量：%s EU (%s A %s§r)")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_1 = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Single Transfer Limit：%s EU/t (%s A %s§r)", cn = "单次传输上限：%s EU/t (%s A %s§r)")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_2 = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Last minute: §a%s EU/t (%s A %s§r)", cn = "近一分钟：§a%s EU/t (%s A %s§r)")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_LAST_MINUTE = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.last_minute";
    @GTMMRegisterLanguage(en = "Last hour: §a%s EU/t (%s A %s§r)", cn = "近一小时：§a%s EU/t (%s A %s§r)")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_LAST_HOUR = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.last_hour";
    @GTMMRegisterLanguage(en = "Last day: §a%s EU/t (%s A %s§r)", cn = "近一天：§a%s EU/t (%s A %s§r)")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_LAST_DAY = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.last_day";
    @GTMMRegisterLanguage(en = "Now: §a%s EU/t (%sA %s§r)", cn = "当前：§a%s EU/t (%s A %s§r)")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_NOW = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.now";
    @GTMMRegisterLanguage(en = "You will never be satisfied with this for the rest of your life", cn = "你一辈子都充不满")
    public static final String WIRELESS_ENERGY_MONITOR_TOOLTIP_TIME_TO_FILL = WIRELESS_ENERGY_MONITOR_PREFIX + ".tooltip.time_to_fill";
    @GTMMRegisterLanguage(en = "Average Net Power: %s", cn = "平均净功率：%s")
    public static final String WIRELESS_MONITOR_TOOLTIP_NET_POWER = WIRELESS_MONITOR_PREFIX + ".tooltip.net_power";
    @GTMMRegisterLanguage(en = "Average Net CWU: %s", cn = "平均净CWU：%s")
    public static final String WIRELESS_MONITOR_TOOLTIP_NET_CWU = WIRELESS_MONITOR_PREFIX + ".tooltip.net_cwu";
    @GTMMRegisterLanguage(en = "Electricity Statistics：%s     Display Format：%s    Power status：%s\nSorting rules：%s        Type：%s", cn = "用电统计：%s     显示格式：%s    功率状态：%s\n排序规则：%s        类别: %s")
    public static final String WIRELESS_MONITOR_TOOLTIP_STATISTICS_ENERGY = WIRELESS_MONITOR_PREFIX + ".tooltip.statistics.energy";
    @GTMMRegisterLanguage(en = "CWU Statistics：%s     Display Format：%s    CWU status：%s", cn = "算力统计：%s     显示格式：%s    算力状态：%s")
    public static final String WIRELESS_MONITOR_TOOLTIP_STATISTICS_CWU = WIRELESS_MONITOR_PREFIX + ".tooltip.statistics.cwu";
    @GTMMRegisterLanguage(en = "Total CWU: %s CWU", cn = "CWU总量：%s CWU")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_1 = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Last minute: %s CWU", cn = "近一分钟：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_LAST_MINUTE = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.last_minute";
    @GTMMRegisterLanguage(en = "Last hour: %s CWU", cn = "近一小时：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_LAST_HOUR = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.last_hour";
    @GTMMRegisterLanguage(en = "Last day: %s CWU", cn = "近一天：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_LAST_DAY = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.last_day";
    @GTMMRegisterLanguage(en = "Now: %s CWU", cn = "当前：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_NOW = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.now";
    @GTMMRegisterLanguage(en = "Receives energy and sends it to the power network", cn = "接收能量并发送至电网")
    public static final String WIRELESS_ENERGY_INTERFACE_TOOLTIP = WIRELESS_ENERGY_INTERFACE_PREFIX + ".tooltip";
    @GTMMRegisterLanguage(en = "Bind to: %s", cn = "成功绑定至：%s")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_BIND = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.bind";
    @GTMMRegisterLanguage(en = "Unbind!", cn = "解除绑定成功")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_UNBIND = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.unbind";
    @GTMMRegisterLanguage(en = "No owner.", cn = "未绑定所有者")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_1 = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Bind to: %s", cn = "已绑定至：%s")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_2 = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Bind to unknow user: %s", cn = "已绑定至未知用户：%s")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_3 = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "The Wireless Energy Reciver unbind!", cn = "无线能源接收器未绑定所有者")
    public static final String WIRELESS_ENERGY_COVER_TOOLTIP_1 = WIRELESS_ENERGY_COVER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "The Wireless Energy Reciver bind to: %s", cn = "无线能源接收器已绑定至：%s")
    public static final String WIRELESS_ENERGY_COVER_TOOLTIP_2 = WIRELESS_ENERGY_COVER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "The Wireless Energy Reciver bind to unknow user: %s", cn = "无线能源接收器已绑定至未知用户：%s")
    public static final String WIRELESS_ENERGY_COVER_TOOLTIP_3 = WIRELESS_ENERGY_COVER_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "Output computational power data from the multiblock structure.", cn = "从多方块结构输出算力数据")
    public static final String WIRELESS_COMPUTATION_TRANSMITTER_HATCH_TOOLTIP_1 = WIRELESS_COMPUTATION_TRANSMITTER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Need to bind the wireless computational power target Hatch and the wireless computational power source Hatch by right-clicking with a flash memory.", cn = "需要使用闪存右键无线算力靶仓和无线算力源仓进行绑定。")
    public static final String WIRELESS_COMPUTATION_TRANSMITTER_HATCH_TOOLTIP_2 = WIRELESS_COMPUTATION_TRANSMITTER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Input computational power data for the multiblock structure", cn = "为多方块结构输入算力数据")
    public static final String WIRELESS_COMPUTATION_RECEIVER_HATCH_TOOLTIP_1 = WIRELESS_COMPUTATION_RECEIVER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Need to bind the wireless computational power target Hatch and the wireless computational power source Hatch by right-clicking with a flash memory.", cn = "需要使用闪存右键无线算力靶仓和无线算力源仓进行绑定。")
    public static final String WIRELESS_COMPUTATION_RECEIVER_HATCH_TOOLTIP_2 = WIRELESS_COMPUTATION_RECEIVER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Multiblock Sharing §4Disabled", cn = "多方块结构共享：§4禁止")
    public static final String UNIVERSAL_DISABLED = "gtmoremachine.universal.disabled";

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.LV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV);
    public static final int[] WIRELL_ENERGY_HIGH_TIERS = GTValues.tiersBetween(GTValues.EV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV);

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static final MachineDefinition WIRELESS_CWU_MONITOR = GTMM
            .machine("wireless_cwu_monitor", WirelessCWUMonitor::new)
            .langValue("Wireless CWU Monitor")
            .rotationState(RotationState.NON_Y_AXIS)
            .workableTieredHullModel(GTmm.id("block/machines/wireless_monitor"))
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition WIRELESS_ENERGY_MONITOR = GTMM
            .machine("wireless_energy_monitor", WirelessEnergyMonitor::new)
            .langValue("Wireless Energy Monitor")
            .rotationState(RotationState.NON_Y_AXIS)
            .workableTieredHullModel(GTmm.id("block/machines/wireless_monitor"))
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition WIRELESS_ENERGY_INTERFACE = GTMM
            .machine("wireless_energy_interface", WirelessEnergyInterface::new)
            .langValue("Wireless Energy Interface")
            .rotationState(RotationState.ALL)
            .overlayTieredHullModel("energy_input_hatch")
            .tier(GTValues.IV)
            .register();

    public static final MachineDefinition WIRELESS_COMPUTATION_HATCH_TRANSMITTER = GTMM
            .machine("wireless_computation_transmitter_hatch", (holder) -> new WirelessCWUHatchMachine(holder, true))
            .langValue("Wireless Computation Data Reception Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .tooltips(Component.translatable(WIRELESS_COMPUTATION_TRANSMITTER_HATCH_TOOLTIP_1),
                    Component.translatable(WIRELESS_COMPUTATION_TRANSMITTER_HATCH_TOOLTIP_2))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition WIRELESS_COMPUTATION_HATCH_RECEIVER = GTMM
            .machine("wireless_computation_receiver_hatch", (holder) -> new WirelessCWUHatchMachine(holder, false))
            .langValue("Wireless Computation Data Transmission Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .tooltips(Component.translatable(WIRELESS_COMPUTATION_TRANSMITTER_HATCH_TOOLTIP_1),
                    Component.translatable(WIRELESS_COMPUTATION_TRANSMITTER_HATCH_TOOLTIP_2))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_RECEPTION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition WIRELESS_CWU_HATCH_PART_TRANSMITTER = GTMM
            .machine("wireless_cwu_transmitter_hatch", (holder) -> new WirelessCWUHatchPartMachine(holder, true))
            .langValue("Wireless Computation Data Reception Hatch")
            .overlayTieredHullModel("computation_data_hatch")
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION)
            .tier(GTValues.UV)
            .register();

    public static final MachineDefinition WIRELESS_CWU_HATCH_PART_RECEIVER = GTMM
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
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_262144A = registerWirelessLaserHatch(IO.OUT, 262144, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_1048576A = registerWirelessLaserHatch(IO.OUT, 1048576, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_4194304A = registerWirelessLaserHatch(IO.OUT, 4194304, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16777216A = registerWirelessLaserHatch(IO.OUT, 16777216, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_33554432A = registerWirelessLaserHatch(IO.OUT, 33554432, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);
    public static final MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_67108863A = registerWirelessLaserHatch(IO.OUT, 67108863, PartAbility.OUTPUT_LASER, WIRELL_ENERGY_HIGH_TIERS);

    public static void init() {}
}
