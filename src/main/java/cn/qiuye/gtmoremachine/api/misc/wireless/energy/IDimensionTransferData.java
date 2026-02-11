package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.util.UUID;

public interface IDimensionTransferData {

    UUID UUID();

    int Voltagelevel();

    MetaMachine machine();

    default Component getInfo() {
        MetaMachine machine = machine();
        int Voltagelevel = Voltagelevel();
        String pos = machine.getPos().toShortString();
        return Component.translatable(machine.getBlockState().getBlock().getDescriptionId())
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("recipe.condition.dimension.tooltip",
                                machine.getLevel().dimension().location()).append(" [").append(pos).append("] ")
                                .append(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.0",
                                        TeamUtils.getName(machine.getLevel(), UUID()))))))
                .append(GTValues.VNF[Voltagelevel])
                .append(ComponentPanelWidget.withButton(Component.literal(" [ ] "), pos));
    }
}
