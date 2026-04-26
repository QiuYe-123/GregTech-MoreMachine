package cn.qiuye.gtmoremachine.common.block;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.GTMMValues;
import cn.qiuye.gtmoremachine.api.annotation.GTMMScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.ICCData;
import cn.qiuye.gtmoremachine.api.machine.multiblock.feature.IECUBlock;
import cn.qiuye.gtmoremachine.config.GTMMConfig;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.IBatteryData;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@GTMMScanned
public class BlockMap {

    /**
     * 分类名 → 该分类下所有方块的映射表。
     *
     * <p>
     * 键为 {@link #registerCategory(String, Block[])} 传入的类别标识符，
     * 值为该类别对应的所有方块（通常按层级升序排列）。
     * </p>
     */
    public static final Object2ObjectOpenHashMap<String, Block[]> MAP = new Object2ObjectOpenHashMap<>(128);
    /**
     * 方块实例 → 所属分类名的反向映射表（基于引用相等）。
     *
     * <p>
     * 由 {@link #registerCategory(String, Block[])} 自动维护，
     * 供 {@link #getCategory(Block)} 查询。
     * </p>
     */
    public static final Reference2ObjectOpenHashMap<Block, String> BLOCK_CATEGORY_MAP = new Reference2ObjectOpenHashMap<>(1024);

    public static final String BLOCKMAP_VALUE_PREFIX = GTMMValues.ADVTER_VALUE_PREFIX + "blockmap";

    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "线圈", en = "Heating Coils")
    private static final String HEATING_COILS = "heating_coils";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "过滤方块", en = "Cleanroom Filters")
    private static final String CLEANROOM_FILTERS = "cleanroom_filters";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "电容", en = "Batteries")
    private static final String BATTERIES = "batteries";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "灯", en = "Lamp")
    private static final String LAMP = "lamp";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "无框灯", en = "Borderless Lamp")
    private static final String BORDERLESS_LAMP = "borlamp";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "消声仓", en = "Muffler Hatch")
    private static final String MUFFLER_HATCH = "muffler_hatch";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "转子支架", en = "Rotor Holder")
    private static final String ROTOR_HATCH = "rotor_hatch";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "能源通讯单元", en = "Energy Communication Unit")
    private static final String ECU = "ecu";
    @GTMMRegisterLanguage(valuePrefix = BLOCKMAP_VALUE_PREFIX, cn = "电网容量组件", en = "Wireless Energy Capacity Component")
    private static final String WECC = "wecc";

    /**
     * 将一个类别名称及其对应的方块数组同时注册到 {@link #MAP} 和 {@link #BLOCK_CATEGORY_MAP}。
     *
     * <p>
     * 此方法在一次遍历中完成正反两个方向的映射构建，避免调用方在 {@code MAP} 填充完毕后
     * 再进行二次遍历来构建反向映射。
     * </p>
     *
     * @param category 类别标识符（如 {@code "heating_coils"}），不可为 {@code null}
     * @param blocks   该类别包含的方块数组，不可为 {@code null}。
     *                 数组内元素建议不含 {@code null}，否则会触发
     *                 {@link #BLOCK_CATEGORY_MAP} 的 null 键异常
     * @throws NullPointerException 如果 {@code category} 或 {@code blocks} 为 {@code null}，
     *                              或 {@code blocks} 数组中含有 {@code null} 元素
     */
    public static void registerCategory(@NotNull String category, @NotNull Block[] blocks) {
        MAP.put(category, blocks);
        for (Block block : blocks) {
            BLOCK_CATEGORY_MAP.put(block, category);
        }
    }

    /**
     * 从 {@code Map.Entry} 集合中，按键的指定 {@code Comparator} 排序后提取值并转换为 {@code Block[]}。
     *
     * <p>
     * 适用于 GTM API 中以 {@code Map<K, Supplier<Block>>} 形式暴露的组件注册表
     * （如 {@code HEATING_COILS}、{@code PSS_BATTERIES} 等）。
     * 在单次流管线中完成排序与转换，避免创建中间 {@code ArrayList}。
     * </p>
     *
     * @param <K>            键类型
     * @param <V>            值类型，必须为 {@code Supplier<? extends Block>} 的子类型
     * @param entries        待处理的 {@code Map.Entry} 集合，不可为 {@code null}
     * @param tierComparator 用于比较键的排序器，不可为 {@code null}。
     *                       需能正确处理 null 键（实际调用方不应传入 null 键）
     * @return 按 {@code tierComparator} 升序排列后提取的 {@code Block} 数组，无重复元素
     * @throws NullPointerException 如果 {@code entries} 或 {@code tierComparator} 为 {@code null}
     */
    public static <K, V extends Supplier<? extends Block>> @NotNull Block[] sortEntries(
                                                                                        @NotNull Collection<? extends Map.Entry<K, V>> entries,
                                                                                        @NotNull Comparator<? super K> tierComparator) {
        /*
         * 不使用 Map.Entry.comparingByKey() 而用显式 lambda 的原因是：
         * JDK 对带有通配符边界的 Entry 类型无法正确进行捕获转换，
         * 会直接导致编译器报类型不兼容错误。
         */
        return entries.stream()
                .sorted((a, b) -> tierComparator.compare(a.getKey(), b.getKey()))
                .map(e -> e.getValue().get())
                .toArray(Block[]::new);
    }

    /**
     * 对已有的 {@link MachineDefinition} 数组滤除空元素、按其 Tier 排序并转为 {@code Block[]}。
     *
     * <p>
     * 适用于 {@link com.gregtechceu.gtceu.common.data.GTMachines GTMachines} 中声明的
     * 全局唯一机器定义数组（如 {@code MUFFLER_HATCH}、{@code ROTOR_HOLDER}）。
     * 由于这些数组本身已在编译期保证元素唯一，故省略 {@code distinct()} 调用。
     * </p>
     *
     * <p>
     * 会过滤掉数组中的 {@code null} 元素，以确保排序安全。
     * </p>
     *
     * @param definitions 机器定义数组，不可为 {@code null}。数组中允许含 {@code null} 元素
     * @return 过滤空值并按 {@code getTier()} 升序排列的 {@code Block} 数组
     * @throws NullPointerException 如果 {@code definitions} 为 {@code null}
     */
    public static @NotNull Block[] filterSort(MachineDefinition[] definitions) {
        return Arrays.stream(definitions)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MachineDefinition::getTier))
                .map(MachineDefinition::get)
                .toArray(Block[]::new);
    }

    /**
     * 构建所有方块分类映射。
     *
     * <p>
     * 此方法应在各注册表（{@link GTCEuAPI}、{@link GTBlocks}、
     * {@link GTMachines}、{@link GTMMAPI} 等）填充完毕后调用，
     * 未被 {@code @GTMMScanned} 注解框架自动调用 —— 后者仅负责扫描
     * 本类中标注 {@link GTMMRegisterLanguage @GTMMRegisterLanguage}
     * 的字段，用于数据生成阶段的语言文件输出。
     * </p>
     *
     * <p>
     * 按顺序注册以下类别：
     * </p>
     * <ol>
     * <li>线圈（Heating Coils） — 来源：{@link GTCEuAPI#HEATING_COILS}</li>
     * <li>过滤机械方块（Cleanroom Filters） — 来源：{@link GTCEuAPI#CLEANROOM_FILTERS}，
     * 使用 {@code CleanroomType.hashCode()} 排序（该接口无可用的层级顺序字段）</li>
     * <li>电容（Batteries） — 来源：{@link GTCEuAPI#PSS_BATTERIES}</li>
     * <li>灯（Lamp） — 来源：{@link GTBlocks#LAMPS}，
     * 来自 Registrate Map，Entry 键为字符串值，不按层级排序</li>
     * <li>无框灯（Borderless Lamp） — 来源：{@link GTBlocks#BORDERLESS_LAMPS}，
     * 同 Lamp，不按层级排序</li>
     * <li>能源通讯单元（ECU） — 来源：{@link GTMMAPI#ECU}</li>
     * <li>消声仓（Muffler Hatch） — 来源：{@link GTMachines#MUFFLER_HATCH}</li>
     * <li>转子支架（Rotor Holder） — 来源：{@link GTMachines#ROTOR_HOLDER}</li>
     * <li>电网容量组件（WECC） — 来源：{@link GTMMAPI#WECC}，
     * 仅在 {@link GTMMConfig#isWirelessCapacitylimitEnable} 为 {@code true} 时注册</li>
     * </ol>
     */
    public static void build() {
        registerCategory(HEATING_COILS, sortEntries(GTCEuAPI.HEATING_COILS.entrySet(), Comparator.comparingInt(ICoilType::getTier)));
        /* CleanroomType 接口内部无可用的排序字段（tier/ordinal），使用 hashCode() 作为确定性排序的折中方案 */
        registerCategory(CLEANROOM_FILTERS, sortEntries(GTCEuAPI.CLEANROOM_FILTERS.entrySet(), Comparator.comparingInt(filter -> filter.getCleanroomType().hashCode())));
        registerCategory(BATTERIES, sortEntries(GTCEuAPI.PSS_BATTERIES.entrySet(), Comparator.comparingInt(IBatteryData::getTier)));
        registerCategory(LAMP, GTBlocks.LAMPS.values().stream().map(RegistryEntry::get).toArray(Block[]::new));
        registerCategory(BORDERLESS_LAMP, GTBlocks.BORDERLESS_LAMPS.values().stream().map(RegistryEntry::get).toArray(Block[]::new));
        registerCategory(ECU, sortEntries(GTMMAPI.ECU.entrySet(), Comparator.comparingInt(IECUBlock::getTier)));
        registerCategory(MUFFLER_HATCH, filterSort(GTMachines.MUFFLER_HATCH));
        registerCategory(ROTOR_HATCH, filterSort(GTMachines.ROTOR_HOLDER));
        if (GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable) {
            registerCategory(WECC, sortEntries(GTMMAPI.WECC.entrySet(), Comparator.comparingInt(ICCData::getTier)));
        }
    }

    /**
     * 查找指定方块所属的类别名称。
     *
     * <p>
     * 基于引用相等进行查找，因此只能查找到已通过
     * {@link #registerCategory(String, Block[])} 注册过的方块。
     * 未注册的方块会返回 {@code null}。
     * </p>
     *
     * @param block 要查找的方块实例，可以为 {@code null}
     * @return 该方块对应的类别标识符（如 {@code "heating_coils"}），
     *         如果方块为 {@code null} 或未注册则返回 {@code null}
     */
    @Nullable
    public static String getCategory(Block block) {
        return BLOCK_CATEGORY_MAP.get(block);
    }
}
