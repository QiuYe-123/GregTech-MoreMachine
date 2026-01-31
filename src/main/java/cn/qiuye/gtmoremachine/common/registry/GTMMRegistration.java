package cn.qiuye.gtmoremachine.common.registry;

import cn.qiuye.gtmoremachine.GTmm;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class GTMMRegistration {

    public final static GTRegistrate GTMM = GTRegistrate.create(GTmm.MOD_ID);

    static {
        GTMMRegistration.GTMM.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    private GTMMRegistration() {/**/}
}
