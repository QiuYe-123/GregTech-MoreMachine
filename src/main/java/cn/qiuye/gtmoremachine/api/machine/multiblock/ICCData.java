package cn.qiuye.gtmoremachine.api.machine.multiblock;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public interface ICCData {

    int getTier();

    BigInteger getCapacity();

    BigInteger getLossEnergy();

    @NotNull
    String getCCName();
}
