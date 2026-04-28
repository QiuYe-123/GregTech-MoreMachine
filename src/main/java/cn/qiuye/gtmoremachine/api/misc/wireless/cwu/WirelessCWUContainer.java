package cn.qiuye.gtmoremachine.api.misc.wireless.cwu;

import cn.qiuye.gtmoremachine.api.gui.monitor.Status;
import cn.qiuye.gtmoremachine.api.misc.time.TimeStat;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.feature.ITransferData;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.record.BasicTransferData;
import cn.qiuye.gtmoremachine.data.wireless.cwu.WirelessCWUSavedData;
import cn.qiuye.gtmoremachine.utils.BigNumberUtils;
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
        return WirelessCWUSavedData.INSTANCE.containerMap.computeIfAbsent(TeamUtils.getTeamUUID(uuid), WirelessCWUContainer::new);
    }

    private BigInteger storage;

    private final UUID UUID;

    private final TimeStat CWUStat;

    public WirelessCWUContainer(UUID uuid, BigInteger storage) {
        this.storage = storage;
        this.UUID = uuid;
        this.CWUStat = new TimeStat(0);
    }

    private WirelessCWUContainer(UUID uuid) {
        this.UUID = uuid;
        this.storage = BigInteger.ZERO;
        int currentTick = server.getTickCount();
        this.CWUStat = new TimeStat(currentTick);
    }

    public int upload(int cwu, @Nullable MetaMachine machine) {
        if (cwu <= 0) return 0;
        if (machine != null) {
            CWUStat.update(BigInteger.valueOf(cwu), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, cwu, machine));
        }
        storage = CWUStat.getAvg(Status.All).toBigInteger();
        WirelessCWUSavedData.INSTANCE.setDirty(true);
        return 0;
    }

    public int download(int cwu, @Nullable MetaMachine machine) {
        int change = Math.min(BigNumberUtils.getIntValue(storage) / 10, cwu);
        if (change <= 0) return 0;
        if (machine != null) {
            CWUStat.update(BigInteger.valueOf(change), server.getTickCount());
        }
        if (observed && machine != null) {
            TRANSFER_DATA.put(machine, new BasicTransferData(UUID, -cwu, machine));
        }
        storage = CWUStat.getAvg(Status.All).toBigInteger();
        WirelessCWUSavedData.INSTANCE.setDirty(true);
        return change;
    }

    public void setStorage(BigInteger cwu) {
        storage = cwu;
        WirelessCWUSavedData.INSTANCE.setDirty(true);
    }

    public int getfreeCWU() {
        return BigNumberUtils.getIntValue(storage) / 20;
    }
}
