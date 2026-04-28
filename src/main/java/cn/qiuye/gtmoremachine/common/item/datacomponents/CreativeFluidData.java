package cn.qiuye.gtmoremachine.common.item.datacomponents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CreativeFluidData(boolean accurate, int capacity) {

    public static final CreativeFluidData DEFAULT = new CreativeFluidData(false, 1000);

    public static final Codec<CreativeFluidData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("accurate", DEFAULT.accurate).forGetter(CreativeFluidData::accurate),
            Codec.INT.optionalFieldOf("capacity", DEFAULT.capacity).forGetter(CreativeFluidData::capacity)).apply(instance, CreativeFluidData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreativeFluidData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public CreativeFluidData withAccurate(boolean accurate) {
        return new CreativeFluidData(accurate, capacity);
    }

    public CreativeFluidData withCapacity(int capacity) {
        return new CreativeFluidData(accurate, capacity);
    }
}
