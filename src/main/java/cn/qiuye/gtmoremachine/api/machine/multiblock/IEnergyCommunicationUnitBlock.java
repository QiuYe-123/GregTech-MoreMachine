package cn.qiuye.gtmoremachine.api.machine.multiblock;

import org.jetbrains.annotations.NotNull;

public interface IEnergyCommunicationUnitBlock {

    int getTier();

    @NotNull
    String getEnergyCommunicationUnitBlockName();
}
