package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyBindingToolBehavior;
import cn.qiuye.gtmoremachine.common.item.WirelessEnergyTerminalBehavior;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ICustomRenderer;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;
import static com.gregtechceu.gtceu.common.data.GTItems.attach;

public class GTMMItems {

    static {
        GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static <T extends ComponentItem> NonNullConsumer<T> attachRenderer(ICustomRenderer customRenderer) {
        return !GTmm.isClientSide() ? NonNullConsumer.noop() : (item) -> item.attachComponents(customRenderer);
    }

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_BINDING_TOOL = GTMMREGISTRATE
            .item("wireless_energy_binding_tool", ComponentItem::create)
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new WirelessEnergyBindingToolBehavior()))
            .register();

    public static ItemEntry<ComponentItem> WIRELESS_ENERGY_TERMINAL = GTMMREGISTRATE
            .item("wireless_energy_terminal", ComponentItem::create)
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(new WirelessEnergyTerminalBehavior()))
            .onRegister(attach(new WirelessEnergyBindingToolBehavior())).register();

    public static void init() {}
}
