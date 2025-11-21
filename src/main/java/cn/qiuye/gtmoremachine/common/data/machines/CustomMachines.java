package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class CustomMachines {

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.ULV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UV);

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    public static void init() {
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAEMachines.init();
        }
    }
}
