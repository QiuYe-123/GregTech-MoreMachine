package cn.qiuye.gtmoremachine.integration.jade.provider;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.common.data.machines.WirelessMachines;
import cn.qiuye.gtmoremachine.utils.BigNumberUtils;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;

import net.minecraft.ChatFormatting;
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

import java.math.BigInteger;
import java.util.UUID;

@GTMMDataGeneratorScanned
public class WirelessCWUProvider extends CapabilityBlockProvider<IWirelessCWUContainerHolder> {

    private static final String JADE_PREFIX = "config.jade.plugin_gtmoremachine";
    @GTMMRegisterLanguage(en = "[GTMoreMachine] Wireless CWU Monitor", cn = "[GTMoreMachine] 无线CWU监视器")
    public static final String WIRELESS_CWU_PROVIDER = JADE_PREFIX + ".wireless_cwu_provider";
    @GTMMRegisterLanguage(en = "Total CWU: %s CWU", cn = "CWU总量：%s CWU")
    public static final String WIRELESS_CWU_HATCH_PROVIDER_TOOLTIP_1 = JADE_PREFIX + ".wireless_cwu_hatch_provider.tooltip.1";

    public WirelessCWUProvider() {
        super(GTmm.id("wireless_cwu_provider"));
    }

    @Override
    protected @Nullable IWirelessCWUContainerHolder getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        var metamachine = MetaMachine.getMachine(level, pos);
        if (metamachine instanceof IWirelessCWUContainerHolder bindable && bindable.display()) {
            return bindable;
        }
        return null;
    }

    @Override
    protected void write(CompoundTag data, IWirelessCWUContainerHolder capability) {
        if (capability.getUUID() != null) {
            data.putBoolean("isCWUBindable", true);
            data.putUUID("UUID", capability.getUUID());
            data.putString("cwu", BigNumberUtils.getStringValue(capability.getWirelessCWUContainer().getStorage()));
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block, BlockEntity blockEntity, IPluginConfig config) {
        if (!capData.getBoolean("isCWUBindable")) return;
        if (!capData.hasUUID("UUID")) {
            tooltip.add(Component.translatable(WirelessMachines.WIRELESS_ENERGY_HATCH_TOOLTIP_1));
        } else {
            BigInteger cwu = BigNumberUtils.getBigIntegerValue(capData.getString("cwu"));
            UUID uuid = capData.getUUID("UUID");
            var formattedCwu = Component.literal(NumberUtils.formatBigIntegerNumberOrSic(cwu)).withStyle(ChatFormatting.GOLD);
            if (TeamUtils.hasOwner(block.getLevel(), uuid)) {
                tooltip.add(Component.translatable(WirelessMachines.WIRELESS_ENERGY_HATCH_TOOLTIP_2, TeamUtils.getName(block.getLevel(), uuid)));
                tooltip.add(Component.translatable(WirelessMachines.WIRELESS_CWU_MONITOR_TOOLTIP_1, formattedCwu));
            } else {
                tooltip.add(Component.translatable(WirelessMachines.WIRELESS_ENERGY_HATCH_TOOLTIP_3, uuid));
                tooltip.add(Component.translatable(WirelessMachines.WIRELESS_CWU_MONITOR_TOOLTIP_1, formattedCwu));
            }
        }
    }
}
