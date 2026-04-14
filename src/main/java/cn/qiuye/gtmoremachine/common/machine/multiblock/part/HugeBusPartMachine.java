package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.machine.fancyconfigurator.ButtonConfigurator;
import cn.qiuye.gtmoremachine.api.machine.fancyconfigurator.InventoryFancyConfigurator;
import cn.qiuye.gtmoremachine.api.machine.trait.CatalystItemStackHandler;
import cn.qiuye.gtmoremachine.api.misc.UnlimitedItemStackTransfer;
import cn.qiuye.gtmoremachine.api.transfer.UnlimitItemTransferHelper;
import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.blockentity.IPaintable;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CircuitFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDistinctPart;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.utils.ISubscription;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.side.item.ItemTransferHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandlerModifiable;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HugeBusPartMachine extends TieredIOPartMachine implements IDistinctPart, IPaintable {

    public static final int INV_MULTIPLE = 2;
    @Getter
    @SaveField
    protected NotifiableItemStackHandler inventory;
    @Nullable
    protected TickableSubscription autoIOSubs;
    protected ISubscription inventorySubs;
    @Getter
    @SaveField
    protected final NotifiableItemStackHandler circuitInventory;
    @Getter
    @SaveField
    protected final CatalystItemStackHandler shareInventory;

    public HugeBusPartMachine(BlockEntityCreationInfo holder, int tier, IO io) {
        this(holder, tier, io, 4);
    }

    public HugeBusPartMachine(BlockEntityCreationInfo holder, int tier, IO io, int shareSize) {
        super(holder, tier, io);
        this.inventory = this.attachTrait(createInventory(io));
        this.circuitInventory = this.attachTrait(createCircuitItemHandler(io));
        this.shareInventory = this.attachTrait(new CatalystItemStackHandler(shareSize, IO.IN, IO.NONE));
    }

    //////////////////////////////////////
    // ***** Initialization ******//
    //////////////////////////////////////

    protected int getInventorySize() {
        if (getTier() < GTValues.EV) return 1 + getTier();
        else return (1 + getTier()) * INV_MULTIPLE;
    }

    protected NotifiableItemStackHandler createInventory(IO io) {
        return new NotifiableItemStackHandler(getInventorySize(), io, io, UnlimitedItemStackTransfer::new) {

            @Override
            public boolean canCapOutput() {
                return true;
            }
        };
    }

    protected NotifiableItemStackHandler createCircuitItemHandler(IO io) {
        if (io == IO.IN) {
            return new NotifiableItemStackHandler(1, IO.IN, IO.NONE)
                    .setFilter(IntCircuitBehaviour::isIntegratedCircuit);
        } else {
            return new NotifiableItemStackHandler(0, IO.NONE);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateInventorySubscription));
        }
        getHandlerList().setColor(getPaintingColor());
        inventorySubs = getInventory().addChangedListener(this::updateInventorySubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (inventorySubs != null) {
            inventorySubs.unsubscribe();
            inventorySubs = null;
        }
    }

    @Override
    public void onPaintingColorChanged(int color) {
        getHandlerList().setColor(color, true);
    }

    @Override
    public int tintColor(int index) {
        if (index == 9) return getRealColor();
        return -1;
    }

    @Override
    public boolean isDistinct() {
        return getInventory().isDistinct() && circuitInventory.isDistinct() && shareInventory.isDistinct();
    }

    @Override
    public void setDistinct(boolean isDistinct) {
        getInventory().setDistinct(isDistinct);
        circuitInventory.setDistinct(isDistinct);
        shareInventory.setDistinct(isDistinct);
    }

    protected void refundAll(ClickData clickData) {
        if (ItemTransferHelper.getItemTransfer(getLevel(), getBlockPos().relative(getFrontFacing()),
                getFrontFacing().getOpposite()) != null) {
            setWorkingEnabled(false);
            exportToNearby(getInventory(), getFrontFacing());
        }
    }
    //////////////////////////////////////
    // ******** Auto IO *********//
    //////////////////////////////////////

    @Override
    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);
        updateInventorySubscription();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        updateInventorySubscription();
    }

    @Override
    public void onMachineDestroyed() {
        clearInventory(shareInventory);
    }

    public void clearInventory(IItemHandlerModifiable inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                inventory.setStackInSlot(i, ItemStack.EMPTY);
                Block.popResource(getLevel(), getBlockPos(), stackInSlot);
            }
        }
    }

    protected void updateInventorySubscription() {
        if (isWorkingEnabled() && ((io == IO.OUT && !getInventory().isEmpty()) || io == IO.IN) &&
                ItemTransferHelper.getItemTransfer(getLevel(), getBlockPos().relative(getFrontFacing()),
                        getFrontFacing().getOpposite()) != null) {
            autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
        } else if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    protected void autoIO() {
        if (getOffsetTimer() % 5 == 0) {
            if (isWorkingEnabled()) {
                if (io == IO.OUT) {
                    exportToNearby(getInventory(), getFrontFacing());
                } else if (io == IO.IN) {
                    getInventory().importFromNearby(getFrontFacing());
                }
            }
            updateInventorySubscription();
        }
    }

    public void exportToNearby(NotifiableItemStackHandler handler, Direction... facings) {
        if (handler.isEmpty()) return;
        var level = getLevel();
        var pos = getBlockPos();
        for (Direction facing : facings) {
            UnlimitItemTransferHelper.exportToTarget(handler, Integer.MAX_VALUE, f -> true, level, pos.relative(facing),
                    facing.getOpposite());
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        updateInventorySubscription();
    }

    //////////////////////////////////////
    // ********** GUI ***********//
    //////////////////////////////////////

    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        IDistinctPart.super.attachConfigurators(configuratorPanel);
        if (this.io == IO.IN) {
            configuratorPanel.attachConfigurators(new CircuitFancyConfigurator(circuitInventory.storage));
            configuratorPanel.attachConfigurators(new ButtonConfigurator(new GuiTextureGroup(GuiTextures.BUTTON, new TextTexture("🔙")), this::refundAll)
                    .setTooltips(List.of(Component.translatable("gtmoremachine.machine.huge_item_bus.tooltip.1"))));
            configuratorPanel.attachConfigurators(new InventoryFancyConfigurator(
                    shareInventory.storage, Component.translatable("gui.gtmoremachine.share_inventory.title"))
                    .setTooltips(List.of(
                            Component.translatable("gui.gtmoremachine.share_inventory.desc.0"),
                            Component.translatable("gui.gtmoremachine.share_inventory.desc.1"),
                            Component.translatable("gui.gtmoremachine.share_inventory.desc.2"))));
        }
    }

    @Override
    public Widget createUIWidget() {
        int height = 117;
        int width = 178;
        var group = new WidgetGroup(0, 0, width + 8, height + 4);

        var componentPanel = new ComponentPanelWidget(8, 5, this::addDisplayText).setMaxWidthLimit(width - 16);
        var screen = new DraggableScrollableWidgetGroup(4, 4, width, height)
                .setBackground(GuiTextures.DISPLAY)
                .addWidget(componentPanel);
        group.addWidget(screen);

        return group;
    }

    private void addDisplayText(List<Component> textList) {
        int itemCount = 0;
        for (int i = 0; i < getInventorySize(); i++) {
            ItemStack is = getInventory().getStackInSlot(i);
            if (!is.isEmpty()) {
                textList.add(is.getDisplayName().copy()
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW))
                        .append(Component.literal(NumberUtils.formatLong(is.getCount()))
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))));
                itemCount++;
            }
        }
        if (textList.isEmpty()) {
            textList.add(Component.translatable("gtmoremachine.machine.huge_item_bus.tooltip.3"));
        }
        textList.add(0, Component.translatable("gtmoremachine.machine.huge_item_bus.tooltip.2", itemCount, getInventorySize())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
    }
}
