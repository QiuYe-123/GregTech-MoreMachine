package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
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
@GTMMDataGeneratorScanned
public class HugeBusPartMachine extends TieredIOPartMachine implements IDistinctPart, IPaintable {

    private static final String HUGE_ITEM_BUS_PREFIX = "gtmoremachine.machine.huge_item_bus";
    private static final String SHARE_INVENTORY_PREFIX = "gui.gtmoremachine.share_inventory";
    @GTMMRegisterLanguage(en = "Inputs items for multiblock structures, with each slot able to store up to 2^31-1 items.", cn = "为多方块结构输入物品，每个槽位最多可存储2^31-1个物品。")
    public static final String HUGE_ITEM_BUS_IMPORT_TOOLTIP = HUGE_ITEM_BUS_PREFIX + ".import.tooltip";
    @GTMMRegisterLanguage(en = "Outputs items for multiblock structures, with each slot able to store up to 2^31-1 items.", cn = "为多方块结构输出物品，每个槽位最多可存储2^31-1个物品。")
    public static final String HUGE_ITEM_BUS_EXPORT_TOOLTIP = HUGE_ITEM_BUS_PREFIX + ".export.tooltip";
    @GTMMRegisterLanguage(en = "Returns all items to the container in front.", cn = "退回所有物品到面前的容器中。")
    public static final String HUGE_ITEM_BUS_TOOLTIP_1 = HUGE_ITEM_BUS_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Item slots: %s/%s", cn = "物品槽位: %s/%s")
    public static final String HUGE_ITEM_BUS_TOOLTIP_2 = HUGE_ITEM_BUS_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Empty", cn = "空")
    public static final String HUGE_ITEM_BUS_TOOLTIP_3 = HUGE_ITEM_BUS_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "Catalyst", cn = "催化剂")
    public static final String SHARE_INVENTORY_TITLE = SHARE_INVENTORY_PREFIX + ".title";
    @GTMMRegisterLanguage(en = "Open Catalyst Slot", cn = "打开催化剂槽")
    public static final String SHARE_INVENTORY_DESC_0 = SHARE_INVENTORY_PREFIX + ".desc.0";
    @GTMMRegisterLanguage(en = "In the catalyst slot, only non-consumable items can participate in the synthesis.", cn = "在催化剂槽中只有不消耗的物品才能参与合成。")
    public static final String SHARE_INVENTORY_DESC_1 = SHARE_INVENTORY_PREFIX + ".desc.1";
    @GTMMRegisterLanguage(en = "Common items can be automatically input by placing a container in front of the input bus.", cn = "普通物品请通过在输入总线面前放置容器以自动输入。")
    public static final String SHARE_INVENTORY_DESC_2 = SHARE_INVENTORY_PREFIX + ".desc.2";

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
                    .setTooltips(List.of(Component.translatable(HUGE_ITEM_BUS_TOOLTIP_1))));
            configuratorPanel.attachConfigurators(new InventoryFancyConfigurator(
                    shareInventory.storage, Component.translatable(SHARE_INVENTORY_TITLE))
                    .setTooltips(List.of(
                            Component.translatable(SHARE_INVENTORY_DESC_0),
                            Component.translatable(SHARE_INVENTORY_DESC_1),
                            Component.translatable(SHARE_INVENTORY_DESC_2))));
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
            textList.add(Component.translatable(HUGE_ITEM_BUS_TOOLTIP_3));
        }
        textList.add(0, Component.translatable(HUGE_ITEM_BUS_TOOLTIP_2, itemCount, getInventorySize())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
    }
}
