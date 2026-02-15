package cn.qiuye.gtmoremachine.common.machine.multiblock.electric;

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder;
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
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

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DemodulationHubMachine extends WorkableMultiblockMachine
                                    implements IFancyUIMachine, IDisplayUIMachine, IWirelessEnergyContainerHolder {

    public static final String CAPACITY_COMPONENT_HEADER = "DRNComponent_";

    private DimensionalRelayNodeBank capacityBank;
    private WirelessEnergyContainer wirelessEnergyContainerCache;
    private final ConditionalSubscriptionHandler tickSubscription;

    public DemodulationHubMachine(BlockEntityCreationInfo holder) {
        super(holder);
        this.capacityBank = new DimensionalRelayNodeBank(this, new ArrayList<>());
        this.tickSubscription = new ConditionalSubscriptionHandler(
                this,
                this::updateMachineStatus,
                this::isFormed);
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

    @Override
    public void setWirelessEnergyContainerCache(WirelessEnergyContainer container) {
        this.wirelessEnergyContainerCache = container;
    }

    @Override
    public WirelessEnergyContainer getWirelessEnergyContainerCache() {
        return wirelessEnergyContainerCache;
    }

    // ============== Computed Properties ==============

    public BigInteger getTotalCapacity() {
        return capacityBank == null ? BigInteger.ZERO : capacityBank.totalCapacity;
    }

    public BigInteger getTotalPassiveDrain() {
        return capacityBank == null ? BigInteger.ZERO : capacityBank.totalPassiveDrain;
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
                container.setCapacity(this.getTotalCapacity(), this.getTotalPassiveDrain(), true, this);
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
            wirelessEnergyContainerCache = null;

            WirelessEnergyContainer container = getWirelessEnergyContainer();
            if (container != null) {
                container.setCapacity(BigInteger.ZERO, BigInteger.ZERO, false, this);
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

        List<ICapacityComponentData> components = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = getMultiblockState().getMatchContext().entrySet();

        for (Map.Entry<String, Object> battery : entries) {
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
            this.capacityBank = new DimensionalRelayNodeBank(this, components);
        } else {
            this.capacityBank = this.capacityBank.rebuild(components);
        }

        tickSubscription.updateSubscription();

        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container != null) {
            container.setCapacity(this.getTotalCapacity(), this.getTotalPassiveDrain(), true, this);
        }
    }

    @Override
    public void onStructureInvalid() {
        this.capacityBank = null;
        tickSubscription.updateSubscription();

        WirelessEnergyContainer container = getWirelessEnergyContainer();
        if (container != null) {
            container.setCapacity(BigInteger.ZERO, BigInteger.ZERO, false, this);
        }

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

    // ============== ManagedField ==============

    @Override
    public boolean isRemote() {
        return super.isRemote();
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
        return new ModularUI(198, 208, this, entityPlayer)
                .widget(new FancyMachineUIWidget(this, 198, 208));
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
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean ignored) {
        // 固定为工作状态
    }

    // ============== Inner Class: DimensionalRelayNodeBank ==============

    public static class DimensionalRelayNodeBank extends MachineTrait {

        public final BigInteger totalCapacity;
        public final BigInteger totalPassiveDrain;

        public DimensionalRelayNodeBank(MetaMachine machine, List<ICapacityComponentData> components) {
            super(machine);
            this.totalCapacity = components.stream()
                    .map(ICapacityComponentData::getCapacity)
                    .reduce(BigInteger.ZERO, BigInteger::add);
            this.totalPassiveDrain = components.stream()
                    .map(ICapacityComponentData::getLossEnergy)
                    .reduce(BigInteger.ZERO, BigInteger::add);
        }

        public DimensionalRelayNodeBank rebuild(List<ICapacityComponentData> component) {
            if (component.isEmpty()) {
                throw new IllegalArgumentException("Cannot rebuild bank with no batteries!");
            }
            return new DimensionalRelayNodeBank(machine, component);
        }

        @Override
        public MachineTraitType<?> getTraitType() {
            return null;
        }
    }

    // ============== Inner Class: ComponentMatchWrapper ==============

    public static class ComponentMatchWrapper {

        public final ICapacityComponentData componentData;
        public int amount = 1;

        public ComponentMatchWrapper(ICapacityComponentData componentData) {
            this.componentData = componentData;
        }

        public ComponentMatchWrapper increment() {
            amount++;
            return this;
        }
    }
}
