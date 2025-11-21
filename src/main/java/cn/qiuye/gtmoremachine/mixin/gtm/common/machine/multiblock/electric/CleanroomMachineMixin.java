package cn.qiuye.gtmoremachine.mixin.gtm.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CleanroomMachine.class)
public class CleanroomMachineMixin {

    @Shadow(remap = false)
    private CleanroomType cleanroomType;

    @Shadow(remap = false)
    private int cleanAmount;

    @Shadow(remap = false)
    private int lDist, rDist, bDist, fDist, hDist;

    @Inject(
            method = "addDisplayText",
            at = @At(
                     value = "INVOKE",
                     target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                     ordinal = 0),
            cancellable = true,
            remap = false)
    private void modifyVoltageDisplay(List<Component> textList, CallbackInfo ci,
                                      @Local(name = "maxVoltage") long maxVoltage, @Local(name = "voltageName") String voltageName) {
        ci.cancel();

        CleanroomMachine self = (CleanroomMachine) (Object) this;

        if (self.isFormed()) {
            if (maxVoltage > 0) {
                textList.add(Component.translatable("gtceu.multiblock.max_energy_per_tick", NumberUtils.formatLong(maxVoltage), voltageName));
            }

            if (cleanroomType != null) {
                textList.add(Component.translatable(cleanroomType.getTranslationKey()));
            }

            if (!self.isWorkingEnabled()) {
                textList.add(Component.translatable("gtceu.multiblock.work_paused"));

            } else if (self.isActive()) {
                textList.add(Component.translatable("gtceu.multiblock.running"));
                int currentProgress = (int) (self.recipeLogic.getProgressPercent() * 100);
                double maxInSec = (float) self.recipeLogic.getDuration() / 20.0f;
                double currentInSec = (float) self.recipeLogic.getProgress() / 20.0f;
                textList.add(
                        Component.translatable("gtceu.multiblock.progress", String.format("%.2f", (float) currentInSec),
                                String.format("%.2f", (float) maxInSec), currentProgress));
            } else {
                textList.add(Component.translatable("gtceu.multiblock.idling"));
            }

            if (self.recipeLogic.isWaiting()) {
                textList.add(Component.translatable("gtceu.multiblock.waiting")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
            }

            if (self.isClean()) textList.add(Component.translatable("gtceu.multiblock.cleanroom.clean_state"));
            else textList.add(Component.translatable("gtceu.multiblock.cleanroom.dirty_state"));
            textList.add(Component.translatable("gtceu.multiblock.cleanroom.clean_amount", this.cleanAmount));
            textList.add(Component.translatable("gtceu.multiblock.dimensions.0"));
            textList.add(Component.translatable("gtceu.multiblock.dimensions.1", lDist + rDist + 1, hDist + 1,
                    fDist + bDist + 1));
        } else {
            Component tooltip = Component.translatable("gtceu.multiblock.invalid_structure.tooltip")
                    .withStyle(ChatFormatting.GRAY);
            textList.add(Component.translatable("gtceu.multiblock.invalid_structure")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip))));
        }
    }
}
