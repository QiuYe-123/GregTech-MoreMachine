package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.machines.WirelessMachines;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class GTMMCreativeModeTabs {

    public static final RegistryEntry<CreativeModeTab> CREATIVE_TAB = GTMMREGISTRATE
            .defaultCreativeTab("creative", builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("creative", GTMMREGISTRATE))
                    .title(GTMMREGISTRATE.addLang("itemGroup", GTmm.id("creative"), "Creative Things"))
                    .icon(GTMachines.CREATIVE_ENERGY::asStack)
                    .build())
            .register();

    public static final RegistryEntry<CreativeModeTab> WIRELESS_TAB = GTMMREGISTRATE
            .defaultCreativeTab("wireless", builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("wireless", GTMMREGISTRATE))
                    .title(GTMMREGISTRATE.addLang("itemGroup", GTmm.id("wireless"), "Gregtech Wireless"))
                    .icon(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH[GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV - 1]::asStack)
                    .build())
            .register();

    public static final RegistryEntry<CreativeModeTab> MORE_MACHINES = GTMMREGISTRATE
            .defaultCreativeTab("more_machines", builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("more_machines", GTMMREGISTRATE))
                    .title(GTMMREGISTRATE.addLang("itemGroup", GTmm.id("more_machines"), "More Machines"))
                    .icon(GTBlocks.BATTERY_EMPTY_TIER_II::asStack)
                    .build())
            .register();

    public static void init() {}
}
