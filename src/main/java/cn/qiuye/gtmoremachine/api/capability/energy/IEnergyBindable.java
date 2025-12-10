package cn.qiuye.gtmoremachine.api.capability.energy;

import cn.qiuye.gtmoremachine.api.capability.IBindable;

public interface IEnergyBindable extends IBindable {

    default boolean Dimensional() {
        return false;
    }

    default boolean Capacity() {
        return false;
    }
}
