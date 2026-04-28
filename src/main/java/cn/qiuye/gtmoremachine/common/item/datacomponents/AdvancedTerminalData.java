package cn.qiuye.gtmoremachine.common.item.datacomponents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public record AdvancedTerminalData(
        int repeatCount,
        boolean replaceMode,
        boolean demolitionMode,
        boolean useAEMode,
        boolean flipMode,
        boolean noHatchMode,
        Map<String, Integer> tierBlocks) {

    public static final AdvancedTerminalData DEFAULT = new AdvancedTerminalData(0, false, false, false, false, true, Map.of());

    public static final Codec<AdvancedTerminalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("repeat_count", DEFAULT.repeatCount).forGetter(AdvancedTerminalData::repeatCount),
            Codec.BOOL.optionalFieldOf("replace_mode", DEFAULT.replaceMode).forGetter(AdvancedTerminalData::replaceMode),
            Codec.BOOL.optionalFieldOf("demolition_mode", DEFAULT.demolitionMode).forGetter(AdvancedTerminalData::demolitionMode),
            Codec.BOOL.optionalFieldOf("use_ae_mode", DEFAULT.useAEMode).forGetter(AdvancedTerminalData::useAEMode),
            Codec.BOOL.optionalFieldOf("flip_mode", DEFAULT.flipMode).forGetter(AdvancedTerminalData::flipMode),
            Codec.BOOL.optionalFieldOf("no_hatch_mode", DEFAULT.noHatchMode).forGetter(AdvancedTerminalData::noHatchMode),
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("tier_blocks", DEFAULT.tierBlocks).forGetter(AdvancedTerminalData::tierBlocks)
    ).apply(instance, AdvancedTerminalData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedTerminalData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public AdvancedTerminalData {
        tierBlocks = Map.copyOf(tierBlocks);
    }

    public AdvancedTerminalData withRepeatCount(int repeatCount) {
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, tierBlocks);
    }

    public AdvancedTerminalData withReplaceMode(boolean replaceMode) {
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, tierBlocks);
    }

    public AdvancedTerminalData withDemolitionMode(boolean demolitionMode) {
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, tierBlocks);
    }

    public AdvancedTerminalData withUseAEMode(boolean useAEMode) {
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, tierBlocks);
    }

    public AdvancedTerminalData withFlipMode(boolean flipMode) {
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, tierBlocks);
    }

    public AdvancedTerminalData withNoHatchMode(boolean noHatchMode) {
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, tierBlocks);
    }

    public AdvancedTerminalData toggleTierBlock(String category, int index) {
        Map<String, Integer> updated = new HashMap<>(tierBlocks);
        if (updated.getOrDefault(category, 0) == index) {
            updated.remove(category);
        } else {
            updated.put(category, index);
        }
        return new AdvancedTerminalData(repeatCount, replaceMode, demolitionMode, useAEMode, flipMode, noHatchMode, updated);
    }
}
