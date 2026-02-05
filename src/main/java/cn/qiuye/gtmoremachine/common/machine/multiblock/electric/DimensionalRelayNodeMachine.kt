package cn.qiuye.gtmoremachine.common.machine.multiblock.electric

import cn.qiuye.gtmoremachine.api.machine.IWirelessEnergyContainerHolder
import cn.qiuye.gtmoremachine.api.machine.multiblock.IEnergyCommunicationUnitBlock
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer
import cn.qiuye.gtmoremachine.utils.TeamUtils.getName

import com.gregtechceu.gtceu.api.gui.GuiTextures
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic
import com.gregtechceu.gtceu.common.data.GTItems

import com.lowdragmc.lowdraglib.gui.modular.ModularUI
import com.lowdragmc.lowdraglib.gui.util.ClickData
import com.lowdragmc.lowdraglib.gui.widget.*
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted
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

import java.util.*

// ================= 主类定义 =================
/**
 * 维度中继节点机器类
 * 继承自WorkableMultiblockMachine，支持维度传输功能
 * 实现无线能源容器、花式UI和显示UI功能
 */
open class DimensionalRelayNodeMachine(holder: IMachineBlockEntity) :
    WorkableMultiblockMachine(holder),
    IFancyUIMachine,
    IDisplayUIMachine,
    IWirelessEnergyContainerHolder {

    // ================= 伴生对象 =================
    companion object {
        /** 管理字段工厂 */
        protected val MANAGED_FIELD_HOLDER: ManagedFieldHolder = ManagedFieldHolder(
            DimensionalRelayNodeMachine::class.java,
            WorkableMultiblockMachine.MANAGED_FIELD_HOLDER,
        )
    }

    // ================= 属性声明 =================
    /** 定时订阅处理器，用于定期更新机器状态 */
    private var tickSubscription: ConditionalSubscriptionHandler

    /** 无线能源容器缓存 */
    private var wirelessEnergyContainerCache: WirelessEnergyContainer? = null

    /** 当前检测到的等级，-1表示未检测或无效（已持久化） */
    @Persisted
    private var currentTier: Int = -1

    // ================= 初始化块 =================
    init {
        // 初始化定时订阅处理器
        this.tickSubscription = ConditionalSubscriptionHandler(this, this::updateMachineStatus) { this.isFormed }
    }

    // ================= 无线电网接口实现 =================
    /**
     * 获取机器的所有者UUID
     * @return 所有者UUID，可能为null
     */
    override fun getUUID(): UUID? = ownerUUID

    /**
     * 检查是否是维度机器
     * @return 返回true表示是维度机器
     */
    override fun Dimensional(): Boolean = true

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

    // ================= 管理字段方法 =================
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
    ): InteractionResult? {
        if (isRemote) return InteractionResult.PASS
        val item = player.getItemInHand(hand)
        if (item.isEmpty) return InteractionResult.PASS
        // 检查是否为数据棒
        if (item.`is`(GTItems.TOOL_DATA_STICK.asItem())) {
            ownerUUID = player.uuid
            wirelessEnergyContainerCache = null
            val container = getWirelessEnergyContainer()
            container?.setDimensional(this.currentTier, true, this)
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
            container?.setDimensional(0, false, this)
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
     * 从匹配上下文中获取ECU（能量通信单元）类型，并设置当前等级
     */
    override fun onStructureFormed() {
        super.onStructureFormed()
        // 从匹配上下文中获取ECU类型
        val ecuType = getMultiblockState().matchContext.get<IEnergyCommunicationUnitBlock>("ECUType")
        this.currentTier = ecuType.tier
        tickSubscription.updateSubscription()
        // 更新无线能源容器维度等级
        val container = getWirelessEnergyContainer()
        container?.setDimensional(this.currentTier, true, this)
    }

    /**
     * 当多方块结构失效时的处理
     * 重置维度等级并更新无线能源容器
     */
    override fun onStructureInvalid() {
        tickSubscription.updateSubscription()
        val container = getWirelessEnergyContainer()
        container?.setDimensional(0, false, this)
        this.currentTier = -1
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
            if (this.currentTier > 0) {}
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
}
