package cn.qiuye.gtmoremachine.api.machine;

import cn.qiuye.gtmoremachine.api.capability.energy.IEnergyBindable;
import cn.qiuye.gtmoremachine.api.misc.wireless.energy.WirelessEnergyContainer;

import javax.annotation.Nullable;

public interface IWirelessEnergyContainerHolder extends IEnergyBindable {

    void setWirelessEnergyContainerCache(WirelessEnergyContainer container);

    WirelessEnergyContainer getWirelessEnergyContainerCache();

    @Nullable
    default WirelessEnergyContainer getWirelessEnergyContainer() {
        if (getUUID() != null && getWirelessEnergyContainerCache() == null) {
            WirelessEnergyContainer container = WirelessEnergyContainer.getOrCreateContainer(getUUID());
            setWirelessEnergyContainerCache(container);
        }
        return getWirelessEnergyContainerCache();
    }
}
