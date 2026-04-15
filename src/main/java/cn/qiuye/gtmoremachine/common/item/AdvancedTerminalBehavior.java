package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.GTMMValues;
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
import com.gregtechceu.gtceu.client.renderer.MultiblockInWorldPreviewRenderer;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.BooleanSupplier;

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
        var player = context.getPlayer();
        var level = context.getLevel();
        var pos = context.getClickedPos();
        if (player != null && MetaMachine.getMachine(level, pos) instanceof MultiblockControllerMachine multi) {
            var AutoBuildSetting = getAutoBuildSetting(itemStack);
            this.Useing(AutoBuildSetting, multi, player, level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void Useing(AutoBuildSetting autoBuildSetting, MultiblockControllerMachine mcahine, Player player, Level level, BlockPos pos) {
        var pattern = mcahine.getPattern();
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                mcahine.checkPatternWithTryLock();
                if (GTmm.Mods.isAE2Loaded()) {
                    AdvancedBlockPattern.getAdvancedBlockPattern(pattern).autoBuild(player, mcahine.getMultiblockState(), autoBuildSetting);
                } else {
                    AdvancedBlockNoAEPattern.getAdvancedBlockPattern(pattern).autoBuild(player, mcahine.getMultiblockState(), autoBuildSetting);
                }
                mcahine.onPartUnload();
            }
        } else if (level.isClientSide) {
            MultiblockInWorldPreviewRenderer.showPreview(pos, mcahine, ConfigHolder.INSTANCE.client.inWorldPreviewDuration * 20);
        }
    }

    private AutoBuildSetting getAutoBuildSetting(ItemStack mainHandItem) {
        var autoBuildSetting = new AutoBuildSetting();
        autoBuildSetting.setRepeatCount(getIntTag(mainHandItem, repeatCount));
        autoBuildSetting.setReplaceMode(getBooleanTag(mainHandItem, ReplaceMode));
        autoBuildSetting.setDemolitionMode(getBooleanTag(mainHandItem, DemolitionMode));
        autoBuildSetting.setUseAEMode(getBooleanTag(mainHandItem, UseAEMode));
        autoBuildSetting.setFlipMode(getBooleanTag(mainHandItem, FlipMode));
        autoBuildSetting.setNoHatchMode(getBooleanTag(mainHandItem, NoHatch, true));
        var blocks = mainHandItem.getOrCreateTag().getCompound("blocks");
        if (!blocks.isEmpty()) {
            ReferenceOpenHashSet<Block> blockSet = new ReferenceOpenHashSet<>();
            for (String key : BlockMap.MAP.keySet()) {
                Block[] blockArray = BlockMap.MAP.get(key);
                int blocktier = blocks.getInt(key);
                if (blocktier > 0 && blocktier <= blockArray.length) {
                    blockSet.addAll(Arrays.asList(blockArray));
                }
                autoBuildSetting.getTierBlocks().put(key, blocktier);
            }
            autoBuildSetting.setBlocks(blockSet);
        }
        return autoBuildSetting;
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        return new ModularUI(176, 166, heldItemHolder, player).widget(createWidget(heldItemHolder.getHeld()));
    }

    private Widget createWidget(final ItemStack handItem) {
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

    private static SwitchWidget Button(int x, int y, BooleanSupplier get, BooleanConsumer set) {
        var widget = new SwitchWidget(x - 2, y - 2, 16, 16, null);
        BooleanConsumer consumer = bol -> widget.setHoverTooltips(bol ? GTMMValues.ENABLED : GTMMValues.DISABLED);
        widget.setOnPressCallback((c, v) -> {
            if (!c.isRemote) set.accept(v.booleanValue());
            else consumer.accept(v.booleanValue());
        });
        widget.setPressed(get.getAsBoolean())
                .setBaseTexture(
                        GuiTextures.BUTTON, GuiTextures.PROGRESS_BAR_SOLAR_STEAM.get(true).copy().getSubTexture(0.0, 0.0, 1.0, 0.5).scale(0.8F))
                .setPressedTexture(
                        GuiTextures.BUTTON, GuiTextures.PROGRESS_BAR_SOLAR_STEAM.get(true).copy().getSubTexture(0.0, 0.5, 1.0, 0.5).scale(0.8F));
        consumer.accept(get.getAsBoolean());
        return widget;
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

        private Object2IntOpenHashMap<String> tierBlocks = new Object2IntOpenHashMap<>();

        private Set<Block> blocks = Collections.emptySet();
        private int repeatCount;
        private boolean noHatchMode, replaceMode, useAEMode, flipMode, demolitionMode;

        private AutoBuildSetting() {
            this.repeatCount = 0;
            this.noHatchMode = true;
            this.replaceMode = false;
            this.useAEMode = false;
            this.flipMode = false;
            this.demolitionMode = false;
        }

        public List<Block> apply(Block[] blocks) {
            if (blocks == null || blocks.length == 0) {
                return Collections.emptyList();
            }
            if (this.tierBlocks != null && blocks.length > 1) {
                for (Block block : blocks) {
                    String category = BlockMap.getCategory(block);
                    int tier = this.tierBlocks.getInt(category);
                    if (tier > 0 && this.blocks.contains(block)) {
                        Block[] tieredBlocks = MAP.get(category);
                        if (tieredBlocks != null && tieredBlocks.length > 0) {
                            Block selected = tieredBlocks[Math.min(tieredBlocks.length, tier) - 1];
                            return Collections.singletonList(selected);
                        }
                    }
                }
            }
            // 未命中等级匹配逻辑，过滤空气方块并返回
            List<Block> candidates = new ObjectArrayList<>();
            for (Block block : blocks) {
                if (block != Blocks.AIR) {
                    candidates.add(block);
                }
            }
            return candidates;
        }

        public boolean isPlaceHatch(Block[] blocks) {
            if (!this.noHatchMode) return true;
            if (blocks != null && blocks.length > 0) {
                var blockInfo = blocks[0];
                return !(blockInfo instanceof MetaMachineBlock machineBlock) ||
                        !Hatch.getBlockSet().contains(machineBlock);
            }
            return true;
        }
    }
}
