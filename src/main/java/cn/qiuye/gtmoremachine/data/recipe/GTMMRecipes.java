package cn.qiuye.gtmoremachine.data.recipe;

import cn.qiuye.gtmoremachine.data.recipe.misc.Assembler;
import cn.qiuye.gtmoremachine.data.recipe.misc.Vanilla;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class GTMMRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        Vanilla.init(provider);
        Assembler.init(provider);
    }
}
