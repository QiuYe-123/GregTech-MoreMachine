package cn.qiuye.gtmoremachine.common.machine.electric;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessEnergyHatchPartMachine;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.utils.ExtendedUseOnContext;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@MethodsReturnNonnullByDefault
@GTMMDataGeneratorScanned
public class WirelessEnergyInterface extends TieredIOPartMachine implements IWirelessEnergyContainerHolder {

    private static final String WIRELESS_ENERGY_INTERFACE_PREFIX = "gtmoremachine.machine.wireless_energy_interface";
    @GTMMRegisterLanguage(en = "Receives energy and sends it to the power network", cn = "接收能量并发送至电网")
    public static final String WIRELESS_ENERGY_INTERFACE_TOOLTIP = WIRELESS_ENERGY_INTERFACE_PREFIX + ".tooltip";

    private TickableSubscription updEnergySubs;

    @Getter
    @Setter
    @Nullable
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    @SaveField
    public final NotifiableEnergyContainer energyContainer;

    public WirelessEnergyInterface(BlockEntityCreationInfo holder) {
        super(holder, GTValues.MAX, IO.IN);
        this.energyContainer = NotifiableEnergyContainer.receiverContainer(this, Long.MAX_VALUE,
                GTValues.VEX[tier], 67108864);
        this.energyContainer.setSideInputCondition(s -> s == getFrontFacing() && isWorkingEnabled());
        this.energyContainer.setCapabilityValidator(s -> s == null || s == getFrontFacing());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateEnergySubscription();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (updEnergySubs != null) {
            updEnergySubs.unsubscribe();
            updEnergySubs = null;
        }
    }

    private void updateEnergySubscription() {
        if (this.getUUID() != null) {
            updEnergySubs = subscribeServerTick(updEnergySubs, this::updateEnergy);
        } else if (updEnergySubs != null) {
            updEnergySubs.unsubscribe();
            updEnergySubs = null;
        }
    }

    private void updateEnergy() {
        var currentStored = energyContainer.getEnergyStored();
        if (currentStored <= 0) return;
        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container == null) return;
        long changeEnergy = container.addTierEnergy(currentStored, this);
        if (changeEnergy > 0) energyContainer.setEnergyStored(currentStored - changeEnergy);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }

    @Override
    public InteractionResult onUseWithItem(ExtendedUseOnContext context) {
        if (isRemote()) return InteractionResult.PASS;
        ItemStack item = context.getItemInHand();
        if (item.isEmpty()) return InteractionResult.PASS;
        if (item.is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(context.getPlayer().getUUID());
            setWirelessEnergyContainerCache(null);
            if (isRemote()) {
                context.getPlayer().sendSystemMessage(Component.translatable(WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_BIND, TeamUtils.getName(context.getPlayer())));
            }
            updateEnergySubscription();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, InteractionHand hand, @Nullable Direction face) {
        if (player.getItemInHand(hand).is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(null);
            setWirelessEnergyContainerCache(null);
            if (isRemote()) {
                player.sendSystemMessage(Component.translatable(WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_UNBIND));
            }
            updateEnergySubscription();
            return true;
        }
        return false;
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getOwnerUUID();
    }

    //////////////////////////////////////
    // ********** Misc **********//
    //////////////////////////////////////

    @Override
    public int tintColor(int index) {
        if (index == 2) {
            return GTValues.VC[getTier()];
        }
        return super.tintColor(index);
    }
}
