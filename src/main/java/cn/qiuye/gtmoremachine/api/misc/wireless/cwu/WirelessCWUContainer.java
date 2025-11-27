package cn.qiuye.gtmoremachine.api.misc.wireless.cwu;

import cn.qiuye.gtmoremachine.api.misc.wireless.time.TimeStat;
import cn.qiuye.gtmoremachine.data.wireless.cwu.WirelessCWUSavaedData;
import cn.qiuye.gtmoremachine.utils.TeamUtils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;

import lombok.Getter;

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

    private GlobalPos bindPos;

    private final UUID uuid;

    private final TimeStat inCWUStat;

    private final TimeStat outCWUStat;

    public WirelessCWUContainer(UUID uuid, BigInteger storage, GlobalPos bindPos) {
        this.storage = storage;
        this.bindPos = bindPos;
        this.uuid = uuid;
        this.inCWUStat = new TimeStat(0);
        this.outCWUStat = new TimeStat(0);
    }

    private WirelessCWUContainer(UUID uuid) {
        this.uuid = uuid;
        this.storage = BigInteger.ZERO;
        int currentTick = server.getTickCount();
        this.inCWUStat = new TimeStat(currentTick);
        this.outCWUStat = new TimeStat(currentTick);
    }
}
