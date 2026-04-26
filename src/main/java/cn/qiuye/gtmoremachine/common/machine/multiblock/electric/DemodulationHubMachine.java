package cn.qiuye.gtmoremachine.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.ICCData;
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.IDimensionalBank;
import cn.qiuye.gtmoremachine.api.machine.multiblock.record.DimensionalBank;
import cn.qiuye.gtmoremachine.api.machine.trait.feature.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.common.data.machines.WirelessMachines;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DemodulationHubMachine extends WorkableMultiblockMachine
                                    implements IFancyUIMachine, IDisplayUIMachine, IDimensionalBank, IWirelessEnergyContainerHolder {

    public static final String CAPACITY_COMPONENT_HEADER = "DRNComponent_";

    @Getter
    private DimensionalRelayNodeBank capacityBank;
    @Getter
    @Setter
    @Nullable
    private WirelessEnergyContainer wirelessEnergyContainerCache;
    private final ConditionalSubscriptionHandler tickSubscription;

    public DemodulationHubMachine(BlockEntityCreationInfo holder) {
        super(holder);
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::updateMachineStatus, this::isFormed);
        this.capacityBank = this.attachTrait(new DimensionalRelayNodeBank(List.of()));
    }

    // ============== IWirelessEnergyContainerHolder ==============

    @Override
    public @Nullable UUID getUUID() {
        return getOwnerUUID();
    }

    @Override
    public boolean Capacity() {
        return true;
    }

    // ============== Computed Properties ==============

    public BigInteger getTotalCapacity() {
        return this.getCapacityBank().getTotalCapacity();
    }

    public BigInteger getTotalPassiveDrain() {
        return this.getCapacityBank().getTotalPassiveDrain();
    }

    // ============== Player Interaction ==============

    @Override
    public InteractionResult onUseWithItem(ExtendedUseOnContext context) {
        if (isRemote()) return InteractionResult.PASS;
        ItemStack item = context.getItemInHand();
        if (item.isEmpty()) return InteractionResult.PASS;

        if (item.is(GTItems.TOOL_DATA_STICK.get())) {
            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                container.setCapacity(this.isFormed(), this);
            }
            setOwnerUUID(context.getPlayer().getUUID());
            setWirelessEnergyContainerCache(null);

            context.getPlayer().sendSystemMessage(Component.translatable(
                    WirelessMachines.WIRELESS_ENERGY_HATCH_TOOLTIP_BIND,
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
            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                container.setCapacity(false, this);
            }
            setOwnerUUID(null);
            setWirelessEnergyContainerCache(null);

            player.sendSystemMessage(Component.translatable(
                    WirelessMachines.WIRELESS_ENERGY_HATCH_TOOLTIP_UNBIND));
            return true;
        }
        return false;
    }

    // ============== Multiblock Lifecycle ==============

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<ICCData> components = new ArrayList<>();

        for (Map.Entry<String, Object> battery : getMultiblockState().getMatchContext().entrySet()) {
            if (battery.getKey().startsWith(CAPACITY_COMPONENT_HEADER) &&
                    battery.getValue() instanceof ComponentMatchWrapper wrapper) {
                for (int i = 0; i < wrapper.amount; i++) {
                    components.add(wrapper.componentData);
                }
            }
        }

        if (components.isEmpty()) {
            onStructureInvalid();
            return;
        }

        if (this.capacityBank == null) {
            this.capacityBank = this.attachTrait(new DimensionalRelayNodeBank(components));
        } else {
            this.capacityBank = this.capacityBank.rebuild(components);
        }

        tickSubscription.updateSubscription();
    }

    @Override
    public void onStructureInvalid() {
        tickSubscription.unsubscribe();

        super.onStructureInvalid();
    }

    // ============== Status Update ==============

    protected void updateMachineStatus() {
        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container != null) {
            container.setCapacity(this.isFormed(), this);
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
        if (isFormed()) {
            if (this.getTotalCapacity().compareTo(BigInteger.ZERO) >= 0) {
                textList.add(Component.literal(NumberUtils.formatBigIntegerNumberOrSic(this.getTotalCapacity())));
            }
            if (this.getTotalPassiveDrain().compareTo(BigInteger.ZERO) >= 0) {
                textList.add(Component.literal(NumberUtils.formatBigIntegerNumberOrSic(this.getTotalPassiveDrain())));
            }
            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                var storagePercentage = container.getStoragePercentage();
                BigDecimal percentage = storagePercentage.storagePercentage();
                if (percentage.compareTo(BigDecimal.ZERO) >= 0) {
                    textList.add(Component.literal(percentage.toString()));
                }
            }
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
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean ignored) {
        // 固定为工作状态
    }

    @Override
    public DimensionalBank getDimensionalRelayNodeBank() {
        return new DimensionalBank(this.getTotalCapacity(), this.getTotalPassiveDrain());
    }

    // ============== Inner Class: DimensionalBank ==============

    public static class DimensionalRelayNodeBank extends MachineTrait {

        public static final MachineTraitType<DemodulationHubMachine.DimensionalRelayNodeBank> TYPE = new MachineTraitType<>(
                DemodulationHubMachine.DimensionalRelayNodeBank.class);

        @Override
        public MachineTraitType<?> getTraitType() {
            return TYPE;
        }

        @Getter
        private final BigInteger totalCapacity;
        @Getter
        private final BigInteger totalPassiveDrain;

        public DimensionalRelayNodeBank(List<ICCData> components) {
            super();
            this.totalCapacity = components.stream()
                    .map(ICCData::getCapacity)
                    .reduce(BigInteger.ZERO, BigInteger::add);
            this.totalPassiveDrain = components.stream()
                    .map(ICCData::getLossEnergy)
                    .reduce(BigInteger.ZERO, BigInteger::add);
        }

        public DimensionalRelayNodeBank rebuild(List<ICCData> component) {
            if (component.isEmpty()) {
                throw new IllegalArgumentException("Cannot rebuild bank with no batteries!");
            }
            return new DimensionalRelayNodeBank(component);
        }
    }

    // ============== Inner Class: ComponentMatchWrapper ==============

    public static class ComponentMatchWrapper {

        public final ICCData componentData;
        public int amount = 1;

        public ComponentMatchWrapper(ICCData componentData) {
            this.componentData = componentData;
        }

        public ComponentMatchWrapper increment() {
            amount++;
            return this;
        }
    }
}
