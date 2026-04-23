package cn.qiuye.gtmoremachine.common.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.machine.trait.CatalystFluidStackHandler;
import cn.qiuye.gtmoremachine.utils.NumberUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.FancyTankConfigurator;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.utils.ISubscription;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.side.item.ItemTransferHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import lombok.Getter;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HugeDualHatchPartMachine extends HugeBusPartMachine {

    @SaveField
    protected final NotifiableFluidTank tank;
    protected ISubscription tankSubs;

    @SaveField
    @Getter
    protected final CatalystFluidStackHandler shareTank;

    private boolean hasFluidTransfer;
    private boolean hasItemTransfer;

    public HugeDualHatchPartMachine(@UnknownNullability BlockEntityCreationInfo holder, int tier, IO io) {
        super(holder, tier, io, 9);
        this.tank = this.attachTrait(createTank());
        this.shareTank = this.attachTrait(new CatalystFluidStackHandler(9, 16000, IO.IN, IO.NONE));
    }

    protected NotifiableFluidTank createTank() {
        return new NotifiableFluidTank(this.getTankInventorySize(), Integer.MAX_VALUE, io) {

            @Override
            public boolean canCapOutput() {
                return true;
            }
        };
    }

    protected int getTankInventorySize() {
        return this.getTier() + 1;
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new FancyTankConfigurator(shareTank.getStorages(), Component.translatable("gui.gtceu.share_tank.title")).setTooltips(List.of(Component.translatable("gui.gtceu.share_tank.desc.0"),
                Component.translatable("gui.gtceu.share_inventory.desc.1"))));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.tankSubs = this.tank.addChangedListener(this::updateInventorySubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.tankSubs != null) {
            this.tankSubs.unsubscribe();
            this.tankSubs = null;
        }
    }

    @Override
    protected void refundAll(ClickData clickData) {
        // 退不回去流体
        super.refundAll(clickData);
        if (hasFluidTransfer) {
            this.tank.exportToNearby(this.getFrontFacing());
        }
    }

    @Override
    protected void updateInventorySubscription() {
        boolean canOutput = this.io == IO.OUT && (!this.tank.isEmpty() || !this.getInventory().isEmpty());
        Level level = this.getLevel();
        if (level != null) {
            this.hasItemTransfer = ItemTransferHelper.getItemTransfer(level, this.getBlockPos().relative(this.getFrontFacing()), this.getFrontFacing().getOpposite()) != null;
            this.hasFluidTransfer = FluidTransferHelper.getFluidTransfer(level, this.getBlockPos().relative(this.getFrontFacing()), this.getFrontFacing().getOpposite()) != null;
        } else {
            this.hasItemTransfer = false;
            this.hasFluidTransfer = false;
        }

        if (!this.isWorkingEnabled() || !canOutput && this.io != IO.IN || !this.hasItemTransfer && !this.hasFluidTransfer) {
            if (this.autoIOSubs != null) {
                this.autoIOSubs.unsubscribe();
                this.autoIOSubs = null;
            }
        } else {
            this.autoIOSubs = this.subscribeServerTick(this.autoIOSubs, this::autoIO);
        }
    }

    @Override
    protected void autoIO() {
        if (this.getOffsetTimer() % 5 == 0) {
            if (isWorkingEnabled()) {
                if (this.io == IO.OUT) {
                    if (this.hasItemTransfer) {
                        this.getInventory().exportToNearby(this.getFrontFacing());
                    }

                    if (this.hasFluidTransfer) {
                        this.tank.exportToNearby(this.getFrontFacing());
                    }
                } else if (this.io == IO.IN) {
                    if (this.hasItemTransfer) {
                        this.getInventory().importFromNearby(this.getFrontFacing());
                    }

                    if (this.hasFluidTransfer) {
                        this.tank.importFromNearby(this.getFrontFacing());
                    }
                }
            }
        }
    }

    @Override
    public Widget createUIWidget() {
        int height = 117;
        int width = 178;
        WidgetGroup group = new WidgetGroup(0, 0, width + 8, height + 4);
        ComponentPanelWidget componentPanel = (new ComponentPanelWidget(8, 5, this::addDisplayText)).setMaxWidthLimit(width - 16);
        WidgetGroup screen = (new DraggableScrollableWidgetGroup(4, 4, width, height)).setBackground(GuiTextures.DISPLAY).addWidget(componentPanel);
        group.addWidget(screen);
        return group;
    }

    private void addDisplayText(List<Component> textList) {
        int itemCount = 0;
        int tankCount = 0;

        // item
        for (int i = 0; i < super.getInventorySize() - 1; ++i) {
            ItemStack is = super.getInventory().getStackInSlot(i);
            if (!is.isEmpty()) {
                textList.add(is.getDisplayName().copy().setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW))
                        .append(Component.literal(NumberUtils.formatLong(is.getCount())).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))));
                ++itemCount;
            }
        }

        // tank
        for (int i = 0; i < this.getTankInventorySize(); ++i) {
            FluidStack fs = this.tank.getFluidInTank(i);
            if (!fs.isEmpty()) {
                textList.add(fs.getDisplayName().copy().setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                        .append(Component.literal(fs.getAmount() < 1000 ? fs.getAmount() + "mB" :
                                NumberUtils.formatLong(fs.getAmount() / 1000L) + "B")
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA))));
                ++tankCount;
            }
        }

        if (textList.isEmpty()) {
            textList.add(Component.translatable("gtmoremachine.machine.huge_item_bus.tooltip.3"));
        }

        textList.add(0, Component.translatable("gtmoremachine.machine.huge_item_bus.tooltip.2", itemCount, super.getInventorySize())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        textList.add(1, Component.translatable("gtmoremachine.machine.huge_dual_hatch.tooltip.2", tankCount, this.getTankInventorySize())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
    }
}
