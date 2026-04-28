package cn.qiuye.gtmoremachine;

import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.data.recipes.GTMMRecipes;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.RecipeOutput;

@SuppressWarnings("unused")
@GTAddon(GTmm.MOD_ID)
public class GTMMGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTMMRegistration.GTMM;
    }

    @Override
    public void gtInitComplete() {}

    @Override
    public void addRecipes(RecipeOutput provider) {
        GTMMRecipes.init(provider);
    }
}
