package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigInteger;
import java.util.UUID;

public interface ICapacitylimitData {

    UUID UUID();

    BigInteger StorageCapacity();

    MetaMachine machine();
}
