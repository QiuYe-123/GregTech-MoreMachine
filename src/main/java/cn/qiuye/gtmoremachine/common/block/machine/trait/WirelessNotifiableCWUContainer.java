package cn.qiuye.gtmoremachine.common.block.machine.trait;

import cn.qiuye.gtmoremachine.api.machine.IWirelessCWUContainerHolder;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WirelessNotifiableCWUContainer extends NotifiableComputationContainer implements IOpticalComputationProvider, IWirelessCWUContainerHolder {

    private WirelessCWUContainer container;

    public WirelessNotifiableCWUContainer(MetaMachine machine, IO handlerIO, boolean transmitter) {
        super(machine, handlerIO, transmitter);
    }

    /**
     * 获取或创建无线容器
     */
    private WirelessCWUContainer getOrCreateContainer() {
        UUID currentUUID = getUUID();
        // 检查是否需要更新容器缓存
        if (currentUUID != null && (this.container == null || !currentUUID.equals(this.getUUID()))) {
            this.container = WirelessCWUContainer.getOrCreateContainer(currentUUID);
        }
        return this.container;
    }

    /**
     * 上传算力到无线网络
     * 
     * @param cwu      要上传的算力量
     * @param simulate 是否为模拟操作
     * @return 实际上传的算力量
     */
    public int upload(int cwu, boolean simulate) {
        if (cwu <= 0) return 0;

        WirelessCWUContainer container = getOrCreateContainer();

        if (!simulate) {
            // 实际操作：调用容器上传方法
            container.upload(cwu, getMachine());
        }
        return cwu;
    }

    /**
     * 从无线网络下载算力
     * 
     * @param cwu      请求的算力量
     * @param simulate 是否为模拟操作
     * @return 实际下载的算力量
     */
    public int download(int cwu, boolean simulate) {
        if (cwu <= 0) return 0;

        WirelessCWUContainer container = getOrCreateContainer();

        if (simulate) {
            // 模拟操作：返回可用的算力量，不超过请求量
            int freeCWU = container.getfreeCWU();
            return Math.min(freeCWU, cwu);
        } else {
            // 实际操作：调用容器下载方法
            return container.download(cwu, getMachine());
        }
    }

    /**
     * 获取可用的无线算力（用于模拟）
     * 
     * @return 可用算力量
     */
    public int getfreeCWU() {
        WirelessCWUContainer container = getOrCreateContainer();
        return container.getfreeCWU();
    }

    /**
     * 重写：处理算力请求（针对输入仓）
     */
    @Override
    public int requestCWUt(int cwut, boolean simulate, Collection<IOpticalComputationProvider> seen) {
        seen.add(this);

        if (!isTransmitter()) {
            // 接收器仓：从无线网络下载算力
            return download(cwut, simulate);
        } else {
            // 发射器仓：对于网络请求，无线发射器不提供算力
            return 0;
        }
    }

    /**
     * 重写：获取最大算力提供能力
     */
    @Override
    public int getMaxCWUt(Collection<IOpticalComputationProvider> seen) {
        seen.add(this);

        if (!isTransmitter()) {
            // 接收器仓：返回无线网络可用算力
            return getfreeCWU();
        } else {
            // 发射器仓：不提供算力给网络
            return 0;
        }
    }

    /**
     * 重写：处理配方中的算力输入输出
     */
    @Override
    public List<Integer> handleRecipeInner(IO io, GTRecipe recipe, List<Integer> left, boolean simulate) {
        if (left.isEmpty()) return left;

        int total = left.stream().mapToInt(Integer::intValue).sum();
        int processed = 0;

        if (io == IO.IN) {
            // 输入处理：接收器仓从无线网络下载算力
            if (!isTransmitter()) {
                processed = download(total, simulate);
            }
        } else if (io == IO.OUT) {
            // 输出处理：发射器仓上传算力到无线网络
            if (isTransmitter()) {
                processed = upload(total, simulate);
            }
        }

        // 计算剩余未处理的算力
        int remaining = total - processed;
        if (remaining <= 0) {
            return null; // 全部处理完成
        } else {
            return Collections.singletonList(remaining);
        }
    }

    @Override
    public boolean canBridge(Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return true;
    }

    @Override
    public IO getHandlerIO() {
        return this.transmitter ? IO.NONE : IO.IN;
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
}
