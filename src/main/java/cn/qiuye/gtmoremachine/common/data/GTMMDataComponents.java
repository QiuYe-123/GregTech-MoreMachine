package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.item.datacomponents.AdvancedTerminalData;
import cn.qiuye.gtmoremachine.common.item.datacomponents.CreativeFluidData;
import cn.qiuye.gtmoremachine.common.item.datacomponents.VirtualItemProviderData;
import cn.qiuye.gtmoremachine.common.item.datacomponents.WirelessEnergyTerminalData;
import cn.qiuye.gtmoremachine.common.item.datacomponents.WirelessTransferCoverData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GTMMDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GTmm.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WirelessEnergyTerminalData>> WIRELESS_ENERGY_TERMINAL = DATA_COMPONENTS.registerComponentType("wireless_energy_terminal",
            builder -> builder.persistent(WirelessEnergyTerminalData.CODEC)
                    .networkSynchronized(WirelessEnergyTerminalData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AdvancedTerminalData>> ADVANCED_TERMINAL = DATA_COMPONENTS.registerComponentType("advanced_terminal",
            builder -> builder.persistent(AdvancedTerminalData.CODEC)
                    .networkSynchronized(AdvancedTerminalData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CreativeFluidData>> CREATIVE_FLUID = DATA_COMPONENTS.registerComponentType("creative_fluid",
            builder -> builder.persistent(CreativeFluidData.CODEC)
                    .networkSynchronized(CreativeFluidData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VirtualItemProviderData>> VIRTUAL_ITEM_PROVIDER = DATA_COMPONENTS.registerComponentType("virtual_item_provider",
            builder -> builder.persistent(VirtualItemProviderData.CODEC)
                    .networkSynchronized(VirtualItemProviderData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WirelessTransferCoverData>> WIRELESS_TRANSFER_COVER = DATA_COMPONENTS.registerComponentType("wireless_transfer_cover",
            builder -> builder.persistent(WirelessTransferCoverData.CODEC)
                    .networkSynchronized(WirelessTransferCoverData.STREAM_CODEC));

    private GTMMDataComponents() {}
}
