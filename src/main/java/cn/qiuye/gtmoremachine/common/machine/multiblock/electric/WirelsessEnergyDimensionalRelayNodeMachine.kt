package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait

import com.lowdragmc.lowdraglib.gui.modular.ModularUI
import com.lowdragmc.lowdraglib.gui.util.ClickData
import com.lowdragmc.lowdraglib.gui.widget.*
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

open class WirelsessEnergyDimensionalRelayNodeMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine {

    companion object {
        protected val MANAGEMENT_FACTORY: ManagedFieldHolder = ManagedFieldHolder(
            WirelsessEnergyDimensionalRelayNodeMachine::class.java,
            MANAGED_FIELD_HOLDER,
        )
    }

    protected var tickSubscription: ConditionalSubscriptionHandler? = null

    override fun onStructureFormed() {
        super.onStructureFormed()
    }

    override fun onStructureInvalid() {
        super.onStructureInvalid()
    }

    override fun getFieldHolder(): ManagedFieldHolder = MANAGEMENT_FACTORY

    override fun isRemote(): Boolean = super<WorkableMultiblockMachine>.isRemote

    override fun createUIWidget(): Widget {
        val group = WidgetGroup(0, 0, 182 + 8, 117 + 8)
        group.addWidget(
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
        group.setBackground(GuiTextures.BACKGROUND_INVERSE)
        return group
    }

    override fun createUI(entityPlayer: Player?): ModularUI =
        ModularUI(198, 208, this, entityPlayer).widget(FancyMachineUIWidget(this, 198, 208))

    open class DimensionalRelayNodeBank(machine: MetaMachine, batteries: MutableList<IBatteryData?>?) :
        MachineTrait(machine) {

        override fun getFieldHolder(): ManagedFieldHolder = MANAGED_FIELD_HOLDER
        companion object {
            protected val MANAGED_FIELD_HOLDER: ManagedFieldHolder = ManagedFieldHolder(
                DimensionalRelayNodeBank::class.java,
            )
        }
    }
}
