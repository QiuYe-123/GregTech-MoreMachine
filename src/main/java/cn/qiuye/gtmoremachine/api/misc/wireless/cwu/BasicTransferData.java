package cn.qiuye.gtmoremachine.api.misc.wireless.cwu;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.util.UUID;

public record BasicTransferData(UUID UUID, int Throughput, MetaMachine machine) implements ITransferData {}
