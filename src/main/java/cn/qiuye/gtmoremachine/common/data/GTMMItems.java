package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyTerminalBehavior;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ICustomRenderer;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;

import net.minecraft.network.chat.Component;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import java.util.Locale;

import static cn.qiuye.gtmoremachine.common.data.GTMMCovers.*;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.common.data.GTItems.attach;

public class GTMMItems {

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static <T extends ComponentItem> NonNullConsumer<T> attachRenderer(ICustomRenderer customRenderer) {
        return !GTmm.isClientSide() ? NonNullConsumer.noop() : (item) -> item.attachComponents(customRenderer);
    }

    public static ItemEntry<ComponentItem> ADVANCED_TERMINAL = GTMMREGISTRATE
            .item("advanced_terminal", ComponentItem::create)
            .lang("§bAdvanced Terminal")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new AdvancedTerminalBehavior()))
            .register();

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_BINDING_TOOL = GTMMREGISTRATE
            .item("wireless_energy_binding_tool", ComponentItem::create)
            .lang("Wireless Energy Binding Tool")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new WirelessEnergyBindingToolBehavior()))
            .register();

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_TERMINAL = GTMMREGISTRATE
            .item("wireless_energy_terminal", ComponentItem::create)
            .lang("Wireless Energy Terminal")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new WirelessEnergyTerminalBehavior()))
            .onRegister(attach(new WirelessEnergyBindingToolBehavior())).register();

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LV = registerTieredCover(GTValues.LV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_MV = registerTieredCover(GTValues.MV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_HV = registerTieredCover(GTValues.HV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_EV = registerTieredCover(GTValues.EV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_IV = registerTieredCover(GTValues.IV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LUV = registerTieredCover(GTValues.LuV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_ZPM = registerTieredCover(GTValues.ZPM, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UV = registerTieredCover(GTValues.UV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UHV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UHV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UEV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UEV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UIV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UIV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UXV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UXV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_OPV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.OpV, 1) : null;

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LV_4A = registerTieredCover(GTValues.LV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_MV_4A = registerTieredCover(GTValues.MV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_HV_4A = registerTieredCover(GTValues.HV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_EV_4A = registerTieredCover(GTValues.EV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_IV_4A = registerTieredCover(GTValues.IV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A = registerTieredCover(GTValues.LuV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A = registerTieredCover(GTValues.ZPM, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UV_4A = registerTieredCover(GTValues.UV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UHV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UHV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UEV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UEV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UIV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UIV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UXV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UXV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_OPV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.OpV, 4) : null;

    private static ItemEntry<ComponentItem> registerTieredCover(int tier, int amperage) {
        return GTMMREGISTRATE
                .item(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + (amperage == 1 ? "" : amperage + "a_") + "wireless_energy_receive_cover", ComponentItem::create)
                .lang(GTValues.VNF[tier] + " " + "Wireless Energy Receive Cover")
                .onRegister(attach(new TooltipBehavior(lines -> {
                    lines.add(Component.translatable("item.gtmoremachine.wireless_energy_receive_cover.tooltip.1"));
                    lines.add(Component.translatable("item.gtmoremachine.wireless_energy_receive_cover.tooltip.2"));
                    lines.add(Component.translatable("item.gtmoremachine.wireless_energy_receive_cover.tooltip.3", GTValues.VEX[tier] * amperage));
                }), new CoverPlaceBehavior(amperage == 1 ? WIRELESS_ENERGY_RECEIVE[tier - 1] : WIRELESS_ENERGY_RECEIVE_4A[tier - 1]))).register();
    }

    public static void init() {
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAEItems.init();
        }
    }
}
