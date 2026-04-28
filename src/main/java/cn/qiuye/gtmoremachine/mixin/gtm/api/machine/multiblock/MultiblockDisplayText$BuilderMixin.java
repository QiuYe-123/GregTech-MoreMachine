package cn.qiuye.gtmoremachine.mixin.gtm.api.machine.multiblock;

import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiblockDisplayText.Builder.class)
public class MultiblockDisplayText$BuilderMixin {

    @Redirect(
              method = "addEnergyUsageLine",
              at = @At(
                       value = "INVOKE",
                       target = "Lcom/gregtechceu/gtceu/utils/FormattingUtil;formatNumbers(J)Ljava/lang/String;"),
              remap = false)
    private String modifyFormatNumbersArg(long number) {
        return NumberUtils.formatLong(number);
    }

    @Redirect(
              method = "addEnergyProductionLine",
              at = @At(
                       value = "INVOKE",
                       target = "Lcom/gregtechceu/gtceu/utils/FormattingUtil;formatNumbers(J)Ljava/lang/String;"),
              remap = false)
    private String modifyFormatNumbersArg1(long number) {
        return NumberUtils.formatLong(number);
    }

    @Redirect(
              method = "addEnergyProductionAmpsLine",
              at = @At(
                       value = "INVOKE",
                       target = "Lcom/gregtechceu/gtceu/utils/FormattingUtil;formatNumbers(J)Ljava/lang/String;"),
              remap = false)
    private String modifyFormatNumbersArg2(long number) {
        return NumberUtils.formatLong(number);
    }
}
