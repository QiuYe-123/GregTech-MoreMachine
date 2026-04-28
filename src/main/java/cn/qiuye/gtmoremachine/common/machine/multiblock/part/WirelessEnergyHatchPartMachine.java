package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@MethodsReturnNonnullByDefault
@GTMMDataGeneratorScanned
public class WirelessEnergyHatchPartMachine extends TieredIOPartMachine implements IWirelessEnergyContainerHolder {

    private static final String WIRELESS_ENERGY_HATCH_PREFIX = "gtmoremachine.machine.wireless_energy_hatch";
    @GTMMRegisterLanguage(en = "Bind to: %s", cn = "成功绑定至：%s")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_BIND = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.bind";
    @GTMMRegisterLanguage(en = "Unbind!", cn = "解除绑定成功")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_UNBIND = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.unbind";
    @GTMMRegisterLanguage(en = "No owner.", cn = "未绑定所有者")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_1 = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Bind to: %s", cn = "已绑定至：%s")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_2 = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Bind to unknow user: %s", cn = "已绑定至未知用户：%s")
    public static final String WIRELESS_ENERGY_HATCH_TOOLTIP_3 = WIRELESS_ENERGY_HATCH_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "Multiblock Sharing §4Disabled", cn = "多方块结构共享：§4禁止")
    public static final String UNIVERSAL_DISABLED = "gtmoremachine.universal.disabled";

    private static final String ENERGY_HATCH_PREFIX = "gtmoremachine.machine.energy_hatch";
    @GTMMRegisterLanguage(en = "Energy Input for Multiblocks", cn = "为多方块结构输入能量")
    public static final String ENERGY_HATCH_INPUT_TOOLTIP = ENERGY_HATCH_PREFIX + ".input.tooltip";
    @GTMMRegisterLanguage(en = "Energy Output for Multiblocks", cn = "为多方块结构输出能量")
    public static final String ENERGY_HATCH_OUTPUT_TOOLTIP = ENERGY_HATCH_PREFIX + ".output.tooltip";
    @GTMMRegisterLanguage(en = "Large Amount Of Energy Input for Multiblocks", cn = "为多方块结构输入大量能量")
    public static final String ENERGY_HATCH_TARGET_TOOLTIP = ENERGY_HATCH_PREFIX + ".target.tooltip";
    @GTMMRegisterLanguage(en = "Large Amount Of Energy Output for Multiblocks", cn = "为多方块结构输出大量能量")
    public static final String ENERGY_HATCH_SOURCE_TOOLTIP = ENERGY_HATCH_PREFIX + ".source.tooltip";
    @GTMMRegisterLanguage(en = "You can bind or change the owner by left-click the Energy Hatch with Data Stick,or right-click to unbind.", cn = "手持闪存右键点击能源仓可绑定·变更所有者，左键点击可解除绑定。")
    public static final String WIRELESS_ENERGY_HATCH_INPUT_TOOLTIP = WIRELESS_ENERGY_HATCH_PREFIX + ".input.tooltip";
    @GTMMRegisterLanguage(en = "You can bind or change the owner by left-click the Dynoma Hatch with Data Stick,or right-click to unbind.", cn = "手持闪存右键点击动力仓可绑定·变更所有者，左键点击可解除绑定。")
    public static final String WIRELESS_ENERGY_HATCH_OUTPUT_TOOLTIP = WIRELESS_ENERGY_HATCH_PREFIX + ".output.tooltip";
    @GTMMRegisterLanguage(en = "You can bind or change the owner by left-click the Laser Target Hatch with Data Stick,or right-click to unbind.", cn = "手持闪存右键点击激光靶仓可绑定·变更所有者，左键点击可解除绑定。")
    public static final String WIRELESS_ENERGY_HATCH_TARGET_TOOLTIP = WIRELESS_ENERGY_HATCH_PREFIX + ".target.tooltip";
    @GTMMRegisterLanguage(en = "You can bind or change the owner by left-click the Laser Source Hatch with Data Stick,or right-click to unbind.", cn = "手持闪存右键点击激光源仓可绑定·变更所有者，左键点击可解除绑定。")
    public static final String WIRELESS_ENERGY_HATCH_SOURCE_TOOLTIP = WIRELESS_ENERGY_HATCH_PREFIX + ".source.tooltip";

    @Nullable
    @Getter
    @Setter
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    @SaveField
    public final NotifiableEnergyContainer energyContainer;
    @Getter
    protected int amperage;
    private final boolean leaser;
    private TickableSubscription updEnergySubs;

    public WirelessEnergyHatchPartMachine(BlockEntityCreationInfo holder, int tier, IO io, int amperage, boolean isleaser) {
        super(holder, tier, io);
        this.amperage = amperage;
        this.leaser = isleaser;
        this.energyContainer = createEnergyContainer();
    }

    protected NotifiableEnergyContainer createEnergyContainer() {
        long multiplier = this.leaser ? 64L : 16L;
        long capacity = GTValues.VEX[this.tier] * multiplier * this.amperage;
        return this.io == IO.IN ?
                NotifiableEnergyContainer.receiverContainer(this, capacity, GTValues.VEX[this.tier], this.amperage) :
                NotifiableEnergyContainer.emitterContainer(this, capacity, GTValues.VEX[this.tier], this.amperage);
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
        if (this.getUUID() == null) return;
        if (io == IO.IN) {
            useEnergy();
        } else {
            addEnergy();
        }
    }

    private void useEnergy() {
        var currentStored = energyContainer.getEnergyStored();
        var maxStored = energyContainer.getEnergyCapacity();
        var changeStored = Math.min(maxStored - currentStored, energyContainer.getInputVoltage() * energyContainer.getInputAmperage());
        if (changeStored <= 0) return;
        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container == null) return;
        changeStored = container.removeTierEnergy(changeStored, this);
        if (changeStored > 0) energyContainer.setEnergyStored(currentStored + changeStored);
    }

    private void addEnergy() {
        var currentStored = energyContainer.getEnergyStored();
        if (currentStored <= 0) return;
        var changeStored = Math.min(energyContainer.getOutputVoltage() * energyContainer.getOutputAmperage(), currentStored);
        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container == null) return;
        changeStored = container.addTierEnergy(changeStored, this);
        if (changeStored > 0) energyContainer.setEnergyStored(currentStored - changeStored);
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
            context.getPlayer().sendSystemMessage(Component.translatable(WIRELESS_ENERGY_HATCH_TOOLTIP_BIND, TeamUtils.getName(context.getPlayer())));
            updateEnergySubscription();
            return InteractionResult.SUCCESS;
        } else if (GTmm.isDev() && item.is(Items.STICK)) {
            if (io == IO.OUT) {
                energyContainer.setEnergyStored(GTValues.VEX[tier] * 64L * amperage);
            } else if (io == IO.IN) {
                energyContainer.setEnergyStored(-(GTValues.VEX[tier] * 64L * amperage));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, InteractionHand hand, @Nullable Direction face) {
        if (isRemote()) return false;
        ItemStack item = player.getItemInHand(hand);
        if (item.isEmpty()) return false;
        if (item.is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(null);
            setWirelessEnergyContainerCache(null);
            player.sendSystemMessage(Component.translatable(WIRELESS_ENERGY_HATCH_TOOLTIP_UNBIND));
            updateEnergySubscription();
            return true;
        }
        return false;
    }

    @Override
    public @Nullable UUID getUUID() {
        return getOwnerUUID();
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
