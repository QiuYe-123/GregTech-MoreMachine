package cn.qiuye.gtmoremachine.api.capability.wireless.energy;

import cn.qiuye.gtmoremachine.api.capability.wireless.IBindable;

public interface IEnergyBindable extends IBindable {

    default boolean Dimensional() {
        return false;
    }

    default boolean Capacity() {
        return false;
    }
}
