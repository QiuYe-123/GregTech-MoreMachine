package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.gui.widget.BlockMapSelectorWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.ExtendLabelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.TerminalInputWidget;
import cn.qiuye.gtmoremachine.api.pattern.AdvancedBlockNoAEPattern;
import cn.qiuye.gtmoremachine.api.pattern.AdvancedBlockPattern;
import cn.qiuye.gtmoremachine.api.pattern.Hatch;
import cn.qiuye.gtmoremachine.common.block.BlockMap;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
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
import net.minecraft.world.level.block.LiquidBlock;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static cn.qiuye.gtmoremachine.common.block.BlockMap.MAP;
import static net.minecraft.network.chat.Component.translatable;

public class AdvancedTerminalBehavior implements IItemUIFactory {

    private static final String repeatCount = "repeatCount";
    private static final String ReplaceMode = "isReplaceMode";
    private static final String DemolitionMode = "isDemolitionMode";
    private static final String UseAEMode = "isUseAEMode";
    private static final String FlipMode = "isFlipMode";
    private static final String NoHatch = "isNoHatchMode";

    public AdvancedTerminalBehavior() {}

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            var level = context.getLevel();
            var blockPos = context.getClickedPos();
            var metaMachine = MetaMachine.getMachine(level, blockPos);
            if (context.getPlayer() != null && !level.isClientSide() &&
                    metaMachine instanceof MultiblockControllerMachine controller) {
                var autoBuildSetting = getAutoBuildSetting(context.getPlayer().getMainHandItem());

                if (GTmm.Mods.isAE2Loaded()) {
                    if (!controller.isFormed()) {
                        AdvancedBlockPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                    } else if (metaMachine instanceof WorkableMultiblockMachine machine && (autoBuildSetting.isReplaceMode() || autoBuildSetting.isUseDemolish())) {
                        AdvancedBlockPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                        machine.onPartUnload();
                    }
                } else {
                    if (!controller.isFormed()) {
                        AdvancedBlockNoAEPattern.getAdvancedBlockPattern(controller.getPattern()).autoBuild(context.getPlayer(), controller.getMultiblockState(), autoBuildSetting);
                    } else if (metaMachine instanceof WorkableMultiblockMachine machine && (autoBuildSetting.isReplaceMode() || autoBuildSetting.isUseDemolish())) {
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
            autoBuildSetting.setFlipped(tag.getBoolean("IsUseMirror"));
            autoBuildSetting.setUseDemolish(tag.getBoolean("IsUseDemolish"));
            String block = tag.getString("blocks");
            if (!block.isEmpty()) {
                autoBuildSetting.setTierBlock(MAP.get(block));
                autoBuildSetting.setBlocks(new ObjectOpenHashSet<>(autoBuildSetting.tierBlock));
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
                        () -> getIntTag(handItem, repeatCount), (v) -> setIntTag(handItem, repeatCount, v))
                        .setMin(0).setMax(1000))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.3"))
                        .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.3.tooltip")))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setBooleanTag(handItem, NoHatch, v))
                        .setPressed(getBooleanTag(handItem, NoHatch))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.4"))
                        .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.4.tooltip")))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setBooleanTag(handItem, ReplaceMode, v))
                        .setPressed(getBooleanTag(handItem, ReplaceMode))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable("item.gtmoremachine.advanced_terminal.setting.5"))
                        .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.5.tooltip")))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setBooleanTag(handItem, UseAEMode, v))
                        .setPressed(getBooleanTag(handItem, UseAEMode))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new LabelWidget(4, 5 + 16 * rowIndex, "item.gtmoremachine.advanced_terminal.setting.6")
                        .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.6.tooltip")))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setBooleanTag(handItem, FlipMode, v))
                        .setPressed(getBooleanTag(handItem, FlipMode))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))))
                .addWidget(new LabelWidget(4, 5 + 16 * rowIndex, "item.gtmoremachine.advanced_terminal.setting.7")
                        .setHoverTooltips(Component.translatable("item.gtmoremachine.advanced_terminal.setting.7.tooltip")))
                .addWidget(new SwitchWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        (c, v) -> setBooleanTag(handItem, DemolitionMode, v))
                        .setPressed(getBooleanTag(handItem, DemolitionMode))
                        .setTexture(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("OFF")),
                                new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("ON"))));
        var blockLabel = new ExtendLabelWidget(40, 21, getBlockComponent(handItem));
        var blockMap = new BlockMapSelectorWidget(group.getSizeHeight() + 4, contain.getSizeWidth(), (s, i) -> {
            if (s != null && i != null) {
                var tag = handItem.getOrCreateTag();
                tag.putString("blocks", s);
                tag.putInt("Tier", i);
                handItem.setTag(tag);
                blockLabel.setComponent(Component.literal(" (").append(translatable(BlockMap.blockmap_valuePrefix + "." + s))
                        .append(Component.literal(" : "))
                        .append(MAP.get(s)[i].getName())
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
                return Component.literal("(").append(translatable(BlockMap.blockmap_valuePrefix + "." + block))
                        .append(Component.literal(" : "))
                        .append(MAP.get(block)[tier].getName())
                        .append(Component.literal(")"));
            }
        }
        return Component.literal("");
    }

    private int getIntTag(ItemStack itemStack, String key) {
        return getIntTag(itemStack, key, 0);
    }

    private int getIntTag(ItemStack itemStack, String key, int def) {
        var tag = itemStack.getOrCreateTag();
        return tag.contains(key) ? tag.getInt(key) : def;
    }

    private void setIntTag(ItemStack itemStack, String key, int value) {
        itemStack.getOrCreateTag().putInt(key, value);
    }

    private boolean getBooleanTag(ItemStack itemStack, String key) {
        return getBooleanTag(itemStack, key, false);
    }

    private boolean getBooleanTag(ItemStack itemStack, String key, boolean def) {
        var tag = itemStack.getOrCreateTag();
        return tag.contains(key) ? tag.getBoolean(key) : def;
    }

    private void setBooleanTag(ItemStack itemStack, String key, boolean value) {
        itemStack.getOrCreateTag().putBoolean(key, value);
    }

    @Setter
    @Getter
    public static class AutoBuildSetting {

        Object2IntOpenHashMap<String> tierBlocks = new Object2IntOpenHashMap<>();

        Block[] tierBlock;
        Set<Block> blocks = Collections.emptySet();
        private int Tier, repeatCount;
        private boolean noHatchMode, replaceMode, isUseAE, isFlipped, isUseDemolish;

        private AutoBuildSetting() {
            this.Tier = 0;
            this.repeatCount = 0;
            this.noHatchMode = true;
            this.replaceMode = false;
            this.isUseAE = false;
            this.isFlipped = false;
            this.isUseDemolish = false;
        }

        public List<ItemStack> apply(BlockInfo[] blockInfos) {
            List<ItemStack> candidates = new ObjectArrayList<>();
            if (blockInfos != null && blockInfos.length > 0) {
                if (this.tierBlocks != null && this.tierBlock.length > 1) {
                    for (var info : blockInfos) {
                        Block block = info.getBlockState().getBlock();
                        String tierBlocks = BlockMap.getCategory(block);
                        if (this.tierBlocks.getInt(tierBlocks) > 0 && this.blocks.contains(block)) {
                            Block[] blocks1 = MAP.get(tierBlocks);
                            if (blocks1 != null && blocks1.length > 0) {
                                Block block2 = blocks1[Math.min(blocks1.length, this.tierBlocks.getInt(tierBlocks)) - 1];
                                return Collections.singletonList(new ItemStack(block2));
                            }
                        }
                    }
                }
                for (BlockInfo blockInfo : blockInfos) {
                    Block block = blockInfo.getBlockState().getBlock();
                    if (block instanceof LiquidBlock fluidBlock) candidates.add(fluidBlock.getFluid().getBucket().getDefaultInstance());
                    else if (block != Blocks.AIR) candidates.add(block.asItem().getDefaultInstance());
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
