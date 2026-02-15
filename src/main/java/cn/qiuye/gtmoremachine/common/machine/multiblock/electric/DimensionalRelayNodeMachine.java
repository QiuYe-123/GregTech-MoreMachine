package cn.qiuye.gtmoremachine.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.machine.multiblock.IEnergyCommunicationUnitBlock;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.data.GTItems;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
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

    // ============== ManagedField ==============

    @Override
    public boolean isRemote() {
        return super.isRemote();
    }

    // ============== Player Interaction ==============

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player,
                                   InteractionHand hand, BlockHitResult hit) {
        if (isRemote()) return InteractionResult.PASS;
        ItemStack item = player.getItemInHand(hand);
        if (item.isEmpty()) return InteractionResult.PASS;

        if (item.is(GTItems.TOOL_DATA_STICK.get())) {
            setOwnerUUID(player.getUUID());
            setWirelessEnergyContainerCache(null);

            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                container.setDimensional(this.currentTier, true, this);
            }

            player.sendSystemMessage(Component.translatable(
                    "gtmoremachine.machine.wireless_energy_hatch.tooltip.bind",
                    TeamUtils.getName(player)));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
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
                    "gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind"));
            return true;
        }
        return false;
    }

    // ============== Multiblock Lifecycle ==============

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        IEnergyCommunicationUnitBlock ecuType = getMultiblockState().getMatchContext().get("ECUType");
        if (ecuType != null) {
            this.currentTier = ecuType.getTier();
        }

        tickSubscription.updateSubscription();

        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container != null) {
            container.setDimensional(this.currentTier, true, this);
        }
    }

    @Override
    public void onStructureInvalid() {
        tickSubscription.updateSubscription();

        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container != null) {
            container.setDimensional(0, false, this);
        }

        this.currentTier = -1;
        super.onStructureInvalid();
    }

    // ============== Status Update ==============

    protected void updateMachineStatus() {
        Level level = getLevel();
        if (level != null && !level.isClientSide) {
            if (isWorkingEnabled() && isFormed()) {
                recipeLogic.setStatus(RecipeLogic.Status.WORKING);
            }
        }
    }

    // ============== UI ==============

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117)
                .setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .setMaxWidthLimit(150)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer)
                .widget(new FancyMachineUIWidget(this, 198, 208));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            // 预留，可添加显示信息
        } else {
            Component tooltip = Component.translatable("gtceu.multiblock.invalid_structure.tooltip")
                    .withStyle(ChatFormatting.GRAY);
            textList.add(Component.translatable("gtceu.multiblock.invalid_structure")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip))));
        }
        this.definition.additionalDisplay.accept(this, textList);
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
