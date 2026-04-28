package cn.qiuye.gtmoremachine.data.recipes

import cn.qiuye.gtmoremachine.GTmm
import cn.qiuye.gtmoremachine.data.recipes.misc.AE2AssemblerRecipe
import cn.qiuye.gtmoremachine.data.recipes.misc.AssemblerRecipe
import cn.qiuye.gtmoremachine.data.recipes.misc.VanillaRecipe

import net.minecraft.data.recipes.RecipeOutput

object GTMMRecipes {

	@JvmStatic
	fun init(provider: RecipeOutput) {
		if (GTmm.Mods.isAE2Loaded()) {
			AE2AssemblerRecipe.init(provider)
		}
		AssemblerRecipe.init(provider)
		VanillaRecipe.init(provider)
	}
}
