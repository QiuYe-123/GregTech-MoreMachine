package cn.qiuye.gtmoremachine.api.misc.wireless.cwu;

import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.utils.NumberUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.util.UUID;

public interface ITransferData {

    UUID UUID();

    int Throughput();

    MetaMachine machine();

    default Component getInfo(Format format) {
        MetaMachine machine = machine();
        int cwu = Throughput();
        String pos = machine.getBlockPos().toShortString();
        return Component.translatable(machine.getBlockState().getBlock().getDescriptionId())
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("recipe.condition.dimension.tooltip",
                                machine.getLevel().dimension().location()).append(" [").append(pos).append("] ")
                                .append(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.0",
                                        TeamUtils.getName(machine.getLevel(), UUID()))))))
                .append((cwu > 0 ? " +" : " ") + NumberUtils.formatInt(cwu, format))
                .append(ComponentPanelWidget.withButton(Component.literal(" [ ] "), pos));
    }
}
