package cn.qiuye.gtmoremachine.api.machine.multiblock.feature;

import org.jetbrains.annotations.NotNull;

public interface IECUBlock {

    int getTier();

    @NotNull
    String getECUBlockName();
}
