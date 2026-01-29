package cn.qiuye.gtmoremachine.common.data.machines;

import cn.qiuye.gtmoremachine.common.data.GTMMCovers;
import cn.qiuye.gtmoremachine.common.data.GTMMCreativeModeTabs;
import cn.qiuye.gtmoremachine.common.item.CreativeFluidStats;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.CreativeEnergyHatchPartMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.CreativeInputBusPartMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.CreativeInputHatchPartMachine;
import cn.qiuye.gtmoremachine.common.machine.multiblock.part.CreativeLaserHatchPartMachine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ICustomDescriptionId;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.ItemFluidContainer;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import static cn.qiuye.gtmoremachine.common.registry.GTMMRegistration.GTMM;

public class CreativeMachines {

    static {
        GTMM.creativeModeTab(() -> GTMMCreativeModeTabs.CREATIVE_TAB);
    }

    public final static MachineDefinition CREATIVE_FLUID_INPUT_HATCH = GTMM
            .machine("creative_fluid_input_hatch", CreativeInputHatchPartMachine::new)
            .langValue("Creative Input Hatch")
            .rotationState(RotationState.ALL)
            .colorOverlayTieredHullModel("overlay_pipe_in_emissive", "overlay_pipe_9x", GTMachineModels.OVERLAY_FLUID_HATCH_INPUT)
            .tooltips(Component.translatable("gtmoremachine.creative_tooltip"))
            .abilities(PartAbility.IMPORT_FLUIDS, PartAbility.IMPORT_FLUIDS_9X)
            .tier(GTValues.MAX)
            .register();

    public final static MachineDefinition CREATIVE_ITEM_INPUT_BUS = GTMM
            .machine("creative_item_input_bus", CreativeInputBusPartMachine::new)
            .langValue("Creative Input Bus")
            .rotationState(RotationState.ALL)
            .colorOverlayTieredHullModel("overlay_pipe_in_emissive", "overlay_pipe", GTMachineModels.OVERLAY_ITEM_HATCH_INPUT)
            .tooltips(Component.translatable("gtmoremachine.creative_tooltip"))
            .abilities(PartAbility.IMPORT_ITEMS)
            .tier(GTValues.MAX)
            .register();

    // energy input hatch
    public final static MachineDefinition CREATIVE_ENERGY_INPUT_HATCH = GTMM
            .machine("creative_energy_hatch", CreativeEnergyHatchPartMachine::new)
            .langValue("§rCreative Energy Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("gtmoremachine.creative_tooltip"))
            .overlayTieredHullModel("energy_input_hatch")
            .abilities(PartAbility.INPUT_ENERGY)
            .tier(GTValues.MAX)
            .register();

    // laser input hatch
    public final static MachineDefinition CREATIVE_LASER_INPUT_HATCH = GTMM
            .machine("creative_laser_hatch", CreativeLaserHatchPartMachine::new)
            .langValue("§rCreative Laser Target Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("gtmoremachine.creative_tooltip"))
            .overlayTieredHullModel("laser_target_hatch")
            .abilities(PartAbility.INPUT_LASER)
            .tier(GTValues.MAX)
            .register();

    public final static ItemEntry<ComponentItem> CREATIVE_ENERGY_COVER = GTMM
            .item("creative_energy_cover", ComponentItem::create)
            .lang("Creative Energy Cover")
            .onRegister(GTItems.attach(new CoverPlaceBehavior(GTMMCovers.CREATIVE_ENERGY),
                    new TooltipBehavior(lines -> lines.add(Component.translatable("gtmoremachine.creative_tooltip")))))
            .register();

    public final static ItemEntry<ComponentItem> CREATIVE_FLUID_CELL = GTMM
            .item("creative_fluid_cell", ComponentItem::create)
            .lang("Creative Fluid Cell")
            .color(() -> GTItems::cellColor)
            .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
            .onRegister(GTItems.attach(cellName(),
                    new CreativeFluidStats(),
                    new ItemFluidContainer()))
            .register();

    public static ICustomDescriptionId cellName() {
        return new ICustomDescriptionId() {

            @Override
            public Component getItemName(ItemStack stack) {
                Component prefix = FluidUtil.getFluidContained(stack).map(FluidStack::getDisplayName)
                        .orElse(Component.translatable("gtceu.fluid.empty"));
                return Component.translatable(stack.getDescriptionId(), prefix);
            }
        };
    }

    public static void init() {}
}
