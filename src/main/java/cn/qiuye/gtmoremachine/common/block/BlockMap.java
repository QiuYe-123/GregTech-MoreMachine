package cn.qiuye.gtmoremachine.common.block;

import cn.qiuye.gtmoremachine.api.GTMMAPI;
import cn.qiuye.gtmoremachine.config.GTMMConfig;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;

public interface BlockMap {

    Object2ObjectOpenHashMap<String, Lazy<Block[]>> tierBlockMap = new Object2ObjectOpenHashMap<>(50);

    Int2ObjectOpenHashMap<Supplier<?>> rotMap = new Int2ObjectOpenHashMap<>(11);

    String COIL = "item.gtmoremachine.advanced_terminal.setting.coil";
    String COMP = "item.gtmoremachine.advanced_terminal.setting.comp";
    String CLEA = "item.gtmoremachine.advanced_terminal.setting.clea";
    String LAMP = "item.gtmoremachine.advanced_terminal.setting.lamp";
    String BORLAMP = "item.gtmoremachine.advanced_terminal.setting.borlamp";
    String ROTOR = "item.gtmoremachine.advanced_terminal.setting.rotor";
    String WECC = "item.gtmoremachine.advanced_terminal.setting.wecc";
    String ECU = "item.gtmoremachine.advanced_terminal.setting.ecu";

    static void init() {
        tierBlockMap.put(COIL, Lazy.of(() -> GTCEuAPI.HEATING_COILS.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getCoilTemperature()))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        tierBlockMap.put(COMP, Lazy.of(() -> GTCEuAPI.PSS_BATTERIES.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getTier()))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        tierBlockMap.put(CLEA, Lazy.of(() -> GTCEuAPI.CLEANROOM_FILTERS.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getCleanroomType().hashCode()))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        tierBlockMap.put(LAMP, Lazy.of(() -> GTBlocks.LAMPS.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getFireworkColor()))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        tierBlockMap.put(BORLAMP, Lazy.of(() -> GTBlocks.BORDERLESS_LAMPS.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getFireworkColor()))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        tierBlockMap.put(ECU, Lazy.of(() -> GTMMAPI.ECU.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        tierBlockMap.put(ROTOR, Lazy.of(() -> rotMap.int2ObjectEntrySet().stream()
                .sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey))
                .map(Int2ObjectMap.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        if (GTMMConfig.getINSTANCE().isWirelessCapacitylimitEnable) {
            tierBlockMap.put(WECC, Lazy.of(() -> GTMMAPI.WECC.entrySet().stream()
                    .sorted((entry1, entry2) -> {
                        BigInteger throughput1 = entry1.getValue().get().getData().getCapacity();
                        BigInteger throughput2 = entry2.getValue().get().getData().getCapacity();
                        return throughput1.compareTo(throughput2);
                    })
                    .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
        }
    }
}
