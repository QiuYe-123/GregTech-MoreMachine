package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.GTmm;
import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationHatch;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationReceiver;
import com.gregtechceu.gtceu.api.capability.GTCapability;
import com.gregtechceu.gtceu.api.capability.recipe.CWURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessNotifiableCWUContainer extends NotifiableRecipeHandlerTrait<Integer> implements IOpticalComputationHatch, IOpticalComputationReceiver, IWirelessCWUContainerHolder {

    public static final MachineTraitType<WirelessNotifiableCWUContainer> TYPE = new MachineTraitType<>(
            WirelessNotifiableCWUContainer.class);

    @Override
    public MachineTraitType<WirelessNotifiableCWUContainer> getTraitType() {
        return TYPE;
    }

    private WirelessCWUContainer container;
    @Getter
    protected IO handlerIO;
    @Getter
    protected boolean transmitter;

    protected long lastTimeStamp;
    private int currentOutputCwu = 0, lastOutputCwu = 0;

    public WirelessNotifiableCWUContainer(MetaMachine machine, IO handlerIO, boolean transmitter) {
        super(machine);
        this.handlerIO = handlerIO;
        this.transmitter = transmitter;

        this.lastTimeStamp = Long.MIN_VALUE;
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        var latestTimeStamp = getMachine().getOffsetTimer();
        if (lastTimeStamp < latestTimeStamp) {
            lastOutputCwu = currentOutputCwu;
            currentOutputCwu = 0;
            lastTimeStamp = latestTimeStamp;
        }
        seen.add(this);
        if (handlerIO == IO.IN) {
            if (isTransmitter()) {
                // Ask the Multiblock controller, which *should* be an IOpticalComputationProvider
                if (machine instanceof IOpticalComputationProvider provider) {
                    return provider.requestCWUt(cwut, simulate, seen);
                } else if (machine instanceof IMultiPart part) {
                    if (!part.isFormed()) {
                        return 0;
                    }
                    for (MultiblockControllerMachine controller : part.getControllers()) {
                        if (controller instanceof IOpticalComputationProvider provider) {
                            return provider.requestCWUt(cwut, simulate, seen);
                        }
                        for (MachineTrait trait : controller.self().getTraitHolder().getAllTraits()) {
                            if (trait instanceof IOpticalComputationProvider provider) {
                                return provider.requestCWUt(cwut, simulate, seen);
                            }
                        }
                    }
                    GTmm.LOGGER
                            .error("NotifiableComputationContainer could request CWU/t from its machine's controller!");
                    return 0;
                } else {
                    GTmm.LOGGER.error("NotifiableComputationContainer could request CWU/t from its machine!");
                    return 0;
                }
            } else {
                // Ask the attached Transmitter hatch, if it exists
                IOpticalComputationProvider provider = getOpticalNetProvider();
                if (provider == null) return 0;
                return provider.requestCWUt(cwut, simulate, seen);
            }
        } else {
            lastOutputCwu = lastOutputCwu - cwut;
            return Math.min(lastOutputCwu, cwut);
        }
    }

    /**
     * 重写：获取最大算力提供能力
     */
    @Override
    public int getMaxCWUt(Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        if (handlerIO == IO.IN) {
            if (isTransmitter()) {
                // Ask the Multiblock controller, which *should* be an IOpticalComputationProvider
                if (machine instanceof IOpticalComputationProvider provider) {
                    return provider.getMaxCWUt(seen);
                } else if (machine instanceof IMultiPart part) {
                    if (!part.isFormed()) {
                        return 0;
                    }
                    for (MultiblockControllerMachine controller : part.getControllers()) {
                        if (!controller.isFormed()) {
                            continue;
                        }
                        if (controller instanceof IOpticalComputationProvider provider) {
                            return provider.getMaxCWUt(seen);
                        }
                        for (MachineTrait trait : controller.self().getTraitHolder().getAllTraits()) {
                            if (trait instanceof IOpticalComputationProvider provider) {
                                return provider.getMaxCWUt(seen);
                            }
                        }
                    }
                    GTmm.LOGGER.error(
                            "NotifiableComputationContainer could not get maximum CWU/t from its machine's controller!");
                    return 0;
                } else {
                    GTmm.LOGGER.error("NotifiableComputationContainer could not get maximum CWU/t from its machine!");
                    return 0;
                }
            } else {
                // Ask the attached Transmitter hatch, if it exists
                IOpticalComputationProvider provider = getOpticalNetProvider();
                if (provider == null) return 0;
                return provider.getMaxCWUt(seen);
            }
        } else {
            return lastOutputCwu;
        }
    }

    @Override
    public boolean canBridge(Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return true;
    }

    @Override
    public @Nullable List<Integer> handleRecipeInner(IO io, GTRecipe recipe, List<Integer> left,
                                                     boolean simulate) {
        IOpticalComputationProvider provider = getOpticalNetProvider();
        if (provider == null) return left;

        int sum = left.stream().mapToInt(Integer::intValue).sum();
        if (io == IO.IN) {
            int availableCWUt = requestCWUt(Integer.MAX_VALUE, true);
            if (availableCWUt >= sum) {
                if (recipe.data.getBoolean("duration_is_total_cwu")) {
                    int drawn = provider.requestCWUt(availableCWUt, simulate);
                    if (!simulate) {
                        if (machine instanceof IRecipeLogicMachine rlm) {
                            // first, remove the progress the recipe logic adds.
                            rlm.getRecipeLogic().setProgress(rlm.getRecipeLogic().getProgress() - 1 + drawn);
                        } else if (machine instanceof IMultiPart multiPart) {
                            for (MultiblockControllerMachine controller : multiPart.getControllers()) {
                                if (controller instanceof IRecipeLogicMachine rlm) {
                                    rlm.getRecipeLogic().setProgress(rlm.getRecipeLogic().getProgress() - 1 + drawn);
                                }
                            }
                        }
                    }
                    sum -= drawn;
                } else {
                    sum -= provider.requestCWUt(sum, simulate);
                }
            }
        } else if (io == IO.OUT) {
            int canInput = this.getMaxCWUt() - this.lastOutputCwu;
            if (!simulate) {
                this.currentOutputCwu = Math.min(canInput, sum);
            }
            sum = sum - canInput;
        }
        return sum <= 0 ? null : Collections.singletonList(sum);
    }

    @Override
    public List<Object> getContents() {
        return List.of(lastOutputCwu);
    }

    @Override
    public double getTotalContentAmount() {
        return lastOutputCwu;
    }

    @Override
    public RecipeCapability<Integer> getCapability() {
        return CWURecipeCapability.CAP;
    }

    @Override
    public @Nullable IOpticalComputationProvider getComputationProvider() {
        if (this.handlerIO.support(IO.OUT)) {
            return this;
        }
        if (machine instanceof IOpticalComputationReceiver receiver) {
            return receiver.getComputationProvider();
        } else if (machine instanceof IOpticalComputationProvider provider) {
            return provider;
        } else if (machine instanceof IRecipeCapabilityHolder recipeCapabilityHolder) {
            var cwuCap = recipeCapabilityHolder.getCapabilitiesFlat(IO.IN, CWURecipeCapability.CAP);
            if (!cwuCap.isEmpty()) {
                var provider = (IOpticalComputationProvider) cwuCap.get(0);
                if (provider != this) {
                    return provider;
                }
            }
        }
        for (Direction direction : GTUtil.DIRECTIONS) {
            BlockEntity blockEntity = machine.getLevel().getBlockEntity(machine.getBlockPos().relative(direction));
            if (blockEntity == null) continue;

            // noinspection DataFlowIssue can be null just fine.
            IOpticalComputationProvider provider = blockEntity
                    .getCapability(GTCapability.CAPABILITY_COMPUTATION_PROVIDER, direction.getOpposite()).orElse(null);
            // noinspection ConstantValue can be null because above.
            if (provider != null && provider != this) {
                return provider;
            }
        }
        return null;
    }

    @Override
    public @Nullable UUID getUUID() {
        return this.getMachine().getOwnerUUID();
    }

    @Override
    public void setWirelessCWUContainerCache(WirelessCWUContainer container) {
        this.container = container;
    }

    @Override
    public WirelessCWUContainer getWirelessCWUContainerCache() {
        return this.container;
    }

    @Nullable
    private IOpticalComputationProvider getOpticalNetProvider() {
        return null;
    }
}
