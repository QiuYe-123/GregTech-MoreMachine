package cn.qiuye.gtmoremachine.api.misc.wireless.cwu;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.misc.wireless.time.TimeStat;
import cn.qiuye.gtmoremachine.data.wireless.cwu.WirelessCWUSavaedData;
import cn.qiuye.gtmoremachine.utils.BigIntegerUtils;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.server.MinecraftServer;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.UUID;
import java.util.WeakHashMap;

@Getter
public class WirelessCWUContainer {

    public static boolean observed;

    public static final WeakHashMap<MetaMachine, ITransferData> TRANSFER_DATA = new WeakHashMap<>();
    public static MinecraftServer server;

    public static WirelessCWUContainer getOrCreateContainer(UUID uuid) {
        return WirelessCWUSavaedData.INSTANCE.containerMap.computeIfAbsent(TeamUtils.getTeamUUID(uuid), WirelessCWUContainer::new);
    }

    private BigInteger storage;

    private final UUID UUID;

    private final TimeStat allCWUStat;

    private final TimeStat inCWUStat;

    private final TimeStat outCWUStat;

    public WirelessCWUContainer(UUID uuid, BigInteger storage) {
        this.storage = storage;
        this.UUID = uuid;
        this.allCWUStat = new TimeStat(0);
        this.inCWUStat = new TimeStat(0);
        this.outCWUStat = new TimeStat(0);
    }

    private WirelessCWUContainer(UUID uuid) {
        this.UUID = uuid;
        this.storage = BigInteger.ZERO;
        int currentTick = server.getTickCount();
        this.allCWUStat = new TimeStat(currentTick);
        this.inCWUStat = new TimeStat(currentTick);
        this.outCWUStat = new TimeStat(currentTick);
    }

    public void upload(int cwu, @Nullable MetaMachine machine) {
        if (cwu <= 0) return;
        if (machine != null) {
            allCWUStat.update(BigInteger.valueOf(cwu), server.getTickCount());
            inCWUStat.update(BigInteger.valueOf(cwu), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, cwu, machine));
        }
        storage = new BigInteger(String.valueOf(inCWUStat.getAvg())).add(new BigInteger(String.valueOf(outCWUStat.getAvg())));
        WirelessCWUSavaedData.INSTANCE.setDirty(true);
    }

    public int download(int cwu, @Nullable MetaMachine machine) {
        int change = Math.min(BigIntegerUtils.getIntValue(storage) / 10, cwu);
        if (change <= 0) return 0;
        if (machine != null) {
            allCWUStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
            outCWUStat.update(BigInteger.valueOf(change).negate(), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, -cwu, machine));
        }
        storage = new BigInteger(String.valueOf(inCWUStat.getAvg())).add(new BigInteger(String.valueOf(outCWUStat.getAvg())));
        WirelessCWUSavaedData.INSTANCE.setDirty(true);
        return change;
    }

    public void setStorage(BigInteger cwu) {
        storage = cwu;
        WirelessCWUSavaedData.INSTANCE.setDirty(true);
    }

    public int getfreeCWU() {
        return BigIntegerUtils.getIntValue(storage) / 10;
    }
}
