package cn.qiuye.gtmoremachine.api.misc.wireless.energy;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.world.level.Level;

import java.util.UUID;

public interface IDimensionTransferData {

    UUID UUID();

    Level level();

    int Voltagelevel();

    MetaMachine machine();
}
