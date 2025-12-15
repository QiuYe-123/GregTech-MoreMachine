package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.gui.BlockMapSelectorWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.ExtendLabelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.TerminalInputWidget;
import cn.qiuye.gtmoremachine.api.pattern.AdvancedBlockNoAEPattern;
import cn.qiuye.gtmoremachine.api.pattern.AdvancedBlockPattern;
import cn.qiuye.gtmoremachine.api.pattern.Hatch;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static cn.qiuye.gtmoremachine.common.block.BlockMap.*;
import static net.minecraft.network.chat.Component.translatable;

@Getter
@Setter
public class AdvancedTerminalBehavior implements IItemUIFactory {

    public AdvancedTerminalBehavior() {}

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            var level = context.getLevel();
            var blockPos = context.getClickedPos();
            var metaMachine = MetaMachine.getMachine(level, blockPos);
            if (context.getPlayer() != null && !level.isClientSide() &&
                    metaMachine instanceof IMultiController controller) {
                var autoBuildSetting = getAutoBuildSetting(context.getPlayer().getMainHandItem());

                if (GTmm.Mods.isAE2Loaded()) {
                    if (!controller.isFormed() || autoBuildSetting.isUseDemolish()) {
                        AdvancedBlockPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                    } else if (metaMachine instanceof WorkableMultiblockMachine machine && autoBuildSetting.isReplaceMode()) {
                        AdvancedBlockPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                        machine.onPartUnload();
                    }
                } else {
                    if (!controller.isFormed() || autoBuildSetting.isUseDemolish()) {
                        AdvancedBlockNoAEPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                    } else if (metaMachine instanceof WorkableMultiblockMachine machine && autoBuildSetting.isReplaceMode()) {
                        AdvancedBlockNoAEPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                        machine.onPartUnload();
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    private AutoBuildSetting getAutoBuildSetting(ItemStack mainHandItem) {
        var autoBuildSetting = new AutoBuildSetting();
        var tag = mainHandItem.getOrCreateTag();
        if (!tag.isEmpty()) {
            autoBuildSetting.setTier(tag.getInt("Tier"));
            autoBuildSetting.setRepeatCount(tag.getInt("RepeatCount"));
            autoBuildSetting.setNoHatchMode(tag.getBoolean("NoHatchMode"));
            autoBuildSetting.setReplaceMode(tag.getBoolean("ReplaceMode"));
            autoBuildSetting.setUseAE(tag.getBoolean("IsUseAE"));
            autoBuildSetting.setUseMirror(tag.getBoolean("IsUseMirror"));
            autoBuildSetting.setUseDemolish(tag.getBoolean("IsUseDemolish"));
            String block = tag.getString("blocks");
            if (!block.isEmpty()) {
                autoBuildSetting.tierBlock = tierBlockMap.get(block).get();
                autoBuildSetting.blocks = new ObjectOpenHashSet<>(autoBuildSetting.tierBlock);
            }
        }
        return autoBuildSetting;
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        return new ModularUI(176, 166, heldItemHolder, player).widget(createWidget(player));
    }

    private Widget createWidget(Player player) {
        final var handItem = player.getMainHandItem();
        var group = new WidgetGroup(0, 0, 182 + 8, 133 + 8);
        int rowIndex = 1;
        var contain = new DraggableScrollableWidgetGroup(4, 4, 182, 133)
                .setBackground(GuiTextures.DISPLAY).setYScrollBarWidth(2)
                .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1));
        contain.addWidget(new ExtendLabelWidget(65, 8, Component.translatable("item.gtmoremachine.advanced_terminal.setting.title")))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex++, Component.translatable("item.gtmoremachine.advanced_terminal.setting.1")))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.2"))
                        .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.2.tooltip")))
                .addWidget(new TerminalInputWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        () -> getRepeatCount(handItem), (v) -> setRepeatCount(v, handItem))
                        .setMin(0).setMax(1000))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.3"))
                        .setHoverTooltips("item.gtmoremachine.advanced_terminal.setting.3.tooltip"))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setBuildHatches(v, handItem))
                        .setPressed(getBuildHatches(handItem))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.4"))
                        .setHoverTooltips("item.gtmoremachine.advanced_terminal.setting.4.tooltip"))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setReplaceMode(v, handItem))
                        .setPressed(getReplaceMode(handItem))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.5"))
                        .setHoverTooltips("item.gtmoremachine.advanced_terminal.setting.5.tooltip"))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setUseAE(v, handItem))
                        .setPressed(getUseAE(handItem))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new LabelWidget(4, 5 + 16 * rowIndex, "item.gtmoremachine.advanced_terminal.setting.6")
                        .setHoverTooltips("item.gtmoremachine.advanced_terminal.setting.6.tooltip"))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setUseMirror(v, handItem))
                        .setPressed(getUseMirror(handItem))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new LabelWidget(4, 5 + 16 * rowIndex, "item.gtmoremachine.advanced_terminal.setting.7")
                        .setHoverTooltips("item.gtmoremachine.advanced_terminal.setting.7.tooltip"))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setUseDemolish(v, handItem))
                        .setPressed(getUseDemolish(handItem))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))));
        var blockLabel = new ExtendLabelWidget(40, 21, getBlockComponent(handItem));
        var blockMap = new BlockMapSelectorWidget(group.getSizeHeight() + 4, contain.getSizeWidth(), (s, i) -> {
            if (s != null && i != null) {
                var tag = handItem.getOrCreateTag();
                tag.putString("blocks", s);
                tag.putInt("Tier", i);
                handItem.setTag(tag);
                blockLabel.setComponent(Component.literal(" (").append(translatable(s))
                        .append(Component.literal(" : "))
                        .append(tierBlockMap.get(s).get()[i].getName())
                        .append(Component.literal(")")));
            }
        });
        blockMap.setInit(handItem);
        var open = new SwitchWidget(4, 21, 30, 16, (c, f) -> blockMap.showType(f))
                .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.1.tooltip"));
        contain.addWidget(open).addWidget(blockLabel);
        group.addWidget(contain).addWidget(blockMap).setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    private static Component getBlockComponent(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty()) {
            var block = tag.getString("blocks");
            if (!block.isEmpty()) {
                int tier = tag.getInt("Tier");
                return Component.literal("(").append(translatable(block))
                        .append(Component.literal(" : "))
                        .append(tierBlockMap.get(block).get()[tier].getName())
                        .append(Component.literal(")"));
            }
        }
        return Component.literal("");
    }

    private int getRepeatCount(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("RepeatCount")) {
            return tag.getInt("RepeatCount");
        } else {
            return 0;
        }
    }

    private void setRepeatCount(int repeatCount, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putInt("RepeatCount", repeatCount);
        itemStack.setTag(tag);
    }

    private boolean getBuildHatches(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("NoHatchMode")) {
            return tag.getBoolean("NoHatchMode");
        } else {
            return true;
        }
    }

    private void setBuildHatches(boolean isBuildHatches, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putBoolean("NoHatchMode", isBuildHatches);
        itemStack.setTag(tag);
    }

    private boolean getReplaceMode(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("ReplaceMode")) {
            return tag.getBoolean("ReplaceMode");
        } else {
            return false;
        }
    }

    private void setReplaceMode(boolean isReplaceCoil, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putBoolean("ReplaceMode", isReplaceCoil);
        itemStack.setTag(tag);
    }

    private boolean getUseAE(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("IsUseAE")) {
            return tag.getBoolean("IsUseAE");
        } else {
            return false;
        }
    }

    private void setUseAE(boolean isUseAE, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putBoolean("IsUseAE", isUseAE);
        itemStack.setTag(tag);
    }

    private boolean getUseMirror(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("IsUseMirror")) {
            return tag.getBoolean("IsUseMirror");
        } else {
            return false; // 默认值为 0 (否)
        }
    }

    private void setUseMirror(boolean isUseMirror, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putBoolean("IsUseMirror", isUseMirror);
        itemStack.setTag(tag);
    }

    private boolean getUseDemolish(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("IsUseDemolish")) {
            return tag.getBoolean("IsUseDemolish");
        } else {
            return false;
        }
    }

    private void setUseDemolish(boolean isUseDemolish, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putBoolean("IsUseDemolish", isUseDemolish);
        itemStack.setTag(tag);
    }

    @Setter
    @Getter
    public static class AutoBuildSetting {

        Block[] tierBlock;
        Set<Block> blocks = Collections.emptySet();
        private int Tier, repeatCount;
        private boolean noHatchMode, replaceMode, isUseAE, isUseMirror, isUseDemolish;

        public AutoBuildSetting() {
            this.Tier = 0;
            this.repeatCount = 0;
            this.noHatchMode = true;
            this.replaceMode = false;
            this.isUseAE = false;
            this.isUseMirror = false;
            this.isUseDemolish = false;
        }

        public List<ItemStack> apply(BlockInfo[] blockInfos) {
            List<ItemStack> candidates = new ObjectArrayList<>();
            if (blockInfos != null) {
                for (var info : blockInfos) {
                    if (this.tierBlock != null && this.Tier >= 0 && blockInfos.length > 1 &&
                            this.blocks.contains(info.getBlockState().getBlock())) {
                        candidates.add(tierBlock[Math.min(this.Tier, blockInfos.length - 1)].asItem().getDefaultInstance());
                        return candidates;
                    }
                    if (info.getBlockState().getBlock() != Blocks.AIR) candidates.add(info.getItemStackForm());
                }
            }
            return candidates;
        }

        public boolean isPlaceHatch(BlockInfo[] blockInfos) {
            if (!this.noHatchMode) return true;
            if (blockInfos != null && blockInfos.length > 0) {
                var blockInfo = blockInfos[0];
                return !(blockInfo.getBlockState().getBlock() instanceof MetaMachineBlock machineBlock) ||
                        !Hatch.getBlockSet().contains(machineBlock);
            }
            return true;
        }
    }
}
