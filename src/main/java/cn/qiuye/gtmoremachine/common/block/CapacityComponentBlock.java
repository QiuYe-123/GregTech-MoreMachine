package cn.qiuye.gtmoremachine.common.block;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CapacityComponentBlock extends Block {

    @Getter
    private final ICapacityComponentData data;

    public CapacityComponentBlock(Properties properties, ICapacityComponentData data) {
        super(properties);
        this.data = data;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (this.data.getTier() == -1) {
            tooltip.add(Component.translatable("block.gtmoremachine.capacity_component.tooltip_empty"));
        } else {
            tooltip.add(Component.translatable("block.gtmoremachine.capacity_component.tooltip_filled",
                    FormattingUtil.formatNumbers(this.data.getCapacity())));
            tooltip.add(Component.translatable("block.gtmoremachine.capacity_component.tooltip_passive_drain",
                    FormattingUtil.formatNumbers(this.data.getLossEnergy())));
        }
    }

    @MethodsReturnNonnullByDefault
    public enum CapacityComponentBlockPartType implements StringRepresentable, ICapacityComponentData {

        EMPTY_TIER,
        LV(GTValues.LV),
        MV(GTValues.MV),
        HV(GTValues.HV),
        EV(GTValues.EV),
        IV(GTValues.IV),
        LuV(GTValues.LuV),
        ZPM(GTValues.ZPM),
        UV(GTValues.UV),
        UHV(GTValues.UHV),
        UEV(GTValues.UEV),
        UIV(GTValues.UIV),
        UXV(GTValues.UXV),
        OpV(GTValues.OpV),
        MAX(GTValues.MAX);

        private final int tier;
        private final BigInteger capacity;
        private final BigInteger lossEnergy;

        CapacityComponentBlockPartType() {
            this.tier = -1;
            this.capacity = BigInteger.ZERO;
            this.lossEnergy = BigInteger.ZERO;
        }

        CapacityComponentBlockPartType(int tier) {
            this.tier = tier;
            this.capacity = GTMMAPI.capacityComponentBlock(tier, true);
            this.lossEnergy = GTMMAPI.capacityComponentBlock(tier, false);
        }

        @Override
        public int getTier() {
            return tier;
        }

        @Override
        public BigInteger getCapacity() {
            return capacity;
        }

        @Override
        public BigInteger getLossEnergy() {
            return lossEnergy;
        }

        @Override
        public String getCapacityComponentName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName() {
            return getCapacityComponentName();
        }
    }
}
