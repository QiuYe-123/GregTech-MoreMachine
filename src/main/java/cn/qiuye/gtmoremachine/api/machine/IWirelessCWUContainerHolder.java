package cn.qiuye.gtmoremachine.api.machine;

import cn.qiuye.gtmoremachine.api.capability.IBindable;
import cn.qiuye.gtmoremachine.api.misc.wireless.cwu.WirelessCWUContainer;

import javax.annotation.Nullable;

public interface IWirelessCWUContainerHolder extends IBindable {

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
