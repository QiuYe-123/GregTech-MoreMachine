package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.api.gui.monitor.Statistics;
import cn.qiuye.gtmoremachine.api.gui.monitor.Status;
import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.config.GTMMConfig;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;
import cn.qiuye.gtmoremachine.utils.FormattingUtil;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.*;

public interface IWirelessMonitor extends IWirelessEnergyContainerHolder {

    default List<Component> getDisplayText(Statistics statistics, Format format, Status powerStatus) {
        List<Component> textListCache = new ArrayList<>();
        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container == null) return List.of();
        BigInteger energyTotal = container.getStorage();
        textListCache.add(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.0",
                TeamUtils.getName(getLevel(), getUUID())).withStyle(ChatFormatting.AQUA));
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_energy_monitor.tooltip.1",
                Component.literal(NumberUtils.formatBigIntegerNumberOrSic(energyTotal, format)).withStyle(ChatFormatting.GOLD),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(new BigDecimal(energyTotal)), format)),
                FormattingUtil.voltageName(new BigDecimal(energyTotal))));
        if (GTMMConfig.INSTANCE.isWirelessRateEnable) {
            long rate = container.getRate();
            textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_energy_monitor.tooltip.2",
                    Component.literal(NumberUtils.formatBigIntegerNumberOrSic(BigInteger.valueOf(rate), format)),
                    Component.literal(String.valueOf(rate / GTValues.VEX[GTUtil.getFloorTierByVoltage(rate)])),
                    Component.literal(GTValues.VNF[GTUtil.getFloorTierByVoltage(rate)])).withStyle(ChatFormatting.GRAY));
        }

        var allstat = container.getAllEnergyStat();
        var instat = container.getInEnergyStat();
        var outstat = container.getOutEnergyStat();
        var stat = switch (powerStatus) {
            case All -> allstat;
            case In -> instat;
            case Out -> outstat;
        };
        textListCache.add(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.net_power",
                getPowerStatusText(powerStatus)));

        BigDecimal avgMinute = stat.getMinuteAvg();
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_energy_monitor.tooltip.last_minute",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgMinute, format)).withStyle(ChatFormatting.DARK_AQUA),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(avgMinute), format)),
                FormattingUtil.voltageName(avgMinute)));
        BigDecimal avgHour = stat.getHourAvg();
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_energy_monitor.tooltip.last_hour",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgHour, format)).withStyle(ChatFormatting.YELLOW),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(avgHour), format)),
                FormattingUtil.voltageName(avgHour)));
        BigDecimal avgDay = stat.getDayAvg();
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_energy_monitor.tooltip.last_day",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgDay, format)).withStyle(ChatFormatting.DARK_GREEN),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(avgDay), format)),
                FormattingUtil.voltageName(avgDay)));
        // average useage
        BigDecimal avgEnergy = stat.getAvg();
        textListCache.add(FormattingUtil.formatWithConstantWidth("gtmoremachine.machine.wireless_energy_monitor.tooltip.now",
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(avgEnergy, format)).withStyle(ChatFormatting.DARK_PURPLE),
                Component.literal(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(avgEnergy), format)),
                FormattingUtil.voltageName(avgEnergy)));

        int compare = avgEnergy.compareTo(BigDecimal.valueOf(0));
        BigInteger multiply = avgEnergy.abs().toBigInteger().multiply(BigInteger.valueOf(20));
        if (compare > 0) {
            textListCache.add(Component.translatable("gtceu.multiblock.power_substation.time_to_fill",
                    container.getCapacity() == null ? Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.time_to_fill") : getTimeToFillDrainText((container.getCapacity().subtract(energyTotal)).divide(multiply))).withStyle(ChatFormatting.GRAY));
        } else if (compare < 0) {
            textListCache.add(Component.translatable("gtceu.multiblock.power_substation.time_to_drain",
                    getTimeToFillDrainText(energyTotal.divide(multiply))).withStyle(ChatFormatting.GRAY));
        }

        if (GTMMConfig.INSTANCE.isWirelessRateEnable && container.getBindPos() != null) {
            String pos = container.getBindPos().pos().toShortString();
            textListCache.add(Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.2",
                    Component.translatable("recipe.condition.dimension.tooltip", container.getBindPos().dimension().location().toString()).append(" [").append(pos).append("] ")).withStyle(ChatFormatting.GRAY));
        }
        textListCache.add(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.statistics.energy",
                ComponentPanelWidget.withButton(getStatisticsText(statistics), "statistics", getStaticsclolor(statistics)),
                ComponentPanelWidget.withButton(getFormatText(format), "format", getFormatclolor(format)),
                ComponentPanelWidget.withButton(getPowerStatusText(powerStatus), "powerStatus", getPowerStatusclolor(powerStatus))));

        List<Map.Entry<MetaMachine, ITransferData>> entryList = new ArrayList<>(WirelessEnergyContainer.TRANSFER_DATA.entrySet());
        entryList.sort((entry1, entry2) -> {
            BigInteger throughput1 = entry1.getValue().Throughput();
            BigInteger throughput2 = entry2.getValue().Throughput();
            return throughput1.compareTo(throughput2);
        });

        for (Map.Entry<MetaMachine, ITransferData> m : entryList) {
            UUID uuid = m.getValue().UUID();
            BigInteger through = m.getValue().Throughput();
            if (statistics == Statistics.Global || uuid.equals(TeamUtils.getTeamUUID(this.getUUID()))) {
                if (powerStatus == Status.All) {
                    textListCache.add(m.getValue().getInfo(format));
                } else if (powerStatus == Status.In && through.compareTo(BigInteger.ZERO) > 0) {
                    textListCache.add(m.getValue().getInfo(format));
                } else if (powerStatus == Status.Out && through.compareTo(BigInteger.ZERO) < 0) {
                    textListCache.add(m.getValue().getInfo(format));
                }
            }
        }

        WirelessEnergyContainer.observed = true;
        WirelessEnergyContainer.TRANSFER_DATA.clear();

        return textListCache;
    }

    Level getLevel();

    private static Component getTimeToFillDrainText(BigInteger timeToFillSeconds) {
        if (timeToFillSeconds.compareTo(BigIntegerUtils.big_integer_max_kong) > 0) {
            timeToFillSeconds = BigIntegerUtils.big_integer_max_kong;
        }

        Duration duration = Duration.ofSeconds(timeToFillSeconds.longValue());
        String key;
        long fillTime;
        if (duration.getSeconds() <= 180) {
            fillTime = duration.getSeconds();
            key = "gtceu.multiblock.power_substation.time_seconds";
        } else if (duration.toMinutes() <= 180) {
            fillTime = duration.toMinutes();
            key = "gtceu.multiblock.power_substation.time_minutes";
        } else if (duration.toHours() <= 72) {
            fillTime = duration.toHours();
            key = "gtceu.multiblock.power_substation.time_hours";
        } else if (duration.toDays() <= 730) { // 2 years
            fillTime = duration.toDays();
            key = "gtceu.multiblock.power_substation.time_days";
        } else if (duration.toDays() / 365 < 1_000_000) {
            fillTime = duration.toDays() / 365;
            key = "gtceu.multiblock.power_substation.time_years";
        } else {
            return Component.translatable("gtceu.multiblock.power_substation.time_forever");
        }

        return Component.translatable(key, NumberUtils.formatLong(fillTime));
    }

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

    private static Component getPowerStatusText(Status powerStatus) {
        return switch (powerStatus) {
            case All -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_all");
            case In -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_in");
            case Out -> Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_out");
        };
    }

    private static int getPowerStatusclolor(Status powerStatus) {
        return switch (powerStatus) {
            case All -> ChatFormatting.GREEN.getColor();
            case In -> ChatFormatting.BLUE.getColor();
            case Out -> ChatFormatting.DARK_RED.getColor();
        };
    }
}
