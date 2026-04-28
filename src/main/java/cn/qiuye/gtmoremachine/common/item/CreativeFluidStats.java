package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.gui.widget.TerminalInputWidget;
import cn.qiuye.gtmoremachine.api.misc.CreativeFluidHandlerItemStack;
import cn.qiuye.gtmoremachine.utils.nbt.ItemStackNbtUtils;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.widget.PhantomFluidWidget;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IComponentCapability;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.common.data.item.GTDataComponents;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SwitchWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.List;

@GTMMDataGeneratorScanned
public class CreativeFluidStats implements IItemComponent, IComponentCapability, IAddInformation, IItemUIFactory {

    private static final String CREATIVE_FLUID_CELL_PREFIX = "item.gtmoremachine.creative_fluid_cell";
    @GTMMRegisterLanguage(en = "§2Fluid Stored: §f%s", cn = "§2内部流体：§f%1$s")
    public static final String CREATIVE_FLUID_CELL_TOOLTIP_1 = CREATIVE_FLUID_CELL_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "Right click to open GUI to set fluid.", cn = "右键打开设置窗口来指定流体。")
    public static final String CREATIVE_FLUID_CELL_TOOLTIP_2 = CREATIVE_FLUID_CELL_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Enabled Accurate output(%1$d mB)", cn = "已启用精确输出(%1$d mB)")
    public static final String CREATIVE_FLUID_CELL_TOOLTIP_3 = CREATIVE_FLUID_CELL_PREFIX + ".tooltip.3";
    @GTMMRegisterLanguage(en = "Enable Accurate output", cn = "启用精确输出")
    public static final String CREATIVE_FLUID_CELL_GUI_BUTTON_1 = CREATIVE_FLUID_CELL_PREFIX + ".gui.button.1";
    @GTMMRegisterLanguage(en = "Disable Accurate output", cn = "禁用精确输出")
    public static final String CREATIVE_FLUID_CELL_GUI_BUTTON_2 = CREATIVE_FLUID_CELL_PREFIX + ".gui.button.2";
    @GTMMRegisterLanguage(en = "§7You just need Creative Mode§7 to use this", cn = "§7你需要§b创造模式§7来使用它")
    public static final String CREATIVE_TOOLTIP = "gtmoremachine.creative_tooltip";

    private ItemStack itemStack;
    protected final CustomFluidTank creativeTank;

    public CreativeFluidStats() {
        this.creativeTank = new CustomFluidTank(1000);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable(CREATIVE_TOOLTIP));
        if (!getStored(stack).isEmpty()) {
            FluidUtil.getFluidContained(stack).ifPresent(tank -> tooltipComponents
                    .add(Component.translatable(CREATIVE_FLUID_CELL_TOOLTIP_1, tank.getHoverName())));
            if (getAccurate(stack)) {
                tooltipComponents
                        .add(Component.translatable(CREATIVE_FLUID_CELL_TOOLTIP_3, getCapacity(stack)));
            }
        } else {
            tooltipComponents.add(Component.translatable(CREATIVE_FLUID_CELL_TOOLTIP_2));
        }
    }

    @Override
    public void attachCapabilities(RegisterCapabilitiesEvent event, Item item) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (itemStack, ignored) -> {
            FluidStack fluidStack = getStored(itemStack);
            int capacity = getAccurate(itemStack) ? getCapacity(itemStack) : Integer.MAX_VALUE;
            return fluidStack.isEmpty() ? null : new CreativeFluidHandlerItemStack(itemStack, capacity, fluidStack);
        }, item);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        return new ModularUI(176, 166, holder, entityPlayer)
                .widget(createWidget())
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(), GuiTextures.SLOT, 7, 50,
                        true));
    }

    private Widget createWidget() {
        var group = new WidgetGroup(0, 0, 176, 131);
        group.setBackground(new ResourceTexture("gtceu:textures/gui/base/bordered_background.png"));
        group.addWidget(new PhantomFluidWidget(creativeTank, 0, 36, 6, 18, 18, this::getStored, this::setStored)
                .setShowAmount(false)
                .setBackground(GuiTextures.FLUID_SLOT));
        group.addWidget(new LabelWidget(7, 9, "gtceu.creative.tank.fluid"));
        group.addWidget(new SwitchWidget(7, 30, 60, 15, (clickData, aBoolean) -> setAccurate(aBoolean))
                .setTexture(
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("item.gtmoremachine.creative_fluid_cell.gui.button.1")),
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("item.gtmoremachine.creative_fluid_cell.gui.button.2")))
                .setPressed(getAccurate()));
        group.addWidget(new TerminalInputWidget(72, 31, 90, 10, this::getCapacity, this::setCapacity)
                .setMin(1).setMax(Integer.MAX_VALUE));

        return group;
    }

    private boolean getAccurate() {
        return getAccurate(this.itemStack);
    }

    private boolean getAccurate(ItemStack fluidCell) {
        CompoundTag tagCompound = ItemStackNbtUtils.getTag(fluidCell);
        return tagCompound.contains("Accurate") && tagCompound.getBoolean("Accurate");
    }

    private void setAccurate(boolean isEnable) {
        ItemStackNbtUtils.updateTag(this.itemStack, tag -> tag.putBoolean("Accurate", isEnable));
    }

    private FluidStack getStored() {
        return getStored(this.itemStack);
    }

    private FluidStack getStored(ItemStack fluidCell) {
        FluidStack stored = fluidCell.getOrDefault(GTDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
        if (!stored.isEmpty()) {
            return stored;
        }
        CompoundTag tagCompound = ItemStackNbtUtils.getTag(fluidCell);
        if (!tagCompound.contains("Fluid", Tag.TAG_COMPOUND)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.parseOptional(GTRegistries.builtinRegistry(), tagCompound.getCompound("Fluid"));
    }

    private void setStored(FluidStack fluid) {
        if (fluid.isEmpty()) {
            this.creativeTank.setFluid(FluidStack.EMPTY);
            this.itemStack.remove(GTDataComponents.FLUID_CONTENT);
            ItemStackNbtUtils.removeKeys(this.itemStack, "Fluid");
            setAccurate(false);
        } else {
            FluidStack stored = fluid.copy();
            stored.setAmount(1000);
            this.creativeTank.setFluid(stored);
            this.itemStack.set(GTDataComponents.FLUID_CONTENT, SimpleFluidContent.copyOf(stored));
            ItemStackNbtUtils.updateTag(this.itemStack,
                    tag -> tag.put("Fluid", stored.save(GTRegistries.builtinRegistry())));
        }
    }

    private int getCapacity() {
        return getCapacity(this.itemStack);
    }

    private int getCapacity(ItemStack fluidCell) {
        CompoundTag tagCompound = ItemStackNbtUtils.getTag(fluidCell);
        return tagCompound.contains("Capacity") ? tagCompound.getInt("Capacity") : 1000;
    }

    private void setCapacity(int capacity) {
        ItemStackNbtUtils.updateTag(this.itemStack, tag -> tag.putInt("Capacity", capacity));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(ItemStack item, Level level, Player player, InteractionHand usedHand) {
        this.itemStack = item;
        this.creativeTank.setFluid(getStored());
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }
}
