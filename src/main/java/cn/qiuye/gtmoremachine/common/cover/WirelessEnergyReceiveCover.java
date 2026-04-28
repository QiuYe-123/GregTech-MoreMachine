package cn.qiuye.gtmoremachine.common.cover;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.WirelessEnergyReceiveCoverHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.TieredEnergyMachine;
import com.gregtechceu.gtceu.common.machine.electric.BatteryBufferMachine;
import com.gregtechceu.gtceu.common.machine.electric.HullMachine;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.gregtechceu.gtceu.api.capability.GTCapabilityHelper.getEnergyContainer;

@MethodsReturnNonnullByDefault
@GTMMDataGeneratorScanned
public class WirelessEnergyReceiveCover extends CoverBehavior implements IWirelessEnergyContainerHolder {

    private static final String WIRELESS_COVER_PREFIX = "item.gtmoremachine.wireless_energy_receive_cover";
    @GTMMRegisterLanguage(en = "§bPull Energy§7 from EU network to the machine as §fCover§7.", cn = "§7作§f覆盖板§7时从电网§b拉取能量§7传输到机器。")
    public static final String WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_1 = WIRELESS_COVER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "§7Can only used for §esingle block machine§7.Can't put on the machine blow the cover's voltage", cn = "§7只可用于§e单方块机器§7。无法将超过机器电压等级的覆盖板安装到机器上。")
    public static final String WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_2 = WIRELESS_COVER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "§bEnergy transfer speed: §f%s §7EU/t", cn = "§b能量传输效率：§f%s §7EU/t")
    public static final String WIRELESS_ENERGY_RECEIVE_COVER_TOOLTIP_3 = WIRELESS_COVER_PREFIX + ".tooltip.3";

    private static final String WIRELESS_ENERGY_COVER_PREFIX = "gtmoremachine.machine.wireless_energy_cover";
    @GTMMRegisterLanguage(en = "The Wireless Energy Reciver unbind!", cn = "无线能源接收器未绑定所有者")
    public static final String WIRELESS_ENERGY_COVER_TOOLTIP_1 = WIRELESS_ENERGY_COVER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "The Wireless Energy Reciver bind to: %s", cn = "无线能源接收器已绑定至：%s")
    public static final String WIRELESS_ENERGY_COVER_TOOLTIP_2 = WIRELESS_ENERGY_COVER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "The Wireless Energy Reciver bind to unknow user: %s", cn = "无线能源接收器已绑定至未知用户：%s")
    public static final String WIRELESS_ENERGY_COVER_TOOLTIP_3 = WIRELESS_ENERGY_COVER_PREFIX + ".tooltip.3";

    private TickableSubscription subscription;

    @Getter
    @Setter
    @Nullable
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    private MetaMachine machine;

    private final long energyPerTick;
    private final int tier;
    private final int amperage;
    private long machineMaxEnergy;

    public WirelessEnergyReceiveCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide, int tier, int amperage) {
        super(definition, coverHolder, attachedSide);
        this.tier = tier;
        this.amperage = amperage;
        this.energyPerTick = GTValues.VEX[tier] * amperage;
    }

    @Override
    public boolean canAttach() {
        var machine = getMachine();
        if (machine instanceof TieredEnergyMachine tieredEnergyMachine && tieredEnergyMachine.energyContainer.getHandlerIO() == IO.IN && tieredEnergyMachine.getTier() >= this.tier) {
            var covers = tieredEnergyMachine.getCoverContainer().getCovers();
            for (var cover : covers) {
                if (cover instanceof WirelessEnergyReceiveCover) return false;
            }
            return true;
        } else if (machine instanceof BatteryBufferMachine batteryBufferMachine) {
            return batteryBufferMachine.getTier() >= this.tier;
        } else if (machine instanceof HullMachine hullMachine) {
            return hullMachine.getTier() >= this.tier;
        } else if (machine instanceof WirelessEnergyReceiveCoverHolder holder) {
            return holder.getTier() >= this.tier;
        } else {
            return false;
        }
    }

    @Override
    public void onAttached(ItemStack itemStack, ServerPlayer player) {
        super.onAttached(itemStack, player);
        MetaMachine machine = getMachine();
        if (machine != null && getUUID() == null) {
            machine.setOwnerUUID(player.getUUID());
        }
        updateCoverSub();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateCoverSub();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        machine = null;
        setWirelessEnergyContainerCache(null);
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private void updateCoverSub() {
        subscription = coverHolder.subscribeServerTick(subscription, this::updateEnergy);
    }

    private void updateEnergy() {
        if (getUUID() == null) return;
        var energyContainer = getEnergyContainer(coverHolder.getLevel(), coverHolder.getBlockPos(), attachedSide);
        if (energyContainer != null) {
            var machine = getMachine();
            if (machine instanceof BatteryBufferMachine || machine instanceof HullMachine || machine instanceof WirelessEnergyReceiveCoverHolder) {
                var changeStored = Math.min(energyContainer.getEnergyCapacity() - energyContainer.getEnergyStored(), this.energyPerTick);
                if (changeStored <= 0) return;
                WirelessEnergyContainer container = getWirelessEnergyContainer();
                if (container == null) return;
                long changeenergy = container.removeTierEnergy(changeStored, machine);
                if (changeenergy > 0) energyContainer.acceptEnergyFromNetwork(null, changeenergy / this.amperage, this.amperage);
            } else {
                var changeStored = Math.min(this.machineMaxEnergy - energyContainer.getEnergyStored(), this.energyPerTick);
                if (changeStored <= 0) return;
                WirelessEnergyContainer container = getWirelessEnergyContainer();
                if (container == null) return;
                long changeenergy = container.removeTierEnergy(changeStored, machine);
                if (changeenergy > 0) energyContainer.addEnergy(changeenergy);
            }
        }
    }

    @Override
    @Nullable
    public UUID getUUID() {
        MetaMachine machine = getMachine();
        if (machine != null) return machine.getOwnerUUID();
        return null;
    }

    @Override
    public boolean cover() {
        return true;
    }

    @Nullable
    private MetaMachine getMachine() {
        if (machine == null) machine = MetaMachine.getMachine(coverHolder.getLevel(), coverHolder.getBlockPos());
        if (machine instanceof TieredEnergyMachine tieredEnergyMachine) {
            this.machineMaxEnergy = GTValues.VEX[tieredEnergyMachine.getTier()] << 6;
        }
        return machine;
    }
}
