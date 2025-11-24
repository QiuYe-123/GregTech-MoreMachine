package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyTerminalBehavior;

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
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.OpV;
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

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LV = registerTieredCover(LV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_MV = registerTieredCover(MV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_HV = registerTieredCover(HV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_EV = registerTieredCover(EV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_IV = registerTieredCover(IV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LUV = registerTieredCover(LuV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_ZPM = registerTieredCover(ZPM, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UV = registerTieredCover(UV, 1);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UHV = GTCEuAPI.isHighTier() ?
            registerTieredCover(UHV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UEV = GTCEuAPI.isHighTier() ?
            registerTieredCover(UEV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UIV = GTCEuAPI.isHighTier() ?
            registerTieredCover(UIV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UXV = GTCEuAPI.isHighTier() ?
            registerTieredCover(UXV, 1) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_OPV = GTCEuAPI.isHighTier() ?
            registerTieredCover(OpV, 1) : null;

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LV_4A = registerTieredCover(LV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_MV_4A = registerTieredCover(MV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_HV_4A = registerTieredCover(HV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_EV_4A = registerTieredCover(EV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_IV_4A = registerTieredCover(IV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A = registerTieredCover(LuV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A = registerTieredCover(ZPM, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UV_4A = registerTieredCover(UV, 4);
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UHV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(UHV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UEV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(UEV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UIV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(UIV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UXV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(UXV, 4) : null;
    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_OPV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(OpV, 4) : null;

    private static ItemEntry<ComponentItem> registerTieredCover(int tier, int amperage) {
        return GTMMREGISTRATE
                .item(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + (amperage == 1 ? "" : amperage + "a_") + "wireless_energy_receive_cover", ComponentItem::create)
                .lang(VNF[tier] + " " + "Wireless Energy Receive Cover")
                .onRegister(attach(new TooltipBehavior(lines -> {
                    lines.add(Component.translatable("item.gtmoremachine.wireless_energy_receive_cover.tooltip.1"));
                    lines.add(Component.translatable("item.gtmoremachine.wireless_energy_receive_cover.tooltip.2"));
                    lines.add(Component.translatable("item.gtmoremachine.wireless_energy_receive_cover.tooltip.3", GTValues.VEX[tier] * amperage));
                }), new CoverPlaceBehavior(amperage == 1 ? WIRELESS_ENERGY_RECEIVE[tier - 1] : WIRELESS_ENERGY_RECEIVE_4A[tier - 1]))).register();
    }

    public static void init() {}
}
