package cn.qiuye.gtmoremachine.common.data.machines.utils;

import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessEnergyHatchPartMachine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class WirelessMachinesUtils {

    public static MachineDefinition[] registerTieredMachines(String name,
                                                             BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                             BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder,
                                                             int... tiers) {
        return CustomMachinesUtils.registerTieredMachines(name, factory, builder, tiers);
    }

    public static MachineDefinition[] registerWirelessEnergyHatch(IO io, int amperage, PartAbility ability, int[] tiers) {
        String voltage = io == IO.IN ? "in" : "out";
        String name = voltage + "put";
        String finalRender = getRender(amperage);
        return registerTieredMachines(amperage + "a_wireless_energy_" + name + "_hatch",
                (holder, tier) -> new WirelessEnergyHatchPartMachine(holder, tier, io, amperage, false),
                (tier, builder) -> builder
                        .langValue(GTValues.VNF[tier] + " " + FormattingUtil.formatNumbers(amperage) + (io == IO.IN ? " Energy Hatch" : " Dynamo Hatch"))
                        .rotationState(RotationState.ALL)
                        .abilities(ability)
                        .tooltips(Component.translatable("gtmoremachine.machine.energy_hatch." + name + ".tooltip"),
                                (Component.translatable("gtmoremachine.machine.wireless_energy_hatch." + name + ".tooltip")),
                                Component.translatable("gtceu.universal.tooltip.voltage_" + voltage,
                                        FormattingUtil.formatNumbers(GTValues.V[tier]), GTValues.VNF[tier]),
                                Component.translatable("gtceu.universal.tooltip.amperage_" + voltage, amperage),
                                Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                        FormattingUtil
                                                .formatNumbers(EnergyHatchPartMachine.getHatchEnergyCapacity(tier, amperage))))
                        .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                        .overlayTieredHullModel(finalRender)
                        .register(),
                tiers);
    }

    public static MachineDefinition[] registerWirelessLaserHatch(IO io, int amperage, PartAbility ability, int[] tiers) {
        String voltage = io == IO.IN ? "in" : "out";
        var name = io == IO.IN ? "target" : "source";
        String finalRender = getRender(amperage);
        return registerTieredMachines(amperage + "a_wireless_laser_" + name + "_hatch",
                (holder, tier) -> new WirelessEnergyHatchPartMachine(holder, tier, io, amperage, true),
                (tier, builder) -> builder
                        .langValue(GTValues.VNF[tier] + " " + FormattingUtil.formatNumbers(amperage) + "A Laser " +
                                FormattingUtil.toEnglishName(name) + " Hatch")
                        .rotationState(RotationState.ALL)
                        .abilities(ability)
                        .tooltips(Component.translatable("gtmoremachine.machine.energy_hatch." + name + ".tooltip"),
                                Component.translatable("gtmoremachine.machine.wireless_energy_hatch." + name + ".tooltip"),
                                Component.translatable("gtceu.universal.tooltip.voltage_" + voltage,
                                        FormattingUtil.formatNumbers(GTValues.V[tier]), GTValues.VNF[tier]),
                                Component.translatable("gtceu.universal.tooltip.amperage_" + voltage, amperage),
                                Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                        FormattingUtil
                                                .formatNumbers(
                                                        EnergyHatchPartMachine.getHatchEnergyCapacity(tier, amperage))))
                        .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                        .overlayTieredHullModel(finalRender)
                        .register(),
                tiers);
    }

    private static @NotNull String getRender(int amperage) {
        String render = "wireless_energy_hatch";
        render = switch (amperage) {
            case 2 -> render;
            case 4 -> render + "_4a";
            case 16 -> render + "_16a";
            case 64 -> render + "_64a";
            default -> "wireless_laser_hatch";
        };
        return render;
    }
}
