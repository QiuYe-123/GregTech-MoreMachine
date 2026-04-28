package cn.qiuye.gtmoremachine.common.item.behaviour;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.common.data.GTMMItems;
import cn.qiuye.gtmoremachine.utils.nbt.ItemStackNbtUtils;

import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.side.item.ItemTransferHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@GTMMDataGeneratorScanned
public record WirelessTransferCoverPlaceBehavior(CoverDefinition coverDefinition) implements IInteractionItem {

    private static final String WIRELESS_TRANSFER_PREFIX = "item.gtmoremachine.wireless_transfer";
    private static final String ADVANCED_WIRELESS_TRANSFER_PREFIX = "item.gtmoremachine.advanced_wireless_transfer";
    @GTMMRegisterLanguage(en = "§7Bind to: §f%s (%s)", cn = "§7已绑定容器：§f%s (%s)")
    public static final String WIRELESS_TRANSFER_TOOLTIP_1 = WIRELESS_TRANSFER_PREFIX + ".tooltip.1";
    @GTMMRegisterLanguage(en = "§7Right click the container with shift to bind container.Right click the air with shift to unbind.", cn = "§7潜行右键需要绑定的容器来进行绑定。潜行右键空气取消绑定。")
    public static final String WIRELESS_TRANSFER_TOOLTIP_2 = WIRELESS_TRANSFER_PREFIX + ".tooltip.2";
    @GTMMRegisterLanguage(en = "Success bind to: %s (%s)", cn = "绑定容器成功：%s (%s)")
    public static final String WIRELESS_TRANSFER_TOOLTIP_BIND_1 = WIRELESS_TRANSFER_PREFIX + ".tooltip.bind.1";
    @GTMMRegisterLanguage(en = "Success unbind.", cn = "解除绑定成功")
    public static final String WIRELESS_TRANSFER_TOOLTIP_BIND_2 = WIRELESS_TRANSFER_PREFIX + ".tooltip.bind.2";
    @GTMMRegisterLanguage(en = "§bTransfer Item§7 to §ebinded container§7 from the machine as §fCover§7.", cn = "§7作§f覆盖板§7时从机器中§b提取物品§7到§e绑定的容器§7中。")
    public static final String WIRELESS_TRANSFER_ITEM_TOOLTIP_1 = WIRELESS_TRANSFER_PREFIX + ".item.tooltip.1";
    @GTMMRegisterLanguage(en = "§bTransfer Fluid§7 to §ebinded container§7 from the machine as §fCover§7.", cn = "§7作§f覆盖板§7时从机器中§b提取流体§7到§e绑定的容器§7中。")
    public static final String WIRELESS_TRANSFER_FLUID_TOOLTIP_1 = WIRELESS_TRANSFER_PREFIX + ".fluid.tooltip.1";
    @GTMMRegisterLanguage(en = "§7Can use §f filter card", cn = "§7可使用§f过滤卡")
    public static final String ADVANCED_WIRELESS_TRANSFER_TOOLTIP_1 = ADVANCED_WIRELESS_TRANSFER_PREFIX + ".tooltip.1";

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            Player player = context.getPlayer();
            Level level = context.getLevel();
            BlockPos blockPos = context.getClickedPos();
            var itemTransfer = ItemTransferHelper.getItemTransfer(level, blockPos, context.getClickedFace());
            var fluidTransfer = FluidTransferHelper.getFluidTransfer(level, blockPos, context.getClickedFace());
            if (((itemStack.is(GTMMItems.WIRELESS_ITEM_TRANSFER_COVER.asItem()) || itemStack.is(GTMMItems.ADVANCED_WIRELESS_ITEM_TRANSFER_COVER.asItem())) && itemTransfer != null && itemTransfer.getSlots() > 0) || ((itemStack.is(GTMMItems.WIRELESS_FLUID_TRANSFER_COVER.asItem()) || itemStack.is(GTMMItems.ADVANCED_WIRELESS_FLUID_TRANSFER_COVER.asItem())) && fluidTransfer != null && fluidTransfer.getTanks() > 0)) {
                CompoundTag tag = new CompoundTag();
                tag.putString("dimensionid", level.dimension().location().toString());
                tag.putString("blockid", level.getBlockState(blockPos).getBlock().getDescriptionId());
                tag.putString("pos", blockPos.toShortString());
                tag.putString("facing", context.getClickedFace().toString());
                tag.putInt("x", blockPos.getX());
                tag.putInt("y", blockPos.getY());
                tag.putInt("z", blockPos.getZ());
                ItemStackNbtUtils.setTag(itemStack, tag);
                if (level.isClientSide()) player.sendSystemMessage(Component.translatable(WIRELESS_TRANSFER_TOOLTIP_BIND_1, Component.translatable(level.getBlockState(blockPos).getBlock().getDescriptionId()), blockPos.toShortString()));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(ItemStack item, Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack is = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStackNbtUtils.removeKeys(is, "dimensionid", "blockid", "pos", "facing", "x", "y", "z");
            if (level.isClientSide()) player.sendSystemMessage(Component.translatable(WIRELESS_TRANSFER_TOOLTIP_BIND_2));
        }
        return IInteractionItem.super.use(item, level, player, usedHand);
    }
}
