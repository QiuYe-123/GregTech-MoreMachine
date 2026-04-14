package cn.qiuye.gtmoremachine.integration.ae.machine.multiblock.part;

import cn.qiuye.gtmoremachine.api.machine.trait.InaccessibleInfiniteHandler;
import cn.qiuye.gtmoremachine.api.machine.trait.InaccessibleInfiniteTank;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.list.AEListGridWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;

import java.util.EnumSet;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEOutputPartMachine extends ItemBusPartMachine implements IGridConnectedMachine {

    @SyncToClient
    @Getter
    protected boolean isOnline;
    @SaveField
    protected final GridNodeHolder nodeHolder;
    protected final IActionSource actionSource;
    @Getter
    protected Object2LongOpenHashMap<AEKey> returnBuffer = new Object2LongOpenHashMap<>();
    @SaveField
    private KeyStorage internalBuffer;
    @SaveField
    private KeyStorage internalTankBuffer;
    @SaveField
    public final NotifiableFluidTank fluidtank;

    public MEOutputPartMachine(BlockEntityCreationInfo holder) {
        super(holder, GTValues.LuV, IO.OUT);
        this.fluidtank = this.attachTrait(createTank());
        this.nodeHolder = this.attachTrait(new GridNodeHolder(this));
        this.actionSource = IActionSource.ofMachine(nodeHolder.getMainNode()::getNode);
    }

    @Override
    public void setOnline(boolean online) {
        isOnline = online;
        syncDataHolder.markClientSyncFieldDirty("isOnline");
    }

    @Override
    public IManagedGridNode getMainNode() {
        return nodeHolder.getMainNode();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        IGridConnectedMachine.super.onMainNodeStateChanged(reason);
        this.updateInventorySubscription();
    }

    @Override
    protected void updateInventorySubscription() {
        if (shouldSubscribe()) {
            autoIOSubs = subscribeServerTick(autoIOSubs, this::autoIO);
        } else if (autoIOSubs != null) {
            autoIOSubs.unsubscribe();
            autoIOSubs = null;
        }
    }

    /////////////////////////////////
    // ***** Machine LifeCycle ****//
    /////////////////////////////////

    @Override
    protected NotifiableItemStackHandler createInventory() {
        this.internalBuffer = new KeyStorage();
        return new InaccessibleInfiniteHandler(internalBuffer);
    }

    protected NotifiableFluidTank createTank() {
        this.internalTankBuffer = new KeyStorage();
        return new InaccessibleInfiniteTank(internalTankBuffer);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        isRemote();
    }

    @Override
    public void onMachineDestroyed() {
        var grid = getMainNode().getGrid();
        if (grid != null) {
            if (!internalBuffer.isEmpty()) {
                for (var entry : internalBuffer) {
                    grid.getStorageService().getInventory().insert(entry.getKey(), entry.getLongValue(),
                            Actionable.MODULATE, actionSource);
                }
            }
            if (!internalTankBuffer.isEmpty()) {
                for (var entry : internalTankBuffer) {
                    grid.getStorageService().getInventory().insert(entry.getKey(), entry.getLongValue(),
                            Actionable.MODULATE, actionSource);
                }
            }
        }
    }

    /////////////////////////////////
    // ********** Sync ME *********//
    /////////////////////////////////

    protected boolean shouldSubscribe() {
        return isWorkingEnabled() && isOnline() && (!internalBuffer.storage.isEmpty() || !internalTankBuffer.storage.isEmpty());
    }

    @Override
    protected void autoIO() {
        if (!this.shouldSyncME()) return;
        if (this.updateMEStatus()) {
            var grid = getMainNode().getGrid();
            if (grid != null) {
                if (!internalBuffer.isEmpty()) {
                    internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
                }
                if (!internalTankBuffer.isEmpty()) {
                    internalTankBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
                }
            }
            this.updateInventorySubscription();
        }
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        getMainNode().setExposedOnSides(EnumSet.of(newFacing));
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(new Position(0, 0));
        // ME Network status
        group.addWidget(new LabelWidget(0, 0, () -> this.isOnline ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));

        group.addWidget(new AEListGridWidget.Item(5, 20, 3, this.internalBuffer));
        group.addWidget(new AEListGridWidget.Fluid(5, 80, 3, this.internalTankBuffer));
        return group;
    }
}
