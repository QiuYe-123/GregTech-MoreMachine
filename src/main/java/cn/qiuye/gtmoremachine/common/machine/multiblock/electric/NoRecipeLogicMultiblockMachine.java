package cn.qiuye.gtmoremachine.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Player;

@MethodsReturnNonnullByDefault
public class NoRecipeLogicMultiblockMachine extends MultiblockControllerMachine implements IControllable, IFancyUIMachine, IDisplayUIMachine {

    public NoRecipeLogicMultiblockMachine(BlockEntityCreationInfo holder) {
        super(holder);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return IFancyUIMachine.super.createUI(entityPlayer);
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {}
}
