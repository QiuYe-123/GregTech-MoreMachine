package cn.qiuye.gtmoremachine;

import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.data.GTMMDatagen;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

@GTAddon
public class GTMMGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTMMRegistration.GTMMREGISTRATE;
    }

    @Override
    public String addonModId() {
        return GTmm.MOD_ID;
    }

    @Override
    public void initializeAddon() {
        GTMMDatagen.initPost();
    }
}
