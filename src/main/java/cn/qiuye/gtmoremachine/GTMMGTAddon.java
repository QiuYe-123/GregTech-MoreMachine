package cn.qiuye.gtmoremachine;

import cn.qiuye.gtmoremachine.common.data.GTMMBlocks;
import cn.qiuye.gtmoremachine.common.data.GTMMItems;
import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;
import cn.qiuye.gtmoremachine.data.GTMMDatagen;
import cn.qiuye.gtmoremachine.data.recipe.GTMMRecipes;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

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
        GTMMItems.init();
        GTMMBlocks.init();
        GTMMDatagen.initPost();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTMMRecipes.init(provider);
    }
}
