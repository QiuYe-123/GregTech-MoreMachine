package cn.qiuye.gtmoremachine.common.block

import cn.qiuye.gtmoremachine.api.GTMMAPI
import cn.qiuye.gtmoremachine.config.GTMMConfig

import com.gregtechceu.gtceu.api.GTCEuAPI
import com.gregtechceu.gtceu.api.block.IMachineBlock
import com.gregtechceu.gtceu.common.data.GTBlocks

import net.minecraft.world.level.block.Block
import net.minecraftforge.common.util.Lazy

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

import java.util.function.Supplier

object BlockMap {
    @JvmField
    val tierBlockMap = Object2ObjectOpenHashMap<String, Lazy<Array<Block>>>(50)

    @JvmField
    val rotMap = Int2ObjectOpenHashMap<Supplier<IMachineBlock>>(11)

    const val COIL = "item.gtmoremachine.advanced_terminal.setting.coil"
    const val COMP = "item.gtmoremachine.advanced_terminal.setting.comp"
    const val CLEA = "item.gtmoremachine.advanced_terminal.setting.clea"
    const val LAMP = "item.gtmoremachine.advanced_terminal.setting.lamp"
    const val BORLAMP = "item.gtmoremachine.advanced_terminal.setting.borlamp"
    const val ROTOR = "item.gtmoremachine.advanced_terminal.setting.rotor"
    const val WECC = "item.gtmoremachine.advanced_terminal.setting.wecc"
    const val ECU = "item.gtmoremachine.advanced_terminal.setting.ecu"

    @JvmStatic
    fun init() {
        tierBlockMap[COIL] = Lazy.of {
            GTCEuAPI.HEATING_COILS.entries
                .sortedBy { it.key.coilTemperature }
                .map { it.value.get() }
                .toTypedArray()
        }

        tierBlockMap[COMP] = Lazy.of {
            GTCEuAPI.PSS_BATTERIES.entries
                .sortedBy { it.key.tier }
                .map { it.value.get() }
                .toTypedArray()
        }

        tierBlockMap[CLEA] = Lazy.of {
            GTCEuAPI.CLEANROOM_FILTERS.entries
                .sortedBy { it.key.cleanroomType.hashCode() }
                .map { it.value.get() }
                .toTypedArray()
        }

        tierBlockMap[LAMP] = Lazy.of {
            GTBlocks.LAMPS.entries
                .sortedBy { it.key.fireworkColor }
                .map { it.value.get() }
                .toTypedArray()
        }

        tierBlockMap[BORLAMP] = Lazy.of {
            GTBlocks.BORDERLESS_LAMPS.entries
                .sortedBy { it.key.fireworkColor }
                .map { it.value.get() }
                .toTypedArray()
        }

        tierBlockMap[ECU] = Lazy.of {
            GTMMAPI.ECU.entries
                .sortedBy { it.key }
                .map { it.value.get() }
                .toTypedArray()
        }

        tierBlockMap[ROTOR] = Lazy.of {
            rotMap.int2ObjectEntrySet()
                .sortedBy { it.intKey }
                .map { it.value.get().self() }
                .toTypedArray()
        }

        if (GTMMConfig.INSTANCE.isWirelessCapacitylimitEnable) {
            tierBlockMap[WECC] = Lazy.of {
                GTMMAPI.WECC.entries
                    .sortedWith(
                        Comparator { entry1, entry2 ->
                            val throughput1 = entry1.value.get().data.capacity
                            val throughput2 = entry2.value.get().data.capacity
                            throughput1.compareTo(throughput2)
                        },
                    )
                    .map { it.value.get() }
                    .toTypedArray()
            }
        }
    }
}
