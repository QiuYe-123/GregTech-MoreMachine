package cn.qiuye.gtmoremachine.api.misc;

import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtil;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.math.BigDecimal;
import java.util.UUID;

public interface ITransferData {

    UUID UUID();

    long Throughput();

    MetaMachine machine();

    default Component getInfo() {
        MetaMachine machine = machine();
        long eut = Throughput();
        String pos = machine.getPos().toShortString();
        return Component.translatable(machine.getBlockState().getBlock().getDescriptionId())
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("recipe.condition.dimension.tooltip", machine.getLevel().dimension().location()).append(" [").append(pos).append("] ").append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", TeamUtil.GetName(machine.getLevel(), UUID()))))))
                .append((eut > 0 ? " +" : " ") + NumberUtils.formatBigDecimalNumberOrSic(BigDecimal.valueOf(eut))).append(" EU/t (").append(GTValues.VNF[GTUtil.getFloorTierByVoltage(Math.abs(eut))]).append(")")
                .append(ComponentPanelWidget.withButton(Component.literal(" [ ] "), pos));
    }
}
