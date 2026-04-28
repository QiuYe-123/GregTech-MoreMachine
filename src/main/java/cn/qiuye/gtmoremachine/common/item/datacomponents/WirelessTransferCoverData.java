package cn.qiuye.gtmoremachine.common.item.datacomponents;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WirelessTransferCoverData(
                                        String dimensionId,
                                        String blockId,
                                        int x,
                                        int y,
                                        int z,
                                        String facing) {

    public static final WirelessTransferCoverData EMPTY = new WirelessTransferCoverData("", "", 0, 0, 0, "");

    public static final Codec<WirelessTransferCoverData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("dimension_id", EMPTY.dimensionId).forGetter(WirelessTransferCoverData::dimensionId),
            Codec.STRING.optionalFieldOf("block_id", EMPTY.blockId).forGetter(WirelessTransferCoverData::blockId),
            Codec.INT.optionalFieldOf("x", EMPTY.x).forGetter(WirelessTransferCoverData::x),
            Codec.INT.optionalFieldOf("y", EMPTY.y).forGetter(WirelessTransferCoverData::y),
            Codec.INT.optionalFieldOf("z", EMPTY.z).forGetter(WirelessTransferCoverData::z),
            Codec.STRING.optionalFieldOf("facing", EMPTY.facing).forGetter(WirelessTransferCoverData::facing)).apply(instance, WirelessTransferCoverData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WirelessTransferCoverData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    public static WirelessTransferCoverData of(String dimensionId, String blockId, BlockPos blockPos, String facing) {
        return new WirelessTransferCoverData(dimensionId, blockId, blockPos.getX(), blockPos.getY(), blockPos.getZ(), facing);
    }

    public boolean isBound() {
        return !dimensionId.isEmpty() && !blockId.isEmpty() && !facing.isEmpty();
    }

    public BlockPos blockPos() {
        return new BlockPos(x, y, z);
    }

    public String shortPos() {
        return blockPos().toShortString();
    }
}
