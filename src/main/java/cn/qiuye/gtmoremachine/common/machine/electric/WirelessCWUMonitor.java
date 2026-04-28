package cn.qiuye.gtmoremachine.common.machine.electric;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.api.gui.monitor.Statistics;
import cn.qiuye.gtmoremachine.api.gui.monitor.Status;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignComponentPanelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.feature.IWirelessMonitor;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@GTMMDataGeneratorScanned
public class WirelessCWUMonitor extends MetaMachine implements IFancyUIMachine, IWirelessMonitor {

    private static final String WIRELESS_CWU_MONITOR_PREFIX = "gtmoremachine.machine.wireless_cwu_monitor";
    @GTMMRegisterLanguage(en = "Total CWU: %s CWU", cn = "CWU总量：%s CWU")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_1 = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Last minute: %s CWU", cn = "近一分钟：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_LAST_MINUTE = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.last_minute";
    @GTMMRegisterLanguage(en = "Last hour: %s CWU", cn = "近一小时：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_LAST_HOUR = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.last_hour";
    @GTMMRegisterLanguage(en = "Last day: %s CWU", cn = "近一天：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_LAST_DAY = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.last_day";
    @GTMMRegisterLanguage(en = "Now: %s CWU", cn = "当前：§a%s CWU/t")
    public static final String WIRELESS_CWU_MONITOR_TOOLTIP_NOW = WIRELESS_CWU_MONITOR_PREFIX + ".tooltip.now";

    public static int p;
    public static BlockPos pPos;

    public WirelessCWUMonitor(BlockEntityCreationInfo holder) {
        super(holder);
        this.statistics = Statistics.Companion.getDefaultValue();
        this.format = Format.Companion.getDefaultValue();
        this.CWUStatus = Status.Companion.getDefaultValue();
    }

    @Getter
    @Setter
    private WirelessCWUContainer WirelessCWUContainerCache;

    private List<Component> textListCache;

    @SaveField
    private Statistics statistics;
    @SaveField
    private Format format;
    @SaveField
    private Status CWUStatus;

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
        } else if (componentData.equals("CWUStatus")) {
            if (!clickData.isRemote) {
                if (CWUStatus == Status.All) {
                    CWUStatus = Status.In;
                } else if (CWUStatus == Status.In) {
                    CWUStatus = Status.Out;
                } else {
                    CWUStatus = Status.All;
                }
            }
        } else if (clickData.isRemote) {
            p = 100;
            String[] parts = componentData.split(", ");
            pPos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
    }

    public static int DISPLAY_TEXT_WIDTH = 220;

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, DISPLAY_TEXT_WIDTH + 8 + 8, 117 + 8);

        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, DISPLAY_TEXT_WIDTH + 8, 117).setBackground(GuiTextures.DISPLAY)
                .addWidget(new AlignLabelWidget(DISPLAY_TEXT_WIDTH / 2 + 4, 5, self().getBlockState().getBlock().getDescriptionId()).setTextAlign(AlignLabelWidget.ALIGN_CENTER))
                .addWidget(new AlignComponentPanelWidget(4, 17, this::addDisplayText)
                        .setMaxWidthLimit(DISPLAY_TEXT_WIDTH)
                        .clickHandler(this::handleDisplayClick)
                        .setSplitChar(".")));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    public void addDisplayText(List<Component> textList) {
        if (isRemote()) return;
        if (textListCache == null || getOffsetTimer() % 10 == 0) {
            textListCache = getDisplayText(statistics, format, CWUStatus);
        }
        textList.addAll(textListCache);
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getOwnerUUID();
    }

    @Override
    public boolean display() {
        return false;
    }

    @Override
    public Level getMonitorLevel() {
        return getLevel();
    }
}
