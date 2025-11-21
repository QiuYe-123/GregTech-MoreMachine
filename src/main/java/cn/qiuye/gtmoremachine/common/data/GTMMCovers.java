package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.cover.CreativeEnergyCover;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;

import java.util.function.Supplier;

public class GTMMCovers {

    static {
        GTMMRegistration.GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.LV,
            GTCEuAPI.isHighTier() ? GTValues.OpV : GTValues.UV);

    public final static CoverDefinition CREATIVE_ENERGY = register("creative_energy",
            CreativeEnergyCover::new, () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_creative_energy")));

    public static CoverDefinition register(String id, CoverDefinition.CoverBehaviourProvider behaviorCreator,
                                           Supplier<Supplier<ICoverRenderer>> coverRenderer) {
        var definition = new CoverDefinition(GTmm.id(id), behaviorCreator, coverRenderer);
        GTRegistries.COVERS.register(GTmm.id(id), definition);
        return definition;
    }

    public static void init() {}
}
