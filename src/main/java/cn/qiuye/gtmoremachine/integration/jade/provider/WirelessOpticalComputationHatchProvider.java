package cn.qiuye.gtmoremachine.integration.jade.provider;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.capability.IGTMMJadeIF;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@GTMMDataGeneratorScanned
public class WirelessOpticalComputationHatchProvider extends CapabilityBlockProvider<IGTMMJadeIF> {

    private static final String JADE_PREFIX = "config.jade.plugin_gtmoremachine";
    private static final String MACHINE_PREFIX = "gtmoremachine.machine";
    @GTMMRegisterLanguage(en = "Already bound to the receiving Hatch (%s).", cn = "已绑定到接收仓(%s)。")
    public static final String TRANSMITTER_HATCH_BIND = MACHINE_PREFIX + ".transmitter_hatch.bind";
    @GTMMRegisterLanguage(en = "Not bound to the receiving Hatch.", cn = "未绑定到接收仓。")
    public static final String TRANSMITTER_HATCH_UNBIND = MACHINE_PREFIX + ".transmitter_hatch.unbind";
    @GTMMRegisterLanguage(en = "Already bound to the launch Hatch (%s).", cn = "已绑定到发射仓(%s)。")
    public static final String RECEIVER_HATCH_BIND = MACHINE_PREFIX + ".receiver_hatch.bind";
    @GTMMRegisterLanguage(en = "Not bound to the launch Hatch", cn = "未绑定到发射仓。")
    public static final String RECEIVER_HATCH_UNBIND = MACHINE_PREFIX + ".receiver_hatch.unbind";
    @GTMMRegisterLanguage(en = "[GTMoreMachine] Wireless OpticalComputation", cn = "[GTMoreMachine] 无线光学算力仓")
    public static final String WIRELESS_OPTICAL_COMPUTATION_HATCH_PROVIDER = JADE_PREFIX + ".wireless_optical_computation_hatch_provider";

    public WirelessOpticalComputationHatchProvider() {
        super(GTmm.id("wireless_optical_computation_hatch_provider"));
    }

    @Override
    protected @Nullable IGTMMJadeIF getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        if (MetaMachine.getMachine(level, pos) instanceof IGTMMJadeIF jadeIF) {
            return jadeIF;
        }
        return null;
    }

    @Override
    protected void write(CompoundTag data, IGTMMJadeIF capability) {
        if (capability == null) return;
        data.putBoolean("isGTMMJadeIF", true);
        data.putBoolean("isTransmitter", capability.isTransmitter());
        data.putBoolean("isBinded", capability.isbinded());
        data.putString("pos", capability.getBindPos());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.getBoolean("isGTMMJadeIF")) return;
        if (capData.getBoolean("isBinded")) {
            if (capData.getBoolean("isTransmitter")) {
                tooltip.add(Component.translatable(TRANSMITTER_HATCH_BIND, capData.getString("pos")));
            } else {
                tooltip.add(Component.translatable(RECEIVER_HATCH_BIND, capData.getString("pos")));
            }
        } else {
            if (capData.getBoolean("isTransmitter")) {
                tooltip.add(Component.translatable(TRANSMITTER_HATCH_UNBIND));
            } else {
                tooltip.add(Component.translatable(RECEIVER_HATCH_UNBIND));
            }
        }
    }
}
