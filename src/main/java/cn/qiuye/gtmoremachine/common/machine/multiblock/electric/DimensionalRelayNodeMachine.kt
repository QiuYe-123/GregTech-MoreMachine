package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic

import com.lowdragmc.lowdraglib.gui.modular.ModularUI
import com.lowdragmc.lowdraglib.gui.util.ClickData
import com.lowdragmc.lowdraglib.gui.widget.*
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

import java.math.BigInteger

open class DimensionalRelayNodeMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine {

    companion object {
        const val CAPACITY_COMPONENT_HEADER = "DRNComponent_"
        protected val MANAGEMENT_FACTORY: ManagedFieldHolder = ManagedFieldHolder(
            DimensionalRelayNodeMachine::class.java,
            MANAGED_FIELD_HOLDER,
        )
    }
    private var capacityBank: DimensionalRelayNodeBank? = null

    val totalCapacity: BigInteger
        get() = capacityBank?.totalCapacity ?: BigInteger.ZERO

    val totalPassiveDrain: BigInteger
        get() = capacityBank?.totalPassiveDrain ?: BigInteger.ZERO

    override fun onStructureFormed() {
        super.onStructureFormed()

        val components = mutableListOf<ICapacityComponentData>()
        val matchContext = multiblockState.matchContext.entrySet()
        for (battery in matchContext) {
            if (battery.key.startsWith(CAPACITY_COMPONENT_HEADER) &&
                battery.value is ComponentMatchWrapper
            ) {
                val wrapper = battery.value as ComponentMatchWrapper
                for (i in 0..<wrapper.amount) {
                    components.add(wrapper.componentData)
                }
            }
        }
        if (components.isEmpty()) {
            onStructureInvalid()
            return
        }
        this.capacityBank = DimensionalRelayNodeBank(this, components)
    }

    override fun onStructureInvalid() {
        capacityBank = null
        super.onStructureInvalid()
    }

    protected fun updateMachineStatus() {
        level?.isClientSide?.let {
            if (!it) {
                // 更新机器状态（仅用于渲染）
                if (isWorkingEnabled && isFormed) {
                    recipeLogic.status = RecipeLogic.Status.WORKING
                }
            }
        }
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

    // ================= 容量银行内部类 =================

    open class DimensionalRelayNodeBank(machine: MetaMachine, components: MutableList<ICapacityComponentData>) :
        MachineTrait(machine) {

        override fun getFieldHolder(): ManagedFieldHolder = MANAGED_FIELD_HOLDER

        val totalCapacity: BigInteger = components.fold(BigInteger.ZERO) { acc, component ->
            acc.add(component.capacity)
        }

        val totalPassiveDrain: BigInteger = components.fold(BigInteger.ZERO) { acc, component ->
            acc.add(component.lossEnergy)
        }

        companion object {
            protected val MANAGED_FIELD_HOLDER: ManagedFieldHolder = ManagedFieldHolder(
                DimensionalRelayNodeBank::class.java,
            )
        }
    }

    class ComponentMatchWrapper(val componentData: ICapacityComponentData) {
        var amount: Int = 1

        fun increment(): ComponentMatchWrapper {
            amount++
            return this
        }
    }
}
