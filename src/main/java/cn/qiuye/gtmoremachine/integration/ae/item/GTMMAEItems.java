package cn.qiuye.gtmoremachine.integration.ae.item;

import cn.qiuye.gtmoremachine.client.renderer.cover.VirtualItemProviderRenderer;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.item.VirtualItemProviderBehavior;
import cn.qiuye.gtmoremachine.common.item.itemstack.VirtualItemProviderCellItem;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;

import com.tterrag.registrate.util.entry.ItemEntry;

import static cn.qiuye.gtmoremachine.common.data.GTMMItems.attachRenderer;
import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;
import static com.gregtechceu.gtceu.common.data.GTItems.attach;

public class GTMMAEItems {

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.MORE_MACHINES);
    }

    public static final ItemEntry<VirtualItemProviderCellItem> VIRTUAL_ITEM_PROVIDER_CELL = GTMM.item("virtual_item_provider_cell", VirtualItemProviderCellItem::new).register();

    public static final ItemEntry<ComponentItem> VIRTUAL_ITEM_PROVIDER = GTMM.item("virtual_item_provider", ComponentItem::create)
            .properties(p -> p.stacksTo(1))
            .onRegister(attach(VirtualItemProviderBehavior.INSTANCE))
            .onRegister(attachRenderer(() -> VirtualItemProviderRenderer.INSTANCE))
            .register();

    public static final ItemEntry<ComponentItem> PROGRAMMABLE_COVER = GTMM.item("programmable_cover", ComponentItem::create)
            .onRegister(attach(new CoverPlaceBehavior(GTMMAECovers.PROGRAMMABLE_COVER)))
            .register();

    public static void init() {}
}
