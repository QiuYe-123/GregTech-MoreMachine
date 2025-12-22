package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import java.math.BigDecimal;
import java.math.BigInteger;

public record StoragePercentageData(BigDecimal storagePercentage, BigInteger storage, BigInteger capacity) {}
