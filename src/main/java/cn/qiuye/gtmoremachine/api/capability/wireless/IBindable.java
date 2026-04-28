package cn.qiuye.gtmoremachine.api.capability.wireless;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IBindable {

    @Nullable
    UUID getUUID();

    default boolean cover() {
        return false;
    }

    default boolean display() {
        return true;
    }
}
