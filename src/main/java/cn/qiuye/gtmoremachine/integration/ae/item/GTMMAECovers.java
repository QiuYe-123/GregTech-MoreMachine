package cn.qiuye.gtmoremachine.integration.ae.item;

import cn.qiuye.gtmoremachine.common.cover.ProgrammableCover;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;

import static cn.qiuye.gtmoremachine.common.data.GTMMCovers.register;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class GTMMAECovers {

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    public final static CoverDefinition PROGRAMMABLE_COVER = register("programmable_cover",
            ProgrammableCover::new, () -> () -> new SimpleCoverRenderer(GTCEu.id("item/programmed_circuit/1")));

    public static void init() {}
}
