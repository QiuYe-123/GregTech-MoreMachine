package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder
import cn.qiuye.gtmoremachine.api.machine.multiblock.IEnergyCommunicationUnitBlock
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic

import com.lowdragmc.lowdraglib.gui.modular.ModularUI
import com.lowdragmc.lowdraglib.gui.util.ClickData
import com.lowdragmc.lowdraglib.gui.widget.*
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

import java.util.*

open class DimensionalRelayNodeMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine,
    IWirelessEnergyContainerHolder {

    companion object {
        protected val MANAGEMENT_FACTORY: ManagedFieldHolder = ManagedFieldHolder(
            DimensionalRelayNodeMachine::class.java,
            MANAGED_FIELD_HOLDER,
        )
    }
    private var tickSubscription: ConditionalSubscriptionHandler

    private var wirelessEnergyContainerCache: WirelessEnergyContainer? = null

    @Persisted
    private var currentTier: Int = -1 // 当前检测到的等级，-1表示未检测或无效

    init {
        this.tickSubscription = ConditionalSubscriptionHandler(this, this::updateMachineStatus) { this.isFormed }
    }

    // ================= 无线电网 =================

    override fun getUUID(): UUID? = ownerUUID

    override fun display(): Boolean = false

    override fun Dimensional(): Boolean = true

    override fun setWirelessEnergyContainerCache(container: WirelessEnergyContainer?) {
        wirelessEnergyContainerCache = container
    }

    override fun getWirelessEnergyContainerCache(): WirelessEnergyContainer? = wirelessEnergyContainerCache

    override fun getFieldHolder(): ManagedFieldHolder = MANAGEMENT_FACTORY

    override fun isRemote(): Boolean = super<WorkableMultiblockMachine>.isRemote

    override fun onStructureFormed() {
        super.onStructureFormed()
        val ecuType = getMultiblockState().matchContext.get<IEnergyCommunicationUnitBlock>("ECUType")
        this.currentTier = ecuType.tier
        tickSubscription.updateSubscription()
        val container = getWirelessEnergyContainer()
        container?.setDimensional(this.currentTier, true, this)
    }

    override fun onStructureInvalid() {
        tickSubscription.updateSubscription()
        val container = getWirelessEnergyContainer()
        container?.setDimensional(0, false, this)
        this.currentTier = -1
        super.onStructureInvalid()
    }

    protected fun updateMachineStatus() {
        level?.isClientSide?.let {
            if (!it) {
                if (isWorkingEnabled && isFormed) {
                    recipeLogic.status = RecipeLogic.Status.WORKING
                }
            }
        }
    }

    override fun createUIWidget(): Widget = WidgetGroup(0, 0, 182 + 8, 117 + 8)
        .addWidget(
            DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(screenTexture)
                .addWidget(LabelWidget(4, 5, self().blockState.block.descriptionId))
                .addWidget(
                    ComponentPanelWidget(
                        4,
                        17,
                    ) { textList: MutableList<Component> -> this.addDisplayText(textList) }
                        .setMaxWidthLimit(150)
                        .clickHandler { componentData: String?, clickData: ClickData? ->
                            this.handleDisplayClick(
                                componentData,
                                clickData,
                            )
                        },
                ),
        )
        .setBackground(GuiTextures.BACKGROUND_INVERSE)

    override fun createUI(entityPlayer: Player?): ModularUI =
        ModularUI(198, 208, this, entityPlayer).widget(FancyMachineUIWidget(this, 198, 208))
}
