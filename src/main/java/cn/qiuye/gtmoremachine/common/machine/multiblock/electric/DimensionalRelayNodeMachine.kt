package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer
import cn.qiuye.gtmoremachine.utils.TeamUtils.getName

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.MetaMachine
import com.gregtechceu.gtceu.api.machine.TickableSubscription
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic
import com.gregtechceu.gtceu.common.data.GTItems

import com.lowdragmc.lowdraglib.gui.modular.ModularUI
import com.lowdragmc.lowdraglib.gui.util.ClickData
import com.lowdragmc.lowdraglib.gui.widget.*
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

import java.math.BigInteger
import java.util.*

open class DimensionalRelayNodeMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine,
    IWirelessEnergyContainerHolder {

    companion object {
        const val CAPACITY_COMPONENT_HEADER = "DRNComponent_"
        protected val MANAGEMENT_FACTORY: ManagedFieldHolder = ManagedFieldHolder(
            DimensionalRelayNodeMachine::class.java,
            MANAGED_FIELD_HOLDER,
        )
    }
    private var capacityBank: DimensionalRelayNodeBank? = null
    private var wirelessEnergyContainerCache: WirelessEnergyContainer? = null
    private var updContainer: TickableSubscription? = null

    val totalCapacity: BigInteger
        get() = capacityBank?.totalCapacity ?: BigInteger.ZERO

    val totalPassiveDrain: BigInteger
        get() = capacityBank?.totalPassiveDrain ?: BigInteger.ZERO

    init {
        this.capacityBank = DimensionalRelayNodeBank(this, mutableListOf())
    }

    // ================= 无线电网 =================

    override fun getUUID(): UUID? = ownerUUID

    override fun display(): Boolean = false

    override fun Capacity(): Boolean = true

    override fun setWirelessEnergyContainerCache(container: WirelessEnergyContainer?) {
        wirelessEnergyContainerCache = container
    }

    override fun getWirelessEnergyContainerCache(): WirelessEnergyContainer? = wirelessEnergyContainerCache

    private fun tick() {
        if (this.uuid == null) return
    }

    override fun onUse(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        if (isRemote) return InteractionResult.PASS
        val item = player.getItemInHand(hand)
        if (item.isEmpty) return InteractionResult.PASS
        if (item.`is`(GTItems.TOOL_DATA_STICK.asItem())) {
            ownerUUID = player.uuid
            wirelessEnergyContainerCache = null
            player.sendSystemMessage(
                Component.translatable(
                    "gtmoremachine.machine.wireless_energy_hatch.tooltip.bind",
                    getName(player),
                ),
            )
            return InteractionResult.SUCCESS
        }
        return InteractionResult.PASS
    }

    override fun onLeftClick(
        player: Player,
        world: Level,
        hand: InteractionHand,
        pos: BlockPos,
        direction: Direction,
    ): Boolean {
        if (isRemote) return false
        val item = player.getItemInHand(hand)
        if (item.isEmpty) return false
        if (item.`is`(GTItems.TOOL_DATA_STICK.asItem())) {
            ownerUUID = null
            wirelessEnergyContainerCache = null
            player.sendSystemMessage(
                Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind"),
            )
            return true
        }
        return false
    }

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
        if (this.capacityBank == null) {
            this.capacityBank = DimensionalRelayNodeBank(this, components)
        } else {
            this.capacityBank = capacityBank!!.rebuild(components)
        }
        val container = getWirelessEnergyContainer() ?: return
        container.setCapacity(this.totalCapacity, true, this)
    }

    override fun onStructureInvalid() {
        capacityBank = null
        val container = getWirelessEnergyContainer() ?: return
        container.setCapacity(BigInteger.ZERO, false, this)
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

        fun rebuild(component: MutableList<ICapacityComponentData>): DimensionalRelayNodeBank {
            if (component.isEmpty()) {
                throw IllegalArgumentException("Cannot rebuild bank with no batteries!")
            }
            return DimensionalRelayNodeBank(machine, component)
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
