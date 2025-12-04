package cn.qiuye.gtmoremachine.data.recipes;

import cn.qiuye.gtmoremachine.data.recipes.misc.AssemblerRecipe;
import cn.qiuye.gtmoremachine.data.recipes.misc.VanillaRecipe;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class GTMMRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipe.init(provider);
        AssemblerRecipe.init(provider);
    }
}
