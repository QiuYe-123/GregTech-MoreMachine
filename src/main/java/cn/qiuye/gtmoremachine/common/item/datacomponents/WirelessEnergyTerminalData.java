package cn.qiuye.gtmoremachine.common.item.datacomponents;

import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.api.gui.monitor.Sorting;
import cn.qiuye.gtmoremachine.api.gui.monitor.Statistics;
import cn.qiuye.gtmoremachine.api.gui.monitor.Status;
import cn.qiuye.gtmoremachine.api.gui.monitor.Type;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WirelessEnergyTerminalData(
                                         Statistics statistics,
                                         Format format,
                                         Status powerStatus,
                                         Sorting sortingRules,
                                         Type type) {

    public static final WirelessEnergyTerminalData DEFAULT = new WirelessEnergyTerminalData(
            Statistics.Companion.getDefaultValue(),
            Format.Companion.getDefaultValue(),
            Status.Companion.getDefaultValue(),
            Sorting.Companion.getDefaultValue(),
            Type.Companion.getDefaultValue());

    public static final Codec<WirelessEnergyTerminalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            enumCodec(Statistics.class).optionalFieldOf("statistics", DEFAULT.statistics).forGetter(WirelessEnergyTerminalData::statistics),
            enumCodec(Format.class).optionalFieldOf("format", DEFAULT.format).forGetter(WirelessEnergyTerminalData::format),
            enumCodec(Status.class).optionalFieldOf("power_status", DEFAULT.powerStatus).forGetter(WirelessEnergyTerminalData::powerStatus),
            enumCodec(Sorting.class).optionalFieldOf("sorting_rules", DEFAULT.sortingRules).forGetter(WirelessEnergyTerminalData::sortingRules),
            enumCodec(Type.class).optionalFieldOf("type", DEFAULT.type).forGetter(WirelessEnergyTerminalData::type)).apply(instance, WirelessEnergyTerminalData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WirelessEnergyTerminalData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public WirelessEnergyTerminalData withStatistics(Statistics statistics) {
        return new WirelessEnergyTerminalData(statistics, format, powerStatus, sortingRules, type);
    }

    public WirelessEnergyTerminalData withFormat(Format format) {
        return new WirelessEnergyTerminalData(statistics, format, powerStatus, sortingRules, type);
    }

    public WirelessEnergyTerminalData withPowerStatus(Status powerStatus) {
        return new WirelessEnergyTerminalData(statistics, format, powerStatus, sortingRules, type);
    }

    public WirelessEnergyTerminalData withSortingRules(Sorting sortingRules) {
        return new WirelessEnergyTerminalData(statistics, format, powerStatus, sortingRules, type);
    }

    public WirelessEnergyTerminalData withType(Type type) {
        return new WirelessEnergyTerminalData(statistics, format, powerStatus, sortingRules, type);
    }

    private static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return Codec.STRING.comapFlatMap(name -> {
            try {
                return DataResult.success(Enum.valueOf(enumClass, name));
            } catch (IllegalArgumentException ignored) {
                return DataResult.error(() -> "Unknown enum value '" + name + "' for " + enumClass.getSimpleName());
            }
        }, Enum::name);
    }
}
