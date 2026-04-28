package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.GTMMValues;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.gui.widget.ExtendLabelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.PatternWidgetGroup;
import cn.qiuye.gtmoremachine.api.gui.widget.TerminalInputWidget;
import cn.qiuye.gtmoremachine.api.pattern.AdvancedBlockNoAEPattern;
import cn.qiuye.gtmoremachine.api.pattern.AdvancedBlockPattern;
import cn.qiuye.gtmoremachine.api.pattern.Hatch;
import cn.qiuye.gtmoremachine.common.block.BlockMap;
import cn.qiuye.gtmoremachine.common.data.GTMMDataComponents;
import cn.qiuye.gtmoremachine.common.item.datacomponents.AdvancedTerminalData;

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

@GTMMDataGeneratorScanned
public class AdvancedTerminalBehavior implements IItemUIFactory {

    private static final String repeatCount = "repeatCount";
    private static final String ReplaceMode = "isReplaceMode";
    private static final String DemolitionMode = "isDemolitionMode";
    private static final String UseAEMode = "isUseAEMode";
    private static final String FlipMode = "isFlipMode";
    private static final String NoHatch = "isNoHatchMode";

    @GTMMRegisterLanguage(cn = "高级终端设置", en = "§bAdvanced Terminal")
    private static final String TITLE = "item.gtmoremachine.advanced_terminal.setting.title";
    @GTMMRegisterLanguage(cn = "重复结构次数", en = "Number of repetitions of the structure")
    private static final String S1 = "item.gtmoremachine.advanced_terminal.setting.1";
    @GTMMRegisterLanguage(cn = "用于设置可重复结构(蒸馏塔、装配线等)的重复部分放置次数", en = "Used to set the number of repetitions for the placement of repeating parts in structures like distillation towers, assembly lines, etc.")
    private static final String S1T = "item.gtmoremachine.advanced_terminal.setting.1.tooltip";
    @GTMMRegisterLanguage(cn = "无仓室模式", en = "No Hatch mode")
    private static final String S2 = "item.gtmoremachine.advanced_terminal.setting.2";
    @GTMMRegisterLanguage(cn = "是否启用无仓室模式(暗:不启用,亮:启用)\n启用无仓室模式后不会在非唯一时放置各种仓室。", en = "Whether to enable the no-Hatch mode (Dark: not enabled, Bright: enabled)\nAfter enabling the no-Hatch mode, various Hatches will not be placed when they are not unique.")
    private static final String S2T = "item.gtmoremachine.advanced_terminal.setting.2.tooltip";
    @GTMMRegisterLanguage(cn = "替换模式", en = "Replace mode")
    private static final String S3 = "item.gtmoremachine.advanced_terminal.setting.3";
    @GTMMRegisterLanguage(cn = "是否启用等级方块替换模式(OFF:不启用,ON:启用)\n启用等级方块替换模式会将所有线圈替换为等级方块中指定的等级。", en = "Whether to enable the Tier Block replace mode (OFF: not enabled, ON: enabled)\nAfter enabling the Tier Block replace mode, all coils will be replaced by the Tier Block specified in the Tier Block setting.")
    private static final String S3T = "item.gtmoremachine.advanced_terminal.setting.3.tooltip";
    @GTMMRegisterLanguage(cn = "使用AE物品", en = "Use AE Items")
    private static final String S4 = "item.gtmoremachine.advanced_terminal.setting.4";
    @GTMMRegisterLanguage(cn = "是否使用AE物品(OFF:不使用,ON:使用)\n使用AE物品开启后，会通过背包中的AE终端连接到相应的AE网络并使用其中的物品来进行建造。", en = "Whether to use AE items (OFF: not use, ON: use)\nAfter enabling 'Use AE Items', it will connect to the corresponding AE network via the AE Terminal in the inventory and use the items from it for construction.")
    private static final String S4T = "item.gtmoremachine.advanced_terminal.setting.4.tooltip";
    @GTMMRegisterLanguage(cn = "镜像搭建", en = "Mirror Building")
    private static final String S5 = "item.gtmoremachine.advanced_terminal.setting.5";
    @GTMMRegisterLanguage(cn = "是否启用镜像搭建(OFF:不启用,ON:启用)\n启用镜像搭建后，会将所有结构的镜像进行搭建。", en = "Whether to enable Mirror Building (OFF: not enabled, ON: enabled)\nAfter enabling Mirror Building, it will build the mirror image of all structures.")
    private static final String S5T = "item.gtmoremachine.advanced_terminal.setting.5.tooltip";
    @GTMMRegisterLanguage(cn = "拆除模式", en = "Dismantle mode")
    private static final String S6 = "item.gtmoremachine.advanced_terminal.setting.6";
    @GTMMRegisterLanguage(cn = "是否启用拆除模式(OFF:不启用,ON:启用)\n启用拆除模式后，会将设定好重复结构次数的部分结构进行拆除。\n警告:会将结构内的所有方块拆除。", en = "Whether to enable Dismantle mode (OFF: not enabled, ON: enabled)\nAfter enabling Dismantle mode, it will dismantle the structural parts for which the number of repetitions has been set.\nWarning: All blocks within the structure will be dismantled.")
    private static final String S6T = "item.gtmoremachine.advanced_terminal.setting.6.tooltip";

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
                if (autoBuildSetting.isDemolitionMode()) {
                    mcahine.onStructureInvalid();
                    mcahine.onPartUnload();
                } else {
                    mcahine.checkPatternWithTryLock();
                }
                if (GTmm.Mods.isAE2Loaded()) {
                    AdvancedBlockPattern.getAdvancedBlockPattern(pattern).autoBuild(player, mcahine.getMultiblockState(), autoBuildSetting);
                } else {
                    AdvancedBlockNoAEPattern.getAdvancedBlockPattern(pattern).autoBuild(player, mcahine.getMultiblockState(), autoBuildSetting);
                }
                if (!autoBuildSetting.isDemolitionMode()) {
                    mcahine.checkPatternWithTryLock();
                    mcahine.onPartUnload();
                }
            }
        } else if (level.isClientSide) {
            MultiblockInWorldPreviewRenderer.showPreview(pos, mcahine, ConfigHolder.INSTANCE.client.inWorldPreviewDuration * 20);
        }
    }

    private AutoBuildSetting getAutoBuildSetting(ItemStack mainHandItem) {
        var autoBuildSetting = new AutoBuildSetting();
        var terminalData = getTerminalData(mainHandItem);
        autoBuildSetting.setRepeatCount(terminalData.repeatCount());
        autoBuildSetting.setReplaceMode(terminalData.replaceMode());
        autoBuildSetting.setDemolitionMode(terminalData.demolitionMode());
        autoBuildSetting.setUseAEMode(terminalData.useAEMode());
        autoBuildSetting.setFlipMode(terminalData.flipMode());
        autoBuildSetting.setNoHatchMode(terminalData.noHatchMode());
        if (!terminalData.tierBlocks().isEmpty()) {
            ReferenceOpenHashSet<Block> blockSet = new ReferenceOpenHashSet<>();
            for (String key : BlockMap.MAP.keySet()) {
                Block[] blockArray = BlockMap.MAP.get(key);
                int blocktier = terminalData.tierBlocks().getOrDefault(key, 0);
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
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 133)
                .setUseScissor(false)
                .setBackground(GuiTextures.DISPLAY)
                .setYScrollBarWidth(2)
                .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1))
                .addWidget(new ExtendLabelWidget(65, 8, Component.translatable(TITLE)))
                .addWidget(new PatternWidgetGroup(96, 4, 76, 12, b -> group.setSelfPositionX(b ? -70 : 0),
                        () -> getTerminalData(handItem).tierBlocks(),
                        (category, index) -> toggleTierBlock(handItem, category, index)))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable(S1))
                        .setHoverTooltips(Component.translatable(S1T)))
                .addWidget(new TerminalInputWidget(140, 5 + 16 * rowIndex++, 25, 16,
                        () -> getTerminalData(handItem).repeatCount(), v -> setRepeatCount(handItem, v))
                        .setMin(0).setMax(1000))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable(S2))
                        .setHoverTooltips(Component.translatable(S2T)))
                .addWidget(Button(140, 5 + 16 * rowIndex++, () -> getTerminalData(handItem).noHatchMode(), v -> setNoHatchMode(handItem, v)))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable(S3))
                        .setHoverTooltips(Component.translatable(S3T)))
                .addWidget(Button(140, 5 + 16 * rowIndex++, () -> getTerminalData(handItem).replaceMode(), v -> setReplaceMode(handItem, v)))
                .addWidget(new ExtendLabelWidget(4, 5 + 16 * rowIndex, Component.translatable(S4))
                        .setHoverTooltips(Component.translatable(S4T)))
                .addWidget(Button(140, 5 + 16 * rowIndex++, () -> getTerminalData(handItem).useAEMode(), v -> setUseAEMode(handItem, v)))
                .addWidget(new LabelWidget(4, 5 + 16 * rowIndex, Component.translatable(S5))
                        .setHoverTooltips(Component.translatable(S5T)))
                .addWidget(Button(140, 5 + 16 * rowIndex++, () -> getTerminalData(handItem).flipMode(), v -> setFlipMode(handItem, v)))
                .addWidget(new LabelWidget(4, 5 + 16 * rowIndex, Component.translatable(S6))
                        .setHoverTooltips(Component.translatable(S6T)))
                .addWidget(Button(140, 5 + 16 * rowIndex++, () -> getTerminalData(handItem).demolitionMode(), v -> setDemolitionMode(handItem, v))));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    private static SwitchWidget Button(int x, int y, BooleanSupplier get, BooleanConsumer set) {
        var widget = new SwitchWidget(x - 2, y - 2, 16, 16, null);
        BooleanConsumer consumer = bol -> widget.setHoverTooltips(bol ? GTMMValues.ENABLED : GTMMValues.DISABLED);
        widget.setOnPressCallback((c, v) -> {
            if (!c.isRemote) set.accept(v.booleanValue());
            else consumer.accept(v.booleanValue());
        });
        widget.setPressed(get.getAsBoolean())
                .setBaseTexture(GuiTextures.BUTTON, GuiTextures.PROGRESS_BAR_SOLAR_STEAM.get(true).copy().getSubTexture(0.0, 0.0, 1.0, 0.5).scale(0.8F))
                .setPressedTexture(GuiTextures.BUTTON, GuiTextures.PROGRESS_BAR_SOLAR_STEAM.get(true).copy().getSubTexture(0.0, 0.5, 1.0, 0.5).scale(0.8F));
        consumer.accept(get.getAsBoolean());
        return widget;
    }

    private static AdvancedTerminalData getTerminalData(ItemStack stack) {
        return stack.getOrDefault(GTMMDataComponents.ADVANCED_TERMINAL.get(), AdvancedTerminalData.DEFAULT);
    }

    private static void setTerminalData(ItemStack stack, AdvancedTerminalData data) {
        stack.set(GTMMDataComponents.ADVANCED_TERMINAL.get(), data);
    }

    private void setRepeatCount(ItemStack itemStack, int value) {
        setTerminalData(itemStack, getTerminalData(itemStack).withRepeatCount(value));
    }

    private void setReplaceMode(ItemStack itemStack, boolean value) {
        setTerminalData(itemStack, getTerminalData(itemStack).withReplaceMode(value));
    }

    private void setDemolitionMode(ItemStack itemStack, boolean value) {
        setTerminalData(itemStack, getTerminalData(itemStack).withDemolitionMode(value));
    }

    private void setUseAEMode(ItemStack itemStack, boolean value) {
        setTerminalData(itemStack, getTerminalData(itemStack).withUseAEMode(value));
    }

    private void setFlipMode(ItemStack itemStack, boolean value) {
        setTerminalData(itemStack, getTerminalData(itemStack).withFlipMode(value));
    }

    private void setNoHatchMode(ItemStack itemStack, boolean value) {
        setTerminalData(itemStack, getTerminalData(itemStack).withNoHatchMode(value));
    }

    private void toggleTierBlock(ItemStack itemStack, String category, int index) {
        if (category == null || index == 0) {
            return;
        }
        setTerminalData(itemStack, getTerminalData(itemStack).toggleTierBlock(category, index));
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

        public Block[] getPlaceableCandidates(Block[] blocks, boolean allowHatches) {
            if (blocks == null || blocks.length == 0) {
                return new Block[0];
            }

            List<Block> candidates = new ObjectArrayList<>(blocks.length);
            for (Block block : blocks) {
                if (block == null) {
                    continue;
                }
                if (!allowHatches && this.noHatchMode && isHatch(block)) {
                    continue;
                }
                candidates.add(block);
            }
            return candidates.toArray(Block[]::new);
        }

        private boolean isHatch(Block block) {
            return block instanceof MetaMachineBlock machineBlock && Hatch.getBlockSet().contains(machineBlock);
        }
    }
}
