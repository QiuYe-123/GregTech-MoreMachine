package cn.qiuye.gtmoremachine.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.IECUBlock;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.WirelessEnergyHatchPartMachine;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.utils.ExtendedUseOnContext;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@MethodsReturnNonnullByDefault
public class DimensionalRelayNodeMachine extends WorkableMultiblockMachine
                                         implements IFancyUIMachine, IDisplayUIMachine, IWirelessEnergyContainerHolder {

    private final ConditionalSubscriptionHandler tickSubscription;

    @Getter
    @Setter
    @Nullable
    private WirelessEnergyContainer wirelessEnergyContainerCache;

    @SaveField
    private int currentTier = -1;

    public DimensionalRelayNodeMachine(BlockEntityCreationInfo holder) {
        super(holder);
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::updateMachineStatus, this::isFormed);
    }

    // ============== IWirelessEnergyContainerHolder ==============

    @Override
    public @Nullable UUID getUUID() {
        return getOwnerUUID();
    }

    @Override
    public boolean Dimensional() {
        return true;
    }

    // ============== Player Interaction ==============

    @Override
    public InteractionResult onUseWithItem(ExtendedUseOnContext context) {
        if (isRemote()) return InteractionResult.PASS;
        ItemStack item = context.getItemInHand();
        if (item.isEmpty()) return InteractionResult.PASS;
        if (item.is(GTItems.TOOL_DATA_STICK.asItem())) {
            setOwnerUUID(context.getPlayer().getUUID());
            setWirelessEnergyContainerCache(null);

            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                container.setDimensional(this.currentTier, true, this);
            }

            context.getPlayer().sendSystemMessage(Component.translatable(
                    WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_BIND,
                    TeamUtils.getName(context.getPlayer())));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, InteractionHand hand, @Nullable Direction face) {
        if (isRemote()) return false;
        ItemStack item = player.getItemInHand(hand);
        if (item.isEmpty()) return false;

        if (item.is(GTItems.TOOL_DATA_STICK.get())) {
            setOwnerUUID(null);
            setWirelessEnergyContainerCache(null);

            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                container.setDimensional(0, false, this);
            }

            player.sendSystemMessage(Component.translatable(
                    WirelessEnergyHatchPartMachine.WIRELESS_ENERGY_HATCH_TOOLTIP_UNBIND));
            return true;
        }
        return false;
    }

    // ============== Multiblock Lifecycle ==============

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        var ecuType = getMultiblockState().getMatchContext().get("ECUType");
        this.currentTier = ecuType instanceof IECUBlock ecuBlock ? ecuBlock.getTier() : -1;

        tickSubscription.updateSubscription();
    }

    @Override
    public void onStructureInvalid() {
        tickSubscription.unsubscribe();

        this.currentTier = -1;
        super.onStructureInvalid();
    }

    // ============== Status Update ==============

    protected void updateMachineStatus() {
        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container != null) {
            container.setDimensional(this.currentTier, this.isFormed(), this);
        }
        if (!getLevel().isClientSide) {
            if (getOffsetTimer() % 20 == 0) {
                getRecipeLogic().setStatus(RecipeLogic.Status.WORKING);
            }
        }
    }

    // ============== UI ==============

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117)
                .setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .setMaxWidthLimit(150)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return IFancyUIMachine.super.createUI(entityPlayer);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        if (this.isFormed()) {
            // 预留，可添加显示信息
        } else {
            Component tooltip = Component.translatable("gtceu.multiblock.invalid_structure.tooltip")
                    .withStyle(ChatFormatting.GRAY);
            textList.add(Component.translatable("gtceu.multiblock.invalid_structure")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip))));
        }
        getDefinition().getAdditionalDisplay().accept(this, textList);
        IDisplayUIMachine.super.addDisplayText(textList);
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean ignored) {
        // 固定为工作状态
    }
}
