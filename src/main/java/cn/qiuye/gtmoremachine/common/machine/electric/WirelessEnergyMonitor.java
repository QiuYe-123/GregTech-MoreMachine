package cn.qiuye.gtmoremachine.common.machine.electric;

import cn.qiuye.gtmoremachine.api.gui.monitor.*;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignComponentPanelWidget;
import cn.qiuye.gtmoremachine.api.gui.widget.AlignLabelWidget;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.feature.IWirelessMonitor;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessEnergyMonitor extends MetaMachine implements IFancyUIMachine, IWirelessMonitor {

    public static int p;
    public static BlockPos pPos;

    public WirelessEnergyMonitor(BlockEntityCreationInfo holder) {
        super(holder);
        this.statistics = Statistics.Companion.getDefaultValue();
        this.format = Format.Companion.getDefaultValue();
        this.powerStatus = Status.Companion.getDefaultValue();
        this.sortingrules = Sorting.Companion.getDefaultValue();
        this.type = Type.Companion.getDefaultValue();
    }

    @Getter
    @Setter
    private WirelessEnergyContainer WirelessEnergyContainerCache;

    private List<Component> textListCache;

    @SaveField
    private Statistics statistics;
    @SaveField
    private Format format;
    @SaveField
    private Status powerStatus;
    @SaveField
    private Sorting sortingrules;
    @SaveField
    private Type type;

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
                    case All -> powerStatus = Status.In;
                    case In -> powerStatus = Status.Out;
                    case Out -> powerStatus = Status.All;
                }
            }
        } else if (componentData.equals("sortingrules")) {
            if (!clickData.isRemote) {
                sortingrules = (sortingrules == Sorting.Ascending) ? Sorting.Descendingorder : Sorting.Ascending;
            }
        } else if (componentData.equals("type")) {
            if (!clickData.isRemote) {
                switch (type) {
                    case PowerInteraction -> type = Type.Capacitycomponent;
                    case Capacitycomponent -> type = Type.RelayNode;
                    case RelayNode -> type = Type.PowerInteraction;
                }
            }
        } else if (clickData.isRemote) {
            p = 200;
            String[] parts = componentData.split(", ");
            pPos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
    }

    public static int DISPLAY_TEXT_WIDTH = 220;

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, DISPLAY_TEXT_WIDTH + 8 + 8, 127 + 8);

        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, DISPLAY_TEXT_WIDTH + 8, 127).setBackground(GuiTextures.DISPLAY)
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
            textListCache = getDisplayText(statistics, format, powerStatus, sortingrules, type);
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

    @Override
    public Level getMonitorLevel() {
        return this.getLevel();
    }
}
