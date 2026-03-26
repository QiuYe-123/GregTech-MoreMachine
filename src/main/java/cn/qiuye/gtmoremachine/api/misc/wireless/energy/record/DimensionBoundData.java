package cn.qiuye.gtmoremachine.api.misc.wireless.energy.record;

import cn.qiuye.gtmoremachine.api.misc.wireless.energy.feature.IDimensionTransferData;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.util.UUID;

public record DimensionBoundData(UUID UUID, int Voltagelevel, MetaMachine machine) implements IDimensionTransferData {}
