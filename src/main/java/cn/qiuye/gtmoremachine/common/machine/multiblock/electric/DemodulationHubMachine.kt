package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer
import cn.qiuye.gtmoremachine.utils.TeamUtils.getName

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.MetaMachine
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

open class DemodulationHubMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine,
    IWirelessEnergyContainerHolder {

    companion object {
        const val CAPACITY_COMPONENT_HEADER = "DRNComponent_"
        protected val MANAGEMENT_FACTORY: ManagedFieldHolder = ManagedFieldHolder(
            DemodulationHubMachine::class.java,
            MANAGED_FIELD_HOLDER,
        )
    }

    private var capacityBank: DimensionalRelayNodeBank? = null
    private var wirelessEnergyContainerCache: WirelessEnergyContainer? = null
    private var tickSubscription: ConditionalSubscriptionHandler

    val totalCapacity: BigInteger
        get() = capacityBank?.totalCapacity ?: BigInteger.ZERO

    val totalPassiveDrain: BigInteger
        get() = capacityBank?.totalPassiveDrain ?: BigInteger.ZERO

    init {
        this.capacityBank = DimensionalRelayNodeBank(this, mutableListOf())
        this.tickSubscription = ConditionalSubscriptionHandler(this, this::updateMachineStatus) { this.isFormed }
    }

    // ================= 无线电网 =================

    /**
     * 获取机器的所有者UUID
     * @return 所有者UUID，可能为null
     */
    override fun getUUID(): UUID? = ownerUUID

    /**
     * 检查是否能源仓
     * @return 当前返回false表示不是
     */
    override fun display(): Boolean = false

    /**
     * 检查是否是容量机器
     * @return 返回true表示是容量机器
     */
    override fun Capacity(): Boolean = true

    /**
     * 设置无线能量容器缓存
     * @param container 无线能量容器，可为null
     */
    override fun setWirelessEnergyContainerCache(container: WirelessEnergyContainer?) {
        wirelessEnergyContainerCache = container
    }

    /**
     * 获取无线能量容器缓存
     * @return 缓存的无线能量容器，可能为null
     */
    override fun getWirelessEnergyContainerCache(): WirelessEnergyContainer? = wirelessEnergyContainerCache

    /**
     * 当玩家使用物品右键点击机器时的处理
     * @param state 方块状态
     * @param world 世界
     * @param pos 位置
     * @param player 玩家
     * @param hand 交互手
     * @param hit 点击结果
     * @return 交互结果
     */
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
            val container = getWirelessEnergyContainer()
            container?.setCapacity(this.totalCapacity, true, this)
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

    /**
     * 当玩家左键点击机器时的处理
     * @param player 玩家
     * @param world 世界
     * @param hand 交互手
     * @param pos 位置
     * @param direction 方向
     * @return 是否处理了点击
     */
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
            wirelessEnergyContainerCache = null
            val container = getWirelessEnergyContainer()
            container?.setCapacity(BigInteger.ZERO, false, this)
            player.sendSystemMessage(
                Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind"),
            )
            return true
        }
        return false
    }

    /**
     * 当多方块结构形成时的处理
     */
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
        tickSubscription.updateSubscription()
        val container = getWirelessEnergyContainer()
        container?.setCapacity(this.totalCapacity, true, this)
    }

    /**
     * 当多方块结构失效时的处理
     */
    override fun onStructureInvalid() {
        capacityBank = null
        tickSubscription.updateSubscription()
        val container = getWirelessEnergyContainer()
        container?.setCapacity(BigInteger.ZERO, false, this)
        super.onStructureInvalid()
    }

    /**
     * 更新机器状态
     */
    protected fun updateMachineStatus() {
        level?.isClientSide?.let {
            if (!it) {
                if (isWorkingEnabled && isFormed) {
                    recipeLogic.status = RecipeLogic.Status.WORKING
                }
            }
        }
    }

    /**
     * 获取管理字段持有器
     * @return 管理字段持有器
     */
    override fun getFieldHolder(): ManagedFieldHolder = MANAGEMENT_FACTORY

    /**
     * 检查是否为远程端（客户端）
     * @return 如果是客户端返回true，服务器端返回false
     */
    override fun isRemote(): Boolean = super<WorkableMultiblockMachine>.isRemote

    /**
     * 创建UI部件
     * @return 配置好的UI部件组
     */
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

    // ================= 容量内部类 =================

    override fun createUI(entityPlayer: Player?): ModularUI =
        ModularUI(198, 208, this, entityPlayer).widget(FancyMachineUIWidget(this, 198, 208))

    // ================= 容量银行内部类 =================

    open class DimensionalRelayNodeBank(machine: MetaMachine, components: MutableList<ICapacityComponentData>) :
        MachineTrait(machine) {

        /**
         * 获取管理字段持有器
         * @return 管理字段持有器
         */
        override fun getFieldHolder(): ManagedFieldHolder = MANAGED_FIELD_HOLDER

        /**
         * 计算总容量
         */
        val totalCapacity: BigInteger = components.fold(BigInteger.ZERO) { acc, component ->
            acc.add(component.capacity)
        }

        /**
         * 计算总被动损耗
         */
        val totalPassiveDrain: BigInteger = components.fold(BigInteger.ZERO) { acc, component ->
            acc.add(component.lossEnergy)
        }

        /**
         * 重新构建容量银行
         * @param component 新的组件列表
         * @return 新的容量银行实例
         * @throws IllegalArgumentException 如果组件列表为空
         */
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

        /**
         * 增加组件数量
         * @return 当前实例（用于链式调用）
         */
        fun increment(): ComponentMatchWrapper {
            amount++
            return this
        }
    }
}
