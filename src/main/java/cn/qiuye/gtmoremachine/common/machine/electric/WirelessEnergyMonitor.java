package cn.qiuye.gtmoremachine.common.machine.electric;

import cn.qiuye.gtmoremachine.api.gui.monitor.Format;
import cn.qiuye.gtmoremachine.api.gui.monitor.PowerStatus;
import cn.qiuye.gtmoremachine.api.gui.monitor.Statistics;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignComponentPanelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.IWirelessMonitor;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessEnergyMonitor extends MetaMachine implements IFancyUIMachine, IWirelessMonitor {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(WirelessEnergyMonitor.class,
            MetaMachine.MANAGED_FIELD_HOLDER);

    public static int p;
    public static BlockPos pPos;

    public WirelessEnergyMonitor(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Getter
    @Setter
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    private List<Component> textListCache;

    @Persisted
    @DropSaved
    private Statistics statistics;
    @Persisted
    @DropSaved
    private Format format;
    @Persisted
    @DropSaved
    private PowerStatus powerStatus;

    //////////////////////////////////////
    // *********** GUI ***********//
    //////////////////////////////////////
    private void handleDisplayClick(String componentData, ClickData clickData) {
        if (componentData.equals("statistics")) {
            if (!clickData.isRemote) {
                statistics = (statistics == Statistics.Team) ? Statistics.Global : Statistics.Team;
            }
        } else if (componentData.equals("format")) {
            if (!clickData.isRemote) {
                format = (format == Format.Unit) ? Format.Science : Format.Unit;
            }
        } else if (componentData.equals("powerStatus")) {
            if (!clickData.isRemote) {
                // 循环切换PowerStatus：All -> In -> Out -> All
                switch (powerStatus) {
                    case All -> powerStatus = PowerStatus.In;
                    case In -> powerStatus = PowerStatus.Out;
                    case Out -> powerStatus = PowerStatus.All;
                }
            }
        } else if (clickData.isRemote) {
            p = 100;
            String[] parts = componentData.split(", ");
            pPos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
    }

    public static int DISPLAY_TEXT_WIDTH = 220;

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, DISPLAY_TEXT_WIDTH + 8 + 8, 117 + 8);

        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, DISPLAY_TEXT_WIDTH + 8, 117).setBackground(GuiTextures.DISPLAY)
                .addWidget(new AlignLabelWidget(DISPLAY_TEXT_WIDTH / 2 + 4, 5, self().getBlockState().getBlock().getDescriptionId()).setTextAlign(AlignLabelWidget.ALIGN_CENTER))
                .addWidget(new AlignComponentPanelWidget(4, 17, this::addDisplayText)
                        .setMaxWidthLimit(DISPLAY_TEXT_WIDTH)
                        .clickHandler(this::handleDisplayClick)
                        .setSplitChar(".")));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    public void addDisplayText(List<Component> textList) {
        if (isRemote()) return;
        if (textListCache == null || getOffsetTimer() % 10 == 0) {
            textListCache = getDisplayText(statistics, format, powerStatus);
        }
        textList.addAll(textListCache);
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getOwnerUUID();
    }

    @Override
    public boolean display() {
        return false;
    }
}
