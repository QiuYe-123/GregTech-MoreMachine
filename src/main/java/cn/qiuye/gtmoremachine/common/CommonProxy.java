package cn.qiuye.gtmoremachine.common;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.GTMMCovers;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.data.machines.Machines;
import cn.qiuye.gtmoremachine.common.data.machines.multiblockmachine.MultiMachines;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.config.GTMMConfig;
import cn.qiuye.gtmoremachine.data.GTMMDatagen;
import cn.qiuye.gtmoremachine.utils.input.SyncedKeyMappings;
import cn.qiuye.gtmoremachine.utils.input.open.HotKeyActions;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {

    public CommonProxy() {
        init();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        GTMMRegistration.GTMMREGISTRATE.registerEventListeners(eventBus);
        eventBus.addListener(CommonProxy::commonSetup);
        eventBus.addGenericListener(GTCreativeModeTabs.class, this::registerCreativeModeTabs);
        eventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        eventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        eventBus.addGenericListener(CoverDefinition.class, this::registerCovers);
    }

    private static void init() {
        GTmm.LOGGER.info("GTMoreMachine common proxy init!");
        GTMMConfig.Companion.init();
        if (GTmm.isDataGen()) {
            GTMMConfig.getINSTANCE().isWirelessDimensionRateEnable = true;
            GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable = true;
        }
        GTMMDatagen.initPost();
        SyncedKeyMappings.init();
        HotKeyActions.init();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {}

    private void registerCreativeModeTabs(GTCEuAPI.RegisterEvent<ResourceLocation, GTCreativeModeTabs> event) {
        GTMMCreativeModeTabs.init();
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {}

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        MultiMachines.init();
        Machines.init();
    }

    private void registerCovers(GTCEuAPI.RegisterEvent<ResourceLocation, CoverDefinition> event) {
        GTMMCovers.init();
    }
}
