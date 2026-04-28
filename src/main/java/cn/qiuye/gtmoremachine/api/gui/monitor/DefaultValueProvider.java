package cn.qiuye.gtmoremachine.api.gui.monitor;

public interface DefaultValueProvider<T extends Enum<T>> {

    /**
     * 获取默认枚举项
     *
     * @return 枚举类的默认枚举项
     */
    T getDefaultValue();
}
