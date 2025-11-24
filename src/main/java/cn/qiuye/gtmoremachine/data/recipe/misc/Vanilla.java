package cn.qiuye.gtmoremachine.data.recipe.misc;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.common.data.GTMMItems;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class Vanilla {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true, GTmm.id("advanced_terminal"), GTMMItems.ADVANCED_TERMINAL.asStack(),
                "SGS", "PBP", "PWP",
                'S', new MaterialEntry(TagPrefix.screw, GTMaterials.Steel),
                'G', Tags.Items.GLASS_PANES,
                'B', new ItemStack(Items.BOOK),
                'P', new MaterialEntry(TagPrefix.plate, GTMaterials.Steel),
                'W', new MaterialEntry(TagPrefix.wireGtSingle, GTMaterials.Tin));

        VanillaRecipeHelper.addShapedRecipe(provider, true, GTmm.id("wireless_energy_binding_tool"), GTMMItems.WIRELESS_ENERGY_BINDING_TOOL.asStack(),
                "A", 'A', Items.PAPER);
    }
}
