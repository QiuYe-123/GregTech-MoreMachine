package cn.qiuye.gtmoremachine.common.block;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.api.annotation.GTMMScanned;
import cn.qiuye.gtmoremachine.api.annotation.language.GTMMRegisterLanguage;
import cn.qiuye.gtmoremachine.config.GTMMConfig;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;
import java.util.function.Supplier;

@GTMMScanned
public class BlockMap {

    public static final Object2ObjectOpenHashMap<String, Block[]> MAP = new Object2ObjectOpenHashMap<>(50);
    public static final Int2ObjectOpenHashMap<Supplier<IMachineBlock>> rotMap = new Int2ObjectOpenHashMap<>(11);

    public static Block[] LAMP;
    public static Block[] BORLAMP;
    public static Block[] ROTOR_HOLDER;

    public static final String namePrefix = "gtmoremachine.adv_terminal.block_map";

    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "线圈", en = "Heating Coils")
    public static final String heating_coils = "heating_coils";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "过滤方块", en = "Cleanroom Filters")
    public static final String cleanroom_filters = "cleanroom_filters";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "电容", en = "Batteries")
    public static final String batteries = "batteries";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "灯", en = "Lamp")
    public static final String lamp = "lamp";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "无框灯", en = "Borderless Lamp")
    public static final String borlamp = "borlamp";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "转子支架", en = "Rotor Holder")
    public static final String rotor_holder = "rotor_holder";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "能源通讯单元", en = "Energy Communication Unit")
    public static final String ECU = "ecu";
    @GTMMRegisterLanguage(namePrefix = namePrefix, cn = "电网容量组件", en = "Wireless Energy Capacity Component")
    public static final String WECC = "wecc";

    public static void build() {
        load();
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
        MAP.put(ECU, ecublock.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        // 转子支架
        MAP.put(rotor_holder, ROTOR_HOLDER);

        if (GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable) {
            var weccblock = new ArrayList<>(GTMMAPI.WECC.entrySet());
            weccblock.sort(Comparator.comparingInt(entry -> entry.getKey().getTier()));
            MAP.put(WECC, weccblock.stream().map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new));
        }
    }

    public static void load() {
        int j = 0;
        for (int i = 3; i <= 13; i++) {
            BlockMap.ROTOR_HOLDER[j] = GTMachines.ROTOR_HOLDER[i].getBlock();
            j++;
        }
    }
}
