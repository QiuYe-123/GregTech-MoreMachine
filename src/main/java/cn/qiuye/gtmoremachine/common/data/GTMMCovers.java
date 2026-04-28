package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.cover.AdvancedWirelessTransferCover;
import cn.qiuye.gtmoremachine.common.cover.CreativeEnergyCover;
import cn.qiuye.gtmoremachine.common.cover.WirelessEnergyReceiveCover;
import cn.qiuye.gtmoremachine.common.cover.WirelessTransferCover;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAECovers;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;

public class GTMMCovers {

    public static final int[] ALL_TIERS = GTValues.tiersBetween(GTValues.LV,
            GTCEuAPI.isHighTier() ? GTValues.OpV : GTValues.UV);

    static {
        GTMMRegistration.GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public final static CoverDefinition CREATIVE_ENERGY = register("creative_energy",
            CreativeEnergyCover::new, () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_creative_energy")));

    public static CoverDefinition register(String id, CoverDefinition.CoverBehaviourProvider behaviorCreator,
                                           Supplier<Supplier<ICoverRenderer>> coverRenderer) {
        var definition = new CoverDefinition(GTmm.id(id), behaviorCreator, coverRenderer);
        GTRegistries.register(GTRegistries.COVERS, definition.getId(), definition);
        return definition;
    }

    public final static CoverDefinition WIRELESS_ITEM_TRANSFER = register("wireless_item_transfer",
            (holder, coverable, side) -> new WirelessTransferCover(holder, coverable, side, WirelessTransferCover.TRANSFER_ITEM),
            () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_wireless_item_transfer")));

    public final static CoverDefinition WIRELESS_FLUID_TRANSFER = register("wireless_fluid_transfer",
            (holder, coverable, side) -> new WirelessTransferCover(holder, coverable, side, WirelessTransferCover.TRANSFER_FLUID),
            () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_wireless_fluid_transfer")));

    public final static CoverDefinition ADVANCED_WIRELESS_ITEM_TRANSFER = register("advanced_wireless_item_transfer",
            (holder, coverable, side) -> new AdvancedWirelessTransferCover(holder, coverable, side, WirelessTransferCover.TRANSFER_ITEM),
            () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_wireless_item_transfer")));

    public final static CoverDefinition ADVANCED_WIRELESS_FLUID_TRANSFER = register("advanced_wireless_fluid_transfer",
            (holder, coverable, side) -> new AdvancedWirelessTransferCover(holder, coverable, side, WirelessTransferCover.TRANSFER_FLUID),
            () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_wireless_fluid_transfer")));

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    public final static CoverDefinition[] WIRELESS_ENERGY_RECEIVE = registerTieredWirelessCover(
            "wireless_energy_receive", 1, ALL_TIERS);

    public final static CoverDefinition[] WIRELESS_ENERGY_RECEIVE_4A = registerTieredWirelessCover(
            "4a_wireless_energy_receive", 4, ALL_TIERS);

    public static CoverDefinition[] registerTieredWirelessCover(String id, int amperage, int[] tiers) {
        return Arrays.stream(tiers).mapToObj(tier -> {
            var name = id + "." + GTValues.VN[tier].toLowerCase(Locale.ROOT);
            return register(name,
                    (holder, coverable, side) -> new WirelessEnergyReceiveCover(holder, coverable, side, tier, amperage),
                    () -> () -> new SimpleCoverRenderer(GTmm.id("block/cover/overlay_" + (amperage == 1 ? "" : "4a_") + "wireless_energy_receive")));
        }).toArray(CoverDefinition[]::new);
    }

    public static void init() {
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAECovers.init();
        }
    }
}
