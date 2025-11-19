package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.gregtechceu.gtceu.common.data.GTMachines.CREATIVE_ENERGY;

public class GTMMCreativeModeTabs {

    public static final RegistryEntry<CreativeModeTab> WIRELESS_TAB = GTMMRegistration.GTMMREGISTRATE
            .defaultCreativeTab("wireless", builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("wireless", GTMMRegistration.GTMMREGISTRATE))
                    .title(GTMMRegistration.GTMMREGISTRATE.addLang("itemGroup", GTmm.id("wireless"), GTmm.MOD_NAME))
                    .icon(CREATIVE_ENERGY::asStack)
                    .build())
            .register();

    public static void init() {}
}
