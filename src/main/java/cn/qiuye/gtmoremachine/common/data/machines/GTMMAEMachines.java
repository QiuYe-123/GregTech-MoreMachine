package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.integration.ae.machine.MEOutputPartMachine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMMREGISTRATE;

public class GTMMAEMachines {

    public static final MachineDefinition ME_EXPORT_BUFFER = GTMMREGISTRATE
            .machine("me_export_buffer", MEOutputPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_ITEMS, PartAbility.EXPORT_FLUIDS)
            .colorOverlayTieredHullModel(GTmm.id("block/overlay/appeng/me_output_bus"))
            .tier(GTValues.LuV)
            .register();

    public static void init() {}
}
