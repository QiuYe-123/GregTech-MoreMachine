package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.gui.monitor.*;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignComponentPanelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget;
import cn.qiuye.gtmoremachine.api.item.ModularHUD;
import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.IWirelessMonitor;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.utils.FormattingUtil;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.nbt.TagUtils;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.component.IItemHUDProvider;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

import static cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget.ALIGN_CENTER;
import static cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyMonitor.DISPLAY_TEXT_WIDTH;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessEnergyTerminalBehavior implements IItemUIFactory, IItemHUDProvider, IWirelessEnergyContainerHolder {

    @OnlyIn(Dist.CLIENT)
    private ModularHUD hud;
    private UUID uuid;
    public static int p;
    public static BlockPos pPos;
    @Nullable
    @Getter
    @Setter
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    private final String statistics = "statistics",
            format = "format",
            powerstatus = "powerStatus",
            sortingrules = "sortingrules",
            type = "type";

    /**
     * 构造函数，初始化HUD显示
     */
    public WirelessEnergyTerminalBehavior() {
        if (GTmm.isClientSide() && !GTmm.isDataGen()) {
            this.hud = new ModularHUD();
        }
    }

    // ==================== 构造函数和初始化 ====================

    // ==================== GUI 相关 ====================

    /**
     * 处理显示界面的点击事件
     *
     * @param stack         物品堆
     * @param componentData 组件数据
     * @param clickData     点击数据
     */
    private void handleDisplayClick(ItemStack stack, String componentData, ClickData clickData) {
        if (componentData.equals(statistics)) {
            if (!clickData.isRemote) {
                setStatistics((getStatistics(stack) == Statistics.Team) ? Statistics.Global : Statistics.Team, stack);
            }
        } else if (componentData.equals(format)) {
            if (!clickData.isRemote) {
                setFormat((getFormat(stack) == Format.Unit) ? Format.Science : Format.Unit, stack);
            }
        } else if (componentData.equals(powerstatus)) {
            if (!clickData.isRemote) {
                // 循环切换PowerStatus：All -> In -> Out -> All
                switch (getPowerStatus(stack)) {
                    case All -> setPowerStatus(Status.In, stack);
                    case In -> setPowerStatus(Status.Out, stack);
                    case Out -> setPowerStatus(Status.All, stack);
                }
            }
        } else if (componentData.equals(sortingrules)) {
            if (!clickData.isRemote) {
                setSortingrules((getSortingrules(stack) == Sorting.Ascending) ? Sorting.Descendingorder : Sorting.Ascending, stack);
            }
        } else if (componentData.equals(type)) {
            if (!clickData.isRemote) {
                switch (getType(stack)) {
                    case PowerInteraction -> setType(Type.Capacitycomponent, stack);
                    case Capacitycomponent -> setType(Type.RelayNode, stack);
                    case RelayNode -> setType(Type.PowerInteraction, stack);
                }
            }
        } else if (clickData.isRemote) {
            p = 200;
            String[] parts = componentData.split(", ");
            pPos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
    }

    /**
     * 创建UI界面
     *
     * @param holder       持有物品的UI工厂
     * @param entityPlayer 玩家
     * @return 模块化UI
     */
    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        final var handItem = entityPlayer.getMainHandItem();
        if (!TagUtils.hasTagKey("UUID", handItem)) {
            TagUtils.setUUID(entityPlayer.getUUID(), handItem);
        }
        return new ModularUI(DISPLAY_TEXT_WIDTH + 8 + 8, 117 + 8 + 8 + 8 + 17, holder, entityPlayer).widget(createWidget(handItem, holder.getHeld().getDescriptionId(), new WirelessMonitor(entityPlayer.getUUID(), entityPlayer.level())));
    }

    /**
     * 创建UI部件
     *
     * @param stack         物品堆
     * @param descriptionId 描述ID
     * @param monitor       无线能量监视器
     * @return UI部件组
     */
    private Widget createWidget(ItemStack stack, String descriptionId, WirelessMonitor monitor) {
        var group = new WidgetGroup(0, 0, DISPLAY_TEXT_WIDTH + 8 + 8, 117 + 8 + 8 + 8 + 17);
        Widget label = new AlignLabelWidget(DISPLAY_TEXT_WIDTH / 2 + 4, 5, descriptionId).setTextAlign(ALIGN_CENTER);
        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, DISPLAY_TEXT_WIDTH + 8, 117 + 8 + 8 + 17)
                        .setBackground(GuiTextures.DISPLAY)
                        .setYScrollBarWidth(2)
                        .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1))
                        .addWidget(label)
                        .addWidget(new AlignComponentPanelWidget(4, 17, text -> addDisplayText(text, monitor, stack))
                                .setMaxWidthLimit(DISPLAY_TEXT_WIDTH)
                                .clickHandler((componentData, clickData) -> handleDisplayClick(stack, componentData, clickData))
                                .setSplitChar(".")));

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    /**
     * 添加显示文本到列表
     *
     * @param textList 文本列表
     * @param monitor  无线能量监视器
     * @param stack    物品堆
     */
    private void addDisplayText(List<Component> textList, WirelessMonitor monitor, ItemStack stack) {
        if (monitor.isRemote()) return;
        if (monitor.displayTextCache == null || monitor.level.getServer().getTickCount() % 10 == 0) {
            monitor.displayTextCache = monitor.getDisplayText(getStatistics(stack), getFormat(stack), getPowerStatus(stack), getSortingrules(stack), getType(stack));
        }
        textList.addAll(monitor.displayTextCache);
    }

    // ==================== ModularHUD 相关 ====================

    /**
     * 绘制HUD信息
     *
     * @param stack       物品堆
     * @param guiGraphics GUI图形对象
     */
    @Override
    public void drawHUD(ItemStack stack, GuiGraphics guiGraphics) {
        UUID uuid = TagUtils.getUUID(stack);
        BigDecimal all;
        BigDecimal in;
        BigDecimal out;
        BigInteger storage;
        all = in = out = BigDecimal.ZERO;
        storage = BigInteger.ZERO;
        if (uuid != null) {
            this.uuid = uuid;
            var container = getWirelessEnergyContainer();
            if (container != null) {
                all = container.getAllEnergyStat().getAvg();
                in = container.getInEnergyStat().getAvg();
                out = container.getOutEnergyStat().getAvg();
                storage = container.getStorage();
            }
        }
        this.hud.newString(Component.translatable("item.gtmoremachine.wireless_energy_terminal.hud.1",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(all)).withStyle(ChatFormatting.DARK_PURPLE),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(all))),
                FormattingUtil.voltageName(all)));
        this.hud.newString(Component.translatable("item.gtmoremachine.wireless_energy_terminal.hud.2",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(in)).withStyle(ChatFormatting.DARK_BLUE),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(in))),
                FormattingUtil.voltageName(in)));
        this.hud.newString(Component.translatable("item.gtmoremachine.wireless_energy_terminal.hud.3",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(out)).withStyle(ChatFormatting.DARK_RED),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(out))),
                FormattingUtil.voltageName(out)));
        this.hud.newString(Component.translatable("item.gtmoremachine.wireless_energy_terminal.hud.4",
                Component.literal(NumberUtils.formatBigIntegerNumberOrSic(storage)).withStyle(ChatFormatting.DARK_AQUA),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(new BigDecimal(storage)))),
                FormattingUtil.voltageName(new BigDecimal(storage))));
        this.hud.draw(guiGraphics);
        this.hud.reset();
    }

    // ==================== 数据存储相关 (NBT) ====================

    /**
     * 设置统计模式
     *
     * @param enumtype 统计模式
     * @param stack    物品堆
     */
    private void setStatistics(Statistics enumtype, ItemStack stack) {
        TagUtils.setEnumTag(statistics, enumtype, stack);
    }

    /**
     * 获取统计模式
     *
     * @param stack 物品堆
     * @return 当前统计模式
     */
    private Statistics getStatistics(ItemStack stack) {
        return TagUtils.getEnumTag(statistics, stack, Statistics.class, Statistics.Companion);
    }

    /**
     * 设置数字格式
     *
     * @param enumtype 数字格式
     * @param stack    物品堆
     */
    private void setFormat(Format enumtype, ItemStack stack) {
        TagUtils.setEnumTag(format, enumtype, stack);
    }

    /**
     * 获取数字格式
     *
     * @param stack 物品堆
     * @return 当前数字格式
     */
    private Format getFormat(ItemStack stack) {
        return TagUtils.getEnumTag(format, stack, Format.class, Format.Companion);
    }

    /**
     * 设置电力状态过滤
     *
     * @param enumtype 电力状态
     * @param stack    物品堆
     */
    private void setPowerStatus(Status enumtype, ItemStack stack) {
        TagUtils.setEnumTag(powerstatus, enumtype, stack);
    }

    /**
     * 获取电力状态过滤
     *
     * @param stack 物品堆
     * @return 当前电力状态
     */
    private Status getPowerStatus(ItemStack stack) {
        return TagUtils.getEnumTag(powerstatus, stack, Status.class, Status.Companion);
    }

    /**
     * 设置排序规则
     *
     * @param enumtype 排序规则
     * @param stack    物品堆
     */
    private void setSortingrules(Sorting enumtype, ItemStack stack) {
        TagUtils.setEnumTag(sortingrules, enumtype, stack);
    }

    /**
     * 获取排序规则
     *
     * @param stack 物品堆
     * @return 当前排序规则
     */
    private Sorting getSortingrules(ItemStack stack) {
        return TagUtils.getEnumTag(sortingrules, stack, Sorting.class, Sorting.Companion);
    }

    /**
     * 设置显示的设备类型
     *
     * @param enumtype 设备类型
     * @param stack    物品堆
     */
    private void setType(Type enumtype, ItemStack stack) {
        TagUtils.setEnumTag(type, enumtype, stack);
    }

    /**
     * 获取显示的设备类型
     *
     * @param stack 物品堆
     * @return 当前显示的设备类型
     */
    private Type getType(ItemStack stack) {
        return TagUtils.getEnumTag(type, stack, Type.class, Type.Companion);
    }

    // ==================== UUID 相关 ====================

    /**
     * 获取UUID
     *
     * @return UUID
     */
    @Override
    public @Nullable UUID getUUID() {
        return this.uuid;
    }

    // ==================== 内部类 ====================

    /**
     * 无线能量监视器内部类
     */
    private static class WirelessMonitor implements IWirelessMonitor {

        private WirelessMonitor(UUID uuid, Level level) {
            this.uuid = uuid;
            this.level = level;
        }

        /**
         * 检查是否为客户端
         *
         * @return 如果是客户端返回true
         */
        private boolean isRemote() {
            return GTmm.isClientThread();
        }

        private final UUID uuid;
        private final Level level;

        private List<Component> displayTextCache;

        @Getter
        @Setter
        private WirelessEnergyContainer WirelessEnergyContainerCache;

        /**
         * 获取缓存的玩家/团队UUID
         *
         * @return UUID
         */
        @Override
        public @Nullable UUID getUUID() {
            return uuid;
        }

        /**
         * 是否显示
         *
         * @return 返回false
         */
        @Override
        public boolean display() {
            return false;
        }

        /**
         * 获取当前世界
         *
         * @return 世界
         */
        @Override
        public Level getLevel() {
            return level;
        }
    }
}
