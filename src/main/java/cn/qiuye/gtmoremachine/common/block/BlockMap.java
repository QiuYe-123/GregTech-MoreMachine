package cn.qiuye.gtmoremachine.common.block;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.annotation.GTMMScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.common.item.AdvancedTerminalBehavior;
import cn.qiuye.gtmoremachine.config.GTMMConfig;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@GTMMScanned
public class BlockMap {

    public static final Object2ObjectOpenHashMap<String, Block[]> MAP = new Object2ObjectOpenHashMap<>(50);
    public static final Reference2ObjectOpenHashMap<Block, String> BLOCK_CATEGORY_MAP = new Reference2ObjectOpenHashMap<>();

    public static Block[] LAMP;
    public static Block[] BORLAMP;

    public static final String blockmap_valuePrefix = AdvancedTerminalBehavior.valuePrefix + "blockmap";

    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "线圈", en = "Heating Coils")
    public static final String heating_coils = "heating_coils";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "过滤方块", en = "Cleanroom Filters")
    public static final String cleanroom_filters = "cleanroom_filters";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "电容", en = "Batteries")
    public static final String batteries = "batteries";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "灯", en = "Lamp")
    public static final String lamp = "lamp";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "无框灯", en = "Borderless Lamp")
    public static final String borlamp = "borlamp";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "消声仓", en = "Muffler Hatch")
    public static final String muffler_hatch = "muffler_hatch";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "转子支架", en = "Rotor Holder")
    public static final String rotor_hatch = "rotor_hatch";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "能源通讯单元", en = "Energy Communication Unit")
    public static final String ecu = "ecu";
    @GTMMRegisterLanguage(valuePrefix = blockmap_valuePrefix, cn = "电网容量组件", en = "Wireless Energy Capacity Component")
    public static final String wecc = "wecc";

    public static void build() {
        // 线圈
        var coils = new ArrayList<>(GTCEuAPI.HEATING_COILS.entrySet());
        coils.sort(Comparator.comparingInt(entry -> entry.getKey().getTier()));
        MAP.put(heating_coils, coils.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        // 过滤机械方块
        var clrs = new ArrayList<>(GTCEuAPI.CLEANROOM_FILTERS.entrySet());
        clrs.sort(Comparator.comparingInt(key -> key.getKey().getCleanroomType().hashCode()));
        MAP.put(cleanroom_filters, clrs.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        // 电容
        var Batteries = new ArrayList<>(GTCEuAPI.PSS_BATTERIES.entrySet());
        Batteries.sort(Comparator.comparingInt(entry -> entry.getKey().getTier()));
        MAP.put(batteries, Batteries.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        // 灯
        LAMP = GTBlocks.LAMPS.values().stream().map(RegistryEntry::get).toArray(Block[]::new);
        MAP.put(lamp, LAMP);
        // 无框灯
        BORLAMP = GTBlocks.BORDERLESS_LAMPS.values().stream().map(RegistryEntry::get).toArray(Block[]::new);
        MAP.put(borlamp, BORLAMP);
        // 能源通讯单元
        var ecublock = new ArrayList<>(GTMMAPI.ECU.entrySet());
        ecublock.sort(Comparator.comparingInt(entry -> entry.getKey().getTier()));
        MAP.put(ecu, ecublock.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        // 消声仓
        MAP.put(muffler_hatch, Arrays.stream(GTMachines.MUFFLER_HATCH).filter(Objects::nonNull).distinct().sorted(Comparator.comparingInt(MachineDefinition::getTier)).map(MachineDefinition::get).toArray(Block[]::new));
        // 转子仓
        MAP.put(rotor_hatch, Arrays.stream(GTMachines.ROTOR_HOLDER).filter(Objects::nonNull).distinct().sorted(Comparator.comparingInt(MachineDefinition::getTier)).map(MachineDefinition::get).toArray(Block[]::new));

        if (GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable) {
            var weccblock = new ArrayList<>(GTMMAPI.WECC.entrySet());
            weccblock.sort(Comparator.comparingInt(entry -> entry.getKey().getTier()));
            MAP.put(wecc, weccblock.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        }

        MAP.forEach((category, blocks) -> {
            for (Block block : blocks) {
                BLOCK_CATEGORY_MAP.put(block, category);
            }
        });
    }

    @Nullable
    public static String getCategory(Block block) {
        return BLOCK_CATEGORY_MAP.get(block);
    }
}
