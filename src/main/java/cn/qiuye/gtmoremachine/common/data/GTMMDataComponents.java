package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.item.datacomponents.WirelessEnergyTerminalData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GTMMDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GTmm.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WirelessEnergyTerminalData>> WIRELESS_ENERGY_TERMINAL =
            DATA_COMPONENTS.registerComponentType("wireless_energy_terminal",
                    builder -> builder.persistent(WirelessEnergyTerminalData.CODEC)
                            .networkSynchronized(WirelessEnergyTerminalData.STREAM_CODEC));

    private GTMMDataComponents() {}
}
