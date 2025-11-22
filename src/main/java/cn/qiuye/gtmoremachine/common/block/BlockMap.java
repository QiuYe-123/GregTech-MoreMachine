package cn.qiuye.gtmoremachine.common.block;

import com.gregtechceu.gtceu.api.GTCEuAPI;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;
import java.util.function.Supplier;

public interface BlockMap {

    Object2ObjectOpenHashMap<String, Lazy<Block[]>> tierBlockMap = new Object2ObjectOpenHashMap<>(1);

    static void init() {
        tierBlockMap.put("coil", Lazy.of(() -> GTCEuAPI.HEATING_COILS.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getCoilTemperature()))
                .map(Map.Entry::getValue).map(Supplier::get).toArray(Block[]::new)));
    }
}
