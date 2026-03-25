package cn.qiuye.gtmoremachine.api.misc.wireless.energy.record;

import cn.qiuye.gtmoremachine.api.misc.wireless.energy.Interface.ITransferData;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigInteger;
import java.util.UUID;

public record BasicTransferData(UUID UUID, BigInteger Throughput, MetaMachine machine) implements ITransferData {}
