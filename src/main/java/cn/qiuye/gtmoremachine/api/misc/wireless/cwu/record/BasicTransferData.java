package cn.qiuye.gtmoremachine.api.misc.wireless.cwu.record;

import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.Interface.ITransferData;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.util.UUID;

public record BasicTransferData(UUID UUID, int Throughput, MetaMachine machine) implements ITransferData {}
