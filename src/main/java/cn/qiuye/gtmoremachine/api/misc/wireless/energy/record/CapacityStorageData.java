package cn.qiuye.gtmoremachine.api.misc.wireless.energy.record;

import cn.qiuye.gtmoremachine.api.misc.wireless.energy.Interface.ICapacitylimitData;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigInteger;
import java.util.UUID;

public record CapacityStorageData(UUID UUID, BigInteger StorageCapacity, BigInteger PassiveDrain, MetaMachine machine) implements ICapacitylimitData {}
