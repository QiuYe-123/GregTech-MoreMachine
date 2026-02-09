package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder
import cn.qiuye.gtmoremachine.api.machine.multiblock.ICapacityComponentData
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer
import cn.qiuye.gtmoremachine.utils.NumberUtils
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

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

// ================= 主类定义 =================
/**
 * 解调枢纽机器类
 * 继承自WorkableMultiblockMachine，实现多方块结构功能
 * 同时支持无线能源容器、花式UI和显示UI功能
 */
open class DemodulationHubMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine,
    IWirelessEnergyContainerHolder {

    // ================= 伴生对象 =================
    companion object {
        /** 容量组件标识前缀 */
        const val CAPACITY_COMPONENT_HEADER = "DRNComponent_"

        /** 管理字段工厂 */
        protected val MANAGED_FIELD_HOLDER: ManagedFieldHolder = ManagedFieldHolder(
            DemodulationHubMachine::class.java,
            WorkableMultiblockMachine.MANAGED_FIELD_HOLDER,
        )
    }

    // ================= 属性声明 =================
    /** 容量实例，存储多方块结构的容量组件数据 */
    private var capacityBank: DimensionalRelayNodeBank? = null

    /** 无线能源容器缓存 */
    private var wirelessEnergyContainerCache: WirelessEnergyContainer? = null

    /** 定时订阅处理器，用于定期更新机器状态 */
    private var tickSubscription: ConditionalSubscriptionHandler

    // ================= 计算属性 =================
    /** 获取总容量（只读属性） */
    val totalCapacity: BigInteger
        get() = capacityBank?.totalCapacity ?: BigInteger.ZERO

    /** 获取总被动损耗（只读属性） */
    val totalPassiveDrain: BigInteger
        get() = capacityBank?.totalPassiveDrain ?: BigInteger.ZERO

    // ================= 初始化块 =================
    init {
        // 初始化容量和定时订阅处理器
        this.capacityBank = DimensionalRelayNodeBank(this, mutableListOf())
        this.tickSubscription = ConditionalSubscriptionHandler(this, this::updateMachineStatus) { this.isFormed }
    }

    // ================= 无线电网接口实现 =================
    /**
     * 获取机器的所有者UUID
     * @return 所有者UUID，可能为null
     */
    override fun getUUID(): UUID? = ownerUUID

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

    // ================= 玩家交互方法 =================
    /**
     * 当玩家使用物品右键点击机器时的处理
     * 主要用于数据棒绑定操作
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
        // 检查是否为数据棒
        if (item.`is`(GTItems.TOOL_DATA_STICK.asItem())) {
            ownerUUID = player.uuid
            setWirelessEnergyContainerCache(null)
            val container = getWirelessEnergyContainer()
            container?.setCapacity(this.totalCapacity, this.totalPassiveDrain, true, this)
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
     * 主要用于数据棒解绑操作
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
        // 检查是否为数据棒
        if (item.`is`(GTItems.TOOL_DATA_STICK.asItem())) {
            ownerUUID = null
            wirelessEnergyContainerCache = null
            val container = getWirelessEnergyContainer()
            container?.setCapacity(BigInteger.ZERO, BigInteger.ZERO, false, this)
            player.sendSystemMessage(
                Component.translatable("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind"),
            )
            return true
        }
        return false
    }

    // ================= 多方块结构生命周期方法 =================
    /**
     * 当多方块结构形成时的处理
     * 收集所有容量组件并重建容量
     */
    override fun onStructureFormed() {
        super.onStructureFormed()

        val components = mutableListOf<ICapacityComponentData>()
        val matchContext = multiblockState.matchContext.entrySet()
        // 遍历匹配上下文，收集容量组件
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
        // 检查是否有组件
        if (components.isEmpty()) {
            onStructureInvalid()
            return
        }
        // 重建或新建容量
        if (this.capacityBank == null) {
            this.capacityBank = DimensionalRelayNodeBank(this, components)
        } else {
            this.capacityBank = capacityBank!!.rebuild(components)
        }
        tickSubscription.updateSubscription()
        // 更新无线能源容器容量
        val container = getWirelessEnergyContainer()
        container?.setCapacity(this.totalCapacity, this.totalPassiveDrain, true, this)
    }

    /**
     * 当多方块结构失效时的处理
     * 清空容量并重置无线能源容器
     */
    override fun onStructureInvalid() {
        capacityBank = null
        tickSubscription.updateSubscription()
        val container = getWirelessEnergyContainer()
        container?.setCapacity(BigInteger.ZERO, BigInteger.ZERO, false, this)
        super.onStructureInvalid()
    }

    // ================= 机器状态管理 =================
    /**
     * 更新机器状态
     * 仅在服务器端且结构完整时更新工作状态
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

    // ================= 继承方法实现 =================
    /**
     * 获取管理字段持有器
     * @return 管理字段持有器
     */
    override fun getFieldHolder(): ManagedFieldHolder = MANAGED_FIELD_HOLDER

    /**
     * 检查是否为远程端（客户端）
     * @return 如果是客户端返回true，服务器端返回false
     */
    override fun isRemote(): Boolean = super<WorkableMultiblockMachine>.isRemote

    // ================= UI相关方法 =================
    /**
     * 创建UI部件（IDisplayUIMachine接口实现）
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

    /**
     * 创建花式UI（IFancyUIMachine接口实现）
     */
    override fun createUI(entityPlayer: Player?): ModularUI =
        ModularUI(198, 208, this, entityPlayer).widget(FancyMachineUIWidget(this, 198, 208))

    override fun addDisplayText(textList: MutableList<Component>) {
        super.addDisplayText(textList)
        if (isFormed()) {
            if (this.totalCapacity >= BigInteger.ZERO) {
                textList.add(Component.literal(NumberUtils.formatBigIntegerNumberOrSic(this.totalCapacity)))
            }
            if (this.totalPassiveDrain >= BigInteger.ZERO) {
                textList.add(Component.literal(NumberUtils.formatBigIntegerNumberOrSic(this.totalPassiveDrain)))
            }
            val container = getWirelessEnergyContainer()
            if (container != null) {
                val storagepercentage = container.getStoragePercentage()
                val percentage = storagepercentage.storagePercentage()
                if (percentage >= BigDecimal.ZERO) {
                    textList.add(Component.literal(percentage.toString()))
                }
            }
        } else {
            val tooltip: Component = Component.translatable("gtceu.multiblock.invalid_structure.tooltip")
                .withStyle(ChatFormatting.GRAY)
            textList.add(
                Component.translatable("gtceu.multiblock.invalid_structure")
                    .withStyle(
                        Style.EMPTY.withColor(ChatFormatting.RED)
                            .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)),
                    ),
            )
        }
        definition.additionalDisplay.accept(this, textList)
    }

    override fun isWorkingEnabled(): Boolean = true

    override fun setWorkingEnabled(ignored: Boolean) {}
    // ================= 内部类：容量 =================
    /**
     * 维度中继节点银行
     * 负责管理多方块结构中的容量组件数据
     */
    open class DimensionalRelayNodeBank(machine: MetaMachine, components: MutableList<ICapacityComponentData>) :
        MachineTrait(machine) {

        /**
         * 获取管理字段持有器
         * @return 管理字段持有器
         */
        override fun getFieldHolder(): ManagedFieldHolder = MANAGED_FIELD_HOLDER

        /**
         * 计算总容量（惰性计算）
         */
        val totalCapacity: BigInteger = components.fold(BigInteger.ZERO) { acc, component ->
            acc.add(component.getCapacity())
        }

        /**
         * 计算总被动损耗（惰性计算）
         */
        val totalPassiveDrain: BigInteger = components.fold(BigInteger.ZERO) { acc, component ->
            acc.add(component.getLossEnergy())
        }

        /**
         * 重新构建容量
         * @param component 新的组件列表
         * @return 新的容量实例
         * @throws IllegalArgumentException 如果组件列表为空
         */
        fun rebuild(component: MutableList<ICapacityComponentData>): DimensionalRelayNodeBank {
            if (component.isEmpty()) {
                throw IllegalArgumentException("Cannot rebuild bank with no batteries!")
            }
            return DimensionalRelayNodeBank(machine, component)
        }

        companion object {
            /** 管理字段持有器 */
            protected val MANAGED_FIELD_HOLDER: ManagedFieldHolder = ManagedFieldHolder(
                DimensionalRelayNodeBank::class.java,
            )
        }
    }

    // ================= 内部类：组件匹配包装器 =================
    /**
     * 组件匹配包装器
     * 用于在多方块结构匹配过程中存储组件数据及其数量
     */
    class ComponentMatchWrapper(val componentData: ICapacityComponentData) {
        /** 组件数量 */
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
