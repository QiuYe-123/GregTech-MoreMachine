package cn.qiuye.gtmoremachine.api.misc;

import cn.qiuye.gtmoremachine.utils.FormattingUtil;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public interface ITransferData {

    UUID UUID();

    BigInteger Throughput();

    MetaMachine machine();

    default Component getInfo() {
        MetaMachine machine = machine();
        BigInteger eut = Throughput();
        String pos = machine.getPos().toShortString();
        return Component.translatable(machine.getBlockState().getBlock().getDescriptionId())
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("recipe.condition.dimension.tooltip", machine.getLevel().dimension().location()).append(" [").append(pos).append("] ").append(Component.translatable("gtmoremachine.machine.wireless_energy_monitor.tooltip.0", TeamUtils.getName(machine.getLevel(), UUID()))))))
                .append((eut.compareTo(BigInteger.ZERO) > 0 ? " +" : " ") + NumberUtils.formatBigIntegerNumberOrSic(eut)).append(" EU/t (").append(FormattingUtil.voltageName(new BigDecimal(eut))).append(")")
                .append(ComponentPanelWidget.withButton(Component.literal(" [ ] "), pos));
    }
}
