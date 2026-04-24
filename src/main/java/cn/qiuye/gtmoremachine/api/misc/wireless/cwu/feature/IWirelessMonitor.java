package cn.qiuye.gtmoremachine.api.misc.wireless.cwu.feature;

import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.api.gui.monitor.Statistics;
import cn.qiuye.gtmoremachine.api.gui.monitor.Status;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.time.TimeStat;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;
import cn.qiuye.gtmoremachine.utils.FormattingUtil;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public interface IWirelessMonitor extends IWirelessCWUContainerHolder {

    default List<Component> getDisplayText(Statistics statistics, Format format, Status CWUStatus) {
        List<Component> textListCache = new ArrayList<>();
        WirelessCWUContainer container = getWirelessCWUContainer();
        if (container == null) return List.of();
        BigInteger CWUToTal = container.getStorage();
        textListCache.add(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.0",
                TeamUtils.getName(getMonitorLevel(), getUUID())).withStyle(ChatFormatting.AQUA));
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_cwu_monitor.tooltip.1",
                Component.literal(NumberUtils.formatBigIntegerNumberOrSic(CWUToTal, format)).withStyle(ChatFormatting.GOLD)));

        TimeStat stat = container.getCWUStat();
        textListCache.add(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.net_cwu",
                getCWUStatusText(CWUStatus)));

        BigDecimal avgMinute = stat.getMinuteAvg(CWUStatus);
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_minute",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgMinute, format)).withStyle(ChatFormatting.DARK_AQUA)));
        BigDecimal avgHour = stat.getHourAvg(CWUStatus);
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_hour",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgHour, format)).withStyle(ChatFormatting.YELLOW)));
        BigDecimal avgDay = stat.getDayAvg(CWUStatus);
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_day",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgDay, format)).withStyle(ChatFormatting.DARK_GREEN)));
        // average useage
        BigDecimal avgEnergy = stat.getAvg(CWUStatus);
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_cwu_monitor.tooltip.now",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgEnergy, format)).withStyle(ChatFormatting.DARK_PURPLE)));

        textListCache.add(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.statistics.cwu",
                ComponentPanelWidget.withButton(getStatisticsText(statistics), "statistics", getStaticsclolor(statistics)),
                ComponentPanelWidget.withButton(getFormatText(format), "format", getFormatclolor(format)),
                ComponentPanelWidget.withButton(getCWUStatusText(CWUStatus), "CWUStatus", getCWUStatusclolor(CWUStatus))));

        List<Map.Entry<MetaMachine, ITransferData>> entryList = new ArrayList<>(WirelessCWUContainer.TRANSFER_DATA.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().Throughput())).toList());

        for (Map.Entry<MetaMachine, ITransferData> m : entryList) {
            UUID uuid = m.getValue().UUID();
            int through = m.getValue().Throughput();
            if (statistics == Statistics.Global || uuid.equals(TeamUtils.getTeamUUID(this.getUUID()))) {
                if (CWUStatus == Status.All) {
                    textListCache.add(m.getValue().getInfo(format));
                } else if (CWUStatus == Status.In && through > 0) {
                    textListCache.add(m.getValue().getInfo(format));
                } else if (CWUStatus == Status.Out && through < 0) {
                    textListCache.add(m.getValue().getInfo(format));
                }
            }
        }

        WirelessCWUContainer.observed = true;
        WirelessCWUContainer.TRANSFER_DATA.clear();

        return textListCache;
    }

    Level getMonitorLevel();

    private static Component getStatisticsText(Statistics statistics) {
        return switch (statistics) {
            case Global -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.all");
            case Team -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.team");
        };
    }

    private static int getStaticsclolor(Statistics statistics) {
        return switch (statistics) {
            case Global -> ChatFormatting.YELLOW.getColor();
            case Team -> ChatFormatting.GOLD.getColor();
        };
    }

    private static Component getFormatText(Format format) {
        return switch (format) {
            case Science -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.science");
            case Unit -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.unit");
        };
    }

    private static int getFormatclolor(Format format) {
        return switch (format) {
            case Science -> ChatFormatting.AQUA.getColor();
            case Unit -> ChatFormatting.RED.getColor();
        };
    }

    private static Component getCWUStatusText(Status CWUStatus) {
        return switch (CWUStatus) {
            case All -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_all");
            case In -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_in");
            case Out -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_out");
        };
    }

    private static int getCWUStatusclolor(Status CWUStatus) {
        return switch (CWUStatus) {
            case All -> ChatFormatting.GREEN.getColor();
            case In -> ChatFormatting.BLUE.getColor();
            case Out -> ChatFormatting.DARK_RED.getColor();
        };
    }
}
