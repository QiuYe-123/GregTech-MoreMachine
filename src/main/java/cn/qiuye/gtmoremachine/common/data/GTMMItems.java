package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyTerminalBehavior;
import cn.qiuye.gtmoremachine.common.item.behaviour.WirelessTransferCoverPlaceBehavior;
import cn.qiuye.gtmoremachine.common.item.behaviour.WirelessTransferCoverTooltipBehavior;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ICustomRenderer;
import com.gregtechceu.gtceu.common.item.behavior.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.behavior.TooltipBehavior;

import net.minecraft.network.chat.Component;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import java.util.Locale;

import static cn.qiuye.gtmoremachine.common.data.GTMMCovers.*;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;
import static com.gregtechceu.gtceu.common.data.GTItems.attach;

@GTMMDataGeneratorScanned
public class GTMMItems {

    private static final String WIRELESS_COVER_PREFIX = "item.gtmoremachine.wireless_energy_receive_cover";
    private static final String WIRELESS_TRANSFER_PREFIX = "item.gtmoremachine.wireless_transfer";
    private static final String ADVANCED_WIRELESS_TRANSFER_PREFIX = "item.gtmoremachine.advanced_wireless_transfer";
    @GTMMRegisterLanguage(en = "§bPull Energy§7 from EU network to the machine as §fCover§7.", cn = "§7作§f覆盖板§7时从电网§b拉取能量§7传输到机器。")
    public static final String WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_1 = WIRELESS_COVER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "§7Can only used for §esingle block machine§7.Can't put on the machine blow the cover's voltage", cn = "§7只可用于§e单方块机器§7。无法将超过机器电压等级的覆盖板安装到机器上。")
    public static final String WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_2 = WIRELESS_COVER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "§bEnergy transfer speed: §f%s §7EU/t", cn = "§b能量传输效率：§f%s §7EU/t")
    public static final String WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_3 = WIRELESS_COVER_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "§7Bind to: §f%s (%s)", cn = "§7已绑定容器：§f%s (%s)")
    public static final String WIRELESS_TRANSFER_TOOLTIP_1 = WIRELESS_TRANSFER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "§7Right click the container with shift to bind container.Right click the air with shift to unbind.", cn = "§7潜行右键需要绑定的容器来进行绑定。潜行右键空气取消绑定。")
    public static final String WIRELESS_TRANSFER_TOOLTIP_2 = WIRELESS_TRANSFER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Success bind to: %s (%s)", cn = "绑定容器成功：%s (%s)")
    public static final String WIRELESS_TRANSFER_TOOLTIP_BIND_1 = WIRELESS_TRANSFER_PREFIX + ".tooltip.bind.1";
    @GTMMRegisterLanguage(en = "Success unbind.", cn = "解除绑定成功")
    public static final String WIRELESS_TRANSFER_TOOLTIP_BIND_2 = WIRELESS_TRANSFER_PREFIX + ".tooltip.bind.2";
    @GTMMRegisterLanguage(en = "§bTransfer Item§7 to §ebinded container§7 from the machine as §fCover§7.", cn = "§7作§f覆盖板§7时从机器中§b提取物品§7到§e绑定的容器§7中。")
    public static final String WIRELESS_TRANSFER_ITEM_TOOLTIP_1 = WIRELESS_TRANSFER_PREFIX + ".item.tooltip.1";
    @GTMMRegisterLanguage(en = "§bTransfer Fluid§7 to §ebinded container§7 from the machine as §fCover§7.", cn = "§7作§f覆盖板§7时从机器中§b提取流体§7到§e绑定的容器§7中。")
    public static final String WIRELESS_TRANSFER_FLUID_TOOLTIP_1 = WIRELESS_TRANSFER_PREFIX + ".fluid.tooltip.1";
    @GTMMRegisterLanguage(en = "§7Can use §f filter card", cn = "§7可使用§f过滤卡")
    public static final String ADVANCED_WIRELESS_TRANSFER_TOOLTIP_1 = ADVANCED_WIRELESS_TRANSFER_PREFIX + ".tooltip.1";

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static <T extends ComponentItem> NonNullConsumer<T> attachRenderer(ICustomRenderer customRenderer) {
        return !GTmm.isClientSide() ? NonNullConsumer.noop() : (item) -> item.attachComponents(customRenderer);
    }

    public final static ItemEntry<ComponentItem> WIRELESS_ITEM_TRANSFER_COVER = GTMM
            .item("wireless_item_transfer_cover", ComponentItem::create)
            .lang("Wireless Item Transfer Cover")
            .onRegister(attach(new WirelessTransferCoverPlaceBehavior(GTMMCovers.WIRELESS_ITEM_TRANSFER),
                    new CoverPlaceBehavior(GTMMCovers.WIRELESS_ITEM_TRANSFER),
                    new WirelessTransferCoverTooltipBehavior(lines -> {
                        lines.add(Component.translatable(WIRELESS_TRANSFER_ITEM_TOOLTIP_1));
                        lines.add(Component.translatable(WIRELESS_TRANSFER_TOOLTIP_2));
                    })))
            .register();

    public final static ItemEntry<ComponentItem> WIRELESS_FLUID_TRANSFER_COVER = GTMM
            .item("wireless_fluid_transfer_cover", ComponentItem::create)
            .lang("Wireless Fluid Transfer Cover")
            .onRegister(attach(new WirelessTransferCoverPlaceBehavior(GTMMCovers.WIRELESS_FLUID_TRANSFER),
                    new CoverPlaceBehavior(GTMMCovers.WIRELESS_FLUID_TRANSFER),
                    new WirelessTransferCoverTooltipBehavior(lines -> {
                        lines.add(Component.translatable(WIRELESS_TRANSFER_FLUID_TOOLTIP_1));
                        lines.add(Component.translatable(WIRELESS_TRANSFER_TOOLTIP_2));
                    })))
            .register();

    public final static ItemEntry<ComponentItem> ADVANCED_WIRELESS_ITEM_TRANSFER_COVER = GTMM
            .item("advanced_wireless_item_transfer_cover", ComponentItem::create)
            .lang("§bAdvanced Wireless Item Transfer Cover")
            .onRegister(attach(new WirelessTransferCoverPlaceBehavior(GTMMCovers.ADVANCED_WIRELESS_ITEM_TRANSFER),
                    new CoverPlaceBehavior(GTMMCovers.ADVANCED_WIRELESS_ITEM_TRANSFER),
                    new WirelessTransferCoverTooltipBehavior(lines -> {
                        lines.add(Component.translatable(WIRELESS_TRANSFER_ITEM_TOOLTIP_1));
                        lines.add(Component.translatable(ADVANCED_WIRELESS_TRANSFER_TOOLTIP_1));
                        lines.add(Component.translatable(WIRELESS_TRANSFER_TOOLTIP_2));
                    })))
            .register();

    public final static ItemEntry<ComponentItem> ADVANCED_WIRELESS_FLUID_TRANSFER_COVER = GTMM
            .item("advanced_wireless_fluid_transfer_cover", ComponentItem::create)
            .lang("§bAdvanced Wireless Fluid Transfer Cover")
            .onRegister(attach(new WirelessTransferCoverPlaceBehavior(GTMMCovers.ADVANCED_WIRELESS_FLUID_TRANSFER),
                    new CoverPlaceBehavior(GTMMCovers.ADVANCED_WIRELESS_FLUID_TRANSFER),
                    new WirelessTransferCoverTooltipBehavior(lines -> {
                        lines.add(Component.translatable(WIRELESS_TRANSFER_FLUID_TOOLTIP_1));
                        lines.add(Component.translatable(ADVANCED_WIRELESS_TRANSFER_TOOLTIP_1));
                        lines.add(Component.translatable(WIRELESS_TRANSFER_TOOLTIP_2));
                    })))
            .register();

    public final static ItemEntry<ComponentItem> ADVANCED_TERMINAL = GTMM
            .item("advanced_terminal", ComponentItem::create)
            .lang("§bAdvanced Terminal")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new AdvancedTerminalBehavior()))
            .register();

    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_TERMINAL = GTMM
            .item("wireless_energy_terminal", ComponentItem::create)
            .lang("Wireless Energy Terminal")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new WirelessEnergyTerminalBehavior()))
            .onRegister(attach(new WirelessEnergyBindingToolBehavior()))
            .register();

    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_BINDING_TOOL = GTMM
            .item("wireless_energy_binding_tool", ComponentItem::create)
            .lang("Wireless Energy Binding Tool")
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new WirelessEnergyBindingToolBehavior()))
            .register();

    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LV = registerTieredCover(GTValues.LV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_MV = registerTieredCover(GTValues.MV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_HV = registerTieredCover(GTValues.HV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_EV = registerTieredCover(GTValues.EV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_IV = registerTieredCover(GTValues.IV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LUV = registerTieredCover(GTValues.LuV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_ZPM = registerTieredCover(GTValues.ZPM, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UV = registerTieredCover(GTValues.UV, 1);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UHV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UHV, 1) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UEV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UEV, 1) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UIV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UIV, 1) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UXV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UXV, 1) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_OPV = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.OpV, 1) : null;

    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LV_4A = registerTieredCover(GTValues.LV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_MV_4A = registerTieredCover(GTValues.MV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_HV_4A = registerTieredCover(GTValues.HV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_EV_4A = registerTieredCover(GTValues.EV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_IV_4A = registerTieredCover(GTValues.IV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A = registerTieredCover(GTValues.LuV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A = registerTieredCover(GTValues.ZPM, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UV_4A = registerTieredCover(GTValues.UV, 4);
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UHV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UHV, 4) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UEV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UEV, 4) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UIV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UIV, 4) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_UXV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.UXV, 4) : null;
    public final static ItemEntry<ComponentItem> WIRELESS_ENERGY_RECEIVE_COVER_OPV_4A = GTCEuAPI.isHighTier() ?
            registerTieredCover(GTValues.OpV, 4) : null;

    private static ItemEntry<ComponentItem> registerTieredCover(int tier, int amperage) {
        return GTMM
                .item(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + (amperage == 1 ? "" : amperage + "a_") + "wireless_energy_receive_cover", ComponentItem::create)
                .lang(GTValues.VNF[tier] + " " + "Wireless Energy Receive Cover")
                .onRegister(attach(new TooltipBehavior(lines -> {
                    lines.add(Component.translatable(WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_1));
                    lines.add(Component.translatable(WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_2));
                    lines.add(Component.translatable(WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_3, GTValues.VEX[tier] * amperage));
                }), new CoverPlaceBehavior(amperage == 1 ? WIRELESS_ENERGY_RECEIVE[tier - 1] : WIRELESS_ENERGY_RECEIVE_4A[tier - 1]))).register();
    }

    public static void init() {
        if (GTmm.Mods.isAE2Loaded()) {
            GTMMAEItems.init();
        }
    }
}
