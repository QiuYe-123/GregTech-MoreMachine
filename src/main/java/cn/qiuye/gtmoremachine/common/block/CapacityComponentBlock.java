package cn.qiuye.gtmoremachine.common.block;

import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData;

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

import static com.gregtechceu.gtceu.api.GTValues.*;

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
        LV_CAPACITY(LV, 0),
        MV_CAPACITY(MV, 0),
        HV_CAPACITY(HV, 0),
        EV_CAPACITY(EV, 0),
        IV_CAPACITY(IV, 0),
        LuV_CAPACITY(LuV, 0),
        ZPM_CAPACITY(ZPM, 0),
        UV_CAPACITY(UV, 0),
        UHV_CAPACITY(UHV, 0),
        UEV_CAPACITY(UEV, 0),
        UIV_CAPACITY(UIV, 0),
        UXV_CAPACITY(UXV, 0),
        OpV_CAPACITY(OpV, 0),
        MAX_CAPACITY(MAX, 0);

        private final int tier;
        private final BigInteger capacity;
        private final BigInteger lossEnergy;

        CapacityComponentBlockPartType() {
            this.tier = -1;
            this.capacity = BigInteger.ZERO;
            this.lossEnergy = BigInteger.ZERO;
        }

        CapacityComponentBlockPartType(int tier, int lossFactor) {
            this.tier = tier;
            BigInteger pow = BigInteger.valueOf(Long.MAX_VALUE);
            this.capacity = pow.multiply(BigInteger.valueOf((long) tier * tier));
            if (lossFactor != 0) {
                BigInteger baseLoss = BigInteger.valueOf(2).pow(lossFactor - 1);
                BigInteger multiplier = BigInteger.valueOf(lossFactor).pow(4);

                this.lossEnergy = baseLoss.multiply(multiplier);
            } else {
                this.lossEnergy = BigInteger.ZERO;
            }
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
