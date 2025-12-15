package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.gui.monitor.*;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignComponentPanelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget;
import cn.qiuye.gtmoremachine.api.item.WirelessEnergyHUD;
import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.IWirelessMonitor;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.utils.NumberUtils;

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

import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

import static cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget.ALIGN_CENTER;
import static cn.qiuye.gtmoremachine.common.machine.electric.WirelessEnergyMonitor.DISPLAY_TEXT_WIDTH;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessEnergyTerminalBehavior implements IItemUIFactory, IItemHUDProvider, IWirelessEnergyContainerHolder {

    @OnlyIn(Dist.CLIENT)
    private WirelessEnergyHUD hud;
    private UUID uuid;
    public static int p;
    public static BlockPos pPos;
    @Nullable
    @Getter
    @Setter
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    /**
     * 构造函数，初始化HUD显示
     */
    public WirelessEnergyTerminalBehavior() {
        if (GTmm.isClientSide() && !GTmm.isDataGen()) {
            this.hud = new WirelessEnergyHUD();
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
        if (componentData.equals("statistics")) {
            if (!clickData.isRemote) {
                setStatistics((getStatistics(stack) == Statistics.Team) ? Statistics.Global : Statistics.Team, stack);
            }
        } else if (componentData.equals("format")) {
            if (!clickData.isRemote) {
                setFormat((getFormat(stack) == Format.Unit) ? Format.Science : Format.Unit, stack);
            }
        } else if (componentData.equals("powerStatus")) {
            if (!clickData.isRemote) {
                // 循环切换PowerStatus：All -> In -> Out -> All
                switch (getPowerStatus(stack)) {
                    case All -> setPowerStatus(Status.In, stack);
                    case In -> setPowerStatus(Status.Out, stack);
                    case Out -> setPowerStatus(Status.All, stack);
                }
            }
        } else if (componentData.equals("sortingrules")) {
            if (!clickData.isRemote) {
                setSortingrules((getSortingrules(stack) == Sorting.Ascending) ? Sorting.Descendingorder : Sorting.Ascending, stack);
            }
        } else if (componentData.equals("type")) {
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
        setUUID(entityPlayer.getUUID(), handItem);
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

    // ==================== WirelessEnergyHUD 相关 ====================

    /**
     * 绘制HUD信息
     *
     * @param stack       物品堆
     * @param guiGraphics GUI图形对象
     */
    @Override
    public void drawHUD(ItemStack stack, GuiGraphics guiGraphics) {
        if (getUUID(stack) == null) {
            this.hud.newString(Component.literal("0"));
            this.hud.newString(Component.literal("0"));
            this.hud.newString(Component.literal("0"));
            this.hud.newString(Component.literal("0"));
        } else {
            this.uuid = getUUID(stack);
            var container = getWirelessEnergyContainer();
            this.hud.newString(Component.literal(NumberUtils.formatBigDecimalNumberOrSic(container.getAllEnergyStat().getAvg())));
            this.hud.newString(Component.literal(NumberUtils.formatBigDecimalNumberOrSic(container.getInEnergyStat().getAvg())));
            this.hud.newString(Component.literal(NumberUtils.formatBigDecimalNumberOrSic(container.getOutEnergyStat().getAvg())));
            this.hud.newString(Component.literal(NumberUtils.formatBigIntegerNumberOrSic(container.getStorage())));
        }
        this.hud.draw(guiGraphics);
        this.hud.reset();
    }

    // ==================== 数据存储相关 (NBT) ====================

    /**
     * 设置统计模式
     *
     * @param statistics 统计模式
     * @param stack      物品堆
     */
    private void setStatistics(Statistics statistics, ItemStack stack) {
        var tag = stack.getOrCreateTag();
        tag.putString("statistics", statistics.toString());
        stack.setTag(tag);
    }

    /**
     * 获取统计模式
     *
     * @param stack 物品堆
     * @return 当前统计模式
     */
    private Statistics getStatistics(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("statistics")) {
            return Statistics.valueOf(tag.getString("statistics"));
        } else {
            return Statistics.Team;
        }
    }

    /**
     * 设置数字格式
     *
     * @param format 数字格式
     * @param stack  物品堆
     */
    private void setFormat(Format format, ItemStack stack) {
        var tag = stack.getOrCreateTag();
        tag.putString("format", format.toString());
        stack.setTag(tag);
    }

    /**
     * 获取数字格式
     *
     * @param stack 物品堆
     * @return 当前数字格式
     */
    private Format getFormat(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("format")) {
            return Format.valueOf(tag.getString("format"));
        } else {
            return Format.Unit;
        }
    }

    /**
     * 设置电力状态过滤
     *
     * @param powerStatus 电力状态
     * @param stack       物品堆
     */
    private void setPowerStatus(Status powerStatus, ItemStack stack) {
        var tag = stack.getOrCreateTag();
        tag.putString("powerStatus", powerStatus.toString());
        stack.setTag(tag);
    }

    /**
     * 获取电力状态过滤
     *
     * @param stack 物品堆
     * @return 当前电力状态
     */
    private Status getPowerStatus(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("powerStatus")) {
            return Status.valueOf(tag.getString("powerStatus"));
        } else {
            return Status.All;
        }
    }

    /**
     * 设置排序规则
     *
     * @param sortingrules 排序规则
     * @param stack        物品堆
     */
    private void setSortingrules(Sorting sortingrules, ItemStack stack) {
        var tag = stack.getOrCreateTag();
        tag.putString("sortingrules", sortingrules.toString());
        stack.setTag(tag);
    }

    /**
     * 获取排序规则
     *
     * @param stack 物品堆
     * @return 当前排序规则
     */
    private Sorting getSortingrules(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("sortingrules")) {
            return Sorting.valueOf(tag.getString("sortingrules"));
        } else {
            return Sorting.Ascending;
        }
    }

    /**
     * 设置显示的设备类型
     *
     * @param type  设备类型
     * @param stack 物品堆
     */
    private void setType(Type type, ItemStack stack) {
        var tag = stack.getOrCreateTag();
        tag.putString("type", type.toString());
        stack.setTag(tag);
    }

    /**
     * 获取显示的设备类型
     *
     * @param stack 物品堆
     * @return 当前显示的设备类型
     */
    private Type getType(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("type")) {
            return Type.valueOf(tag.getString("type"));
        } else {
            return Type.Capacitycomponent;
        }
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

    /**
     * 设置UUID到物品堆
     *
     * @param uuid      UUID
     * @param itemStack 物品堆
     */
    private void setUUID(UUID uuid, ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        tag.putUUID("UUID", uuid);
        itemStack.setTag(tag);
    }

    /**
     * 从物品堆获取UUID
     *
     * @param itemStack 物品堆
     * @return UUID
     */
    private @Nullable UUID getUUID(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();
        if (!tag.isEmpty() && tag.contains("UUID")) {
            return tag.getUUID("UUID");
        } else {
            return null;
        }
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
