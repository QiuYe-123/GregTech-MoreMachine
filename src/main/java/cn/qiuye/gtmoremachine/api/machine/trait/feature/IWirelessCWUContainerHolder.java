package cn.qiuye.gtmoremachine.api.machine.trait.feature;

import cn.qiuye.gtmoremachine.api.capability.wireless.cwu.ICWUBindable;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import javax.annotation.Nullable;

public interface IWirelessCWUContainerHolder extends ICWUBindable {

    void setWirelessCWUContainerCache(WirelessCWUContainer container);

    WirelessCWUContainer getWirelessCWUContainerCache();

    @Nullable
    default WirelessCWUContainer getWirelessCWUContainer() {
        if (getUUID() != null && getWirelessCWUContainerCache() == null) {
            WirelessCWUContainer container = WirelessCWUContainer.getOrCreateContainer(getUUID());
            setWirelessCWUContainerCache(container);
        }
        return getWirelessCWUContainerCache();
    }
}
