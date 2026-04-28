package cn.qiuye.gtmoremachine.common.item;

import cn.qiuye.gtmoremachine.common.data.GTMMDataComponents;
import cn.qiuye.gtmoremachine.common.item.datacomponents.VirtualItemProviderData;
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class VirtualItemProviderBehavior implements IAddInformation, IItemUIFactory, IFancyUIProvider {

    public static final VirtualItemProviderBehavior INSTANCE = new VirtualItemProviderBehavior();

    private static ItemStack setVirtualItem(ItemStack stack, ItemStack virtualItem) {
        setVirtualItemProviderData(stack, getVirtualItemProviderData(stack).withVirtualItem(virtualItem));
        return stack;
    }

    public static ItemStack getVirtualItem(ItemStack item) {
        var component = item.get(GTMMDataComponents.VIRTUAL_ITEM_PROVIDER.get());
        if (component != null && !component.virtualItem().isEmpty()) {
            return component.virtualItem().copy();
        }
        return ItemStack.EMPTY;
    }

    public static boolean hasVirtualItem(ItemStack item) {
        return !getVirtualItem(item).isEmpty();
    }

    public static boolean isMarked(ItemStack item) {
        var component = item.get(GTMMDataComponents.VIRTUAL_ITEM_PROVIDER.get());
        return component != null && component.marked();
    }

    private Player player;
    private InteractionHand hand;

    @Override
    public void appendHoverText(@NotNull ItemStack itemstack, Item.TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (hasVirtualItem(itemstack)) {
            list.add(Component.translatable("gui.ae2.Items").append(": ").append(getVirtualItem(itemstack).getDisplayName()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(ItemStack item, Level level, Player player, InteractionHand usedHand) {
        this.player = player;
        hand = usedHand;
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder holder, Player entityPlayer) {
        return new ModularUI(176, 166, holder, entityPlayer).widget(new FancyMachineUIWidget(this, 176, 166));
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        WidgetGroup group = new WidgetGroup(0, 0, 18 + 16, 18 + 16);
        WidgetGroup container = new WidgetGroup(4, 4, 18 + 8, 18 + 8);
        container.addWidget(new SlotWidget(new ItemHandler(player, hand), 0, 4, 4, true, true).setBackground(GuiTextures.SLOT));
        group.addWidget(container);
        return group;
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get());
    }

    @Override
    public Component getTitle() {
        return Component.translatable(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get().getDescriptionId());
    }

    private record ItemHandler(Player entityPlayer, InteractionHand hand) implements IItemHandlerModifiable {

        private ItemStack getItem() {
            return entityPlayer.getItemInHand(hand);
        }

        @Override
        public void setStackInSlot(int i, @NotNull ItemStack arg) {}

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            return getVirtualItem(getItem());
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
            if (arg.is(GTMMAEItems.VIRTUAL_ITEM_PROVIDER.get())) return arg;
            entityPlayer.setItemInHand(hand, setVirtualItem(getItem(), arg.copyWithCount(1)));
            return arg.copyWithCount(arg.getCount() - 1);
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int j, boolean bl) {
            if (isMarked(getItem())) return ItemStack.EMPTY;
            setVirtualItem(getItem(), ItemStack.EMPTY);
            return getStackInSlot(0);
        }

        @Override
        public int getSlotLimit(int i) {
            return 1;
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack arg) {
            return true;
        }
    }

    private static VirtualItemProviderData getVirtualItemProviderData(ItemStack stack) {
        return stack.getOrDefault(GTMMDataComponents.VIRTUAL_ITEM_PROVIDER.get(), VirtualItemProviderData.DEFAULT);
    }

    static void setVirtualItemProviderData(ItemStack stack, VirtualItemProviderData data) {
        stack.set(GTMMDataComponents.VIRTUAL_ITEM_PROVIDER.get(), data);
    }
}
