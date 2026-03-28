package cn.qiuye.gtmoremachine.api.misc.wireless.energy.feature;

import cn.qiuye.gtmoremachine.api.annotation.GTMMDataGeneratorScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
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

@GTMMDataGeneratorScanned
public interface ICapacitylimitData {

    @GTMMRegisterLanguage(en = "Passive energy consumption", cn = "被动耗能")
    String passive = "gtmoremachine.machine.wireless_monitor.tooltip.2";

    UUID UUID();

    BigInteger StorageCapacity();

    BigInteger PassiveDrain();

    MetaMachine machine();

    default Component getInfo(Format format) {
        MetaMachine machine = machine();
        BigDecimal euStorage = new BigDecimal(StorageCapacity());
        BigDecimal euPassiveDrain = new BigDecimal(PassiveDrain());
        String pos = machine.getBlockPos().toShortString();
        return Component.translatable(machine.getBlockState().getBlock().getDescriptionId())
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("recipe.condition.dimension.tooltip",
                                machine.getLevel().dimension().location()).append(" [").append(pos).append("] ")
                                .append(Component.translatable("gtmoremachine.machine.wireless_monitor.tooltip.0",
                                        TeamUtils.getName(machine.getLevel(), UUID()))))))
                .append(NumberUtils.formatBigDecimalNumberOrSic(euStorage, format))
                .append(" EU (")
                .append(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(euStorage), format) + " A ")
                .append(FormattingUtil.voltageName(euStorage)).append(")")
                .append("\n")
                .append(Component.translatable(passive))
                .append(NumberUtils.formatBigDecimalNumberOrSic(euPassiveDrain, format))
                .append(" EU (")
                .append(NumberUtils.formatBigDecimalNumberOrSic(FormattingUtil.voltageAmperage(euPassiveDrain), format) + " A ")
                .append(FormattingUtil.voltageName(euPassiveDrain)).append(")")
                .append(ComponentPanelWidget.withButton(Component.literal(" [ ] "), pos));
    }
}
