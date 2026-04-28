package cn.qiuye.gtmoremachine.common;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.registries.ScanningClass;
import cn.qiuye.gtmoremachine.common.block.BlockMap;
import cn.qiuye.gtmoremachine.common.data.GTMMBlocks;
import cn.qiuye.gtmoremachine.common.data.GTMMCovers;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.data.GTMMDataComponents;
import cn.qiuye.gtmoremachine.common.data.GTMMItems;
import cn.qiuye.gtmoremachine.common.data.machines.Machines;
import cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine.MultiMachines;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.config.GTMMConfig;
import cn.qiuye.gtmoremachine.data.GTMMDatagen;
import cn.qiuye.gtmoremachine.utils.input.SyncedKeyMappings;
import cn.qiuye.gtmoremachine.utils.input.open.HotKeyActions;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class CommonProxy {

    public static void init(IEventBus eventBus) {
        init();
        GTMMDataComponents.DATA_COMPONENTS.register(eventBus);
        GTMMRegistration.GTMM.registerEventListeners(eventBus);
        eventBus.addListener(CommonProxy::commonSetup);
        eventBus.addListener(CommonProxy::registerCreativeModeTabs);
        eventBus.addListener(CommonProxy::registerBlocks);
        eventBus.addListener(CommonProxy::registerItems);
        eventBus.addListener(CommonProxy::registerRecipeTypes);
        eventBus.addListener(CommonProxy::registerMachines);
        eventBus.addListener(CommonProxy::registerCovers);
    }

    private static void init() {
        GTmm.LOGGER.info("GTMoreMachine common proxy init!");
        ScanningClass.init();
        if (GTmm.isDev()) {
            GTMMConfig.INSTANCE.isWirelessDimensionRateEnable = true;
            GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable = true;
        }
        GTMMDatagen.initPost();
        SyncedKeyMappings.init();
        HotKeyActions.init();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        BlockMap.build();
    }

    private static void registerCreativeModeTabs(RegisterEvent event) {
        if (event.getRegistryKey() != Registries.CREATIVE_MODE_TAB) {
            return;
        }
        GTMMCreativeModeTabs.init();
    }

    private static void registerBlocks(RegisterEvent event) {
        if (event.getRegistryKey() != Registries.BLOCK) {
            return;
        }
        GTMMBlocks.init();
    }

    private static void registerItems(RegisterEvent event) {
        if (event.getRegistryKey() != Registries.ITEM) {
            return;
        }
        GTMMItems.init();
    }

    private static void registerRecipeTypes(RegisterEvent event) {
        if (event.getRegistryKey() != GTRegistries.RECIPE_TYPE_REGISTRY) {
            return;
        }
    }

    private static void registerMachines(RegisterEvent event) {
        if (event.getRegistryKey() != GTRegistries.MACHINE_REGISTRY) {
            return;
        }
        MultiMachines.init();
        Machines.init();
    }

    private static void registerCovers(RegisterEvent event) {
        if (event.getRegistryKey() != GTRegistries.COVER_REGISTRY) {
            return;
        }
        GTMMCovers.init();
    }
}
