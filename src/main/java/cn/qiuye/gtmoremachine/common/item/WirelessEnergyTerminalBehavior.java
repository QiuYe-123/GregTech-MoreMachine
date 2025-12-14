package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.api.gui.monitor.Sorting;
import cn.qiuye.gtmoremachine.api.gui.monitor.Statistics;
import cn.qiuye.gtmoremachine.api.gui.monitor.Status;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignComponentPanelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget;
import cn.qiuye.gtmoremachine.api.item.HUD;
import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.IWirelessMonitor;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.GTCEu;
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
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;

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
    private HUD hud;
    @DropSaved
    private UUID uuid;
    public static int p;
    public static BlockPos pPos;
    @DropSaved
    private Statistics statistics = Statistics.Team;
    @DropSaved
    private Format format = Format.Unit;
    @DropSaved
    private Status powerStatus = Status.All;
    @DropSaved
    private Sorting sortingrules = Sorting.Ascending;
    @Nullable
    @Getter
    @Setter
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    public WirelessEnergyTerminalBehavior() {
        if (GTCEu.isClientSide())
            hud = new HUD();
    }

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////
    private void handleDisplayClick(String componentData, ClickData clickData) {
        if (componentData.equals("statistics")) {
            if (!clickData.isRemote) {
                statistics = (statistics == Statistics.Team) ? Statistics.Global : Statistics.Team;
            }
        } else if (componentData.equals("format")) {
            if (!clickData.isRemote) {
                format = (format == Format.Unit) ? Format.Science : Format.Unit;
            }
        } else if (componentData.equals("powerStatus")) {
            if (!clickData.isRemote) {
                // 循环切换PowerStatus：All -> In -> Out -> All
                switch (powerStatus) {
                    case All -> powerStatus = Status.In;
                    case In -> powerStatus = Status.Out;
                    case Out -> powerStatus = Status.All;
                }
            }
        } else if (componentData.equals("sortingrules")) {
            if (!clickData.isRemote) {
                sortingrules = (sortingrules == Sorting.Ascending) ? Sorting.Descendingorder : Sorting.Ascending;
            }
        } else if (clickData.isRemote) {
            p = 200;
            String[] parts = componentData.split(", ");
            pPos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        this.uuid = entityPlayer.getUUID();
        return new ModularUI(DISPLAY_TEXT_WIDTH + 8 + 8, 117 + 8 + 8 + 8 + 17, holder, entityPlayer).widget(createWidget(holder.getHeld().getDescriptionId(), new WirelessMonitor(entityPlayer.getUUID(), entityPlayer.level())));
    }

    private Widget createWidget(String descriptionId, WirelessMonitor monitor) {
        var group = new WidgetGroup(0, 0, DISPLAY_TEXT_WIDTH + 8 + 8, 117 + 8 + 8 + 8 + 17);
        Widget label = new AlignLabelWidget(DISPLAY_TEXT_WIDTH / 2 + 4, 5, descriptionId).setTextAlign(ALIGN_CENTER);
        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, DISPLAY_TEXT_WIDTH + 8, 117 + 8 + 8 + 17)
                        .setBackground(GuiTextures.DISPLAY)
                        .setYScrollBarWidth(2)
                        .setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1))
                        .addWidget(label)
                        .addWidget(new AlignComponentPanelWidget(4, 17, text -> addDisplayText(text, monitor))
                                .setMaxWidthLimit(DISPLAY_TEXT_WIDTH)
                                .clickHandler(this::handleDisplayClick)
                                .setSplitChar(".")));

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    private void addDisplayText(List<Component> textList, WirelessMonitor monitor) {
        if (monitor.isRemote()) return;
        if (monitor.displayTextCache == null || monitor.level.getServer().getTickCount() % 10 == 0) {
            monitor.displayTextCache = monitor.getDisplayText(statistics, format, powerStatus, sortingrules);
        }
        textList.addAll(monitor.displayTextCache);
    }

    @Override
    public void drawHUD(ItemStack stack, GuiGraphics guiGraphics) {
        if (getUUID() == null) {
            this.hud.newString(Component.literal("0"));
            this.hud.newString(Component.literal("0"));
            this.hud.newString(Component.literal("0"));
            this.hud.newString(Component.literal("0"));
        } else {
            var container = getWirelessEnergyContainer();
            this.hud.newString(Component.literal(NumberUtils.formatBigDecimalNumberOrSic(container.getAllEnergyStat().getAvg())));
            this.hud.newString(Component.literal(NumberUtils.formatBigDecimalNumberOrSic(container.getInEnergyStat().getAvg())));
            this.hud.newString(Component.literal(NumberUtils.formatBigDecimalNumberOrSic(container.getOutEnergyStat().getAvg())));
            this.hud.newString(Component.literal(NumberUtils.formatBigIntegerNumberOrSic(container.getStorage())));
        }
        this.hud.draw(guiGraphics);
        this.hud.reset();
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.uuid;
    }

    private static class WirelessMonitor implements IWirelessMonitor {

        private WirelessMonitor(UUID uuid, Level level) {
            this.uuid = uuid;
            this.level = level;
        }

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
         * @return cached uuid of player/team
         */
        @Override
        public @Nullable UUID getUUID() {
            return uuid;
        }

        /**
         * @return false
         */
        @Override
        public boolean display() {
            return false;
        }

        /**
         * @return level
         */
        @Override
        public Level getLevel() {
            return level;
        }
    }
}
