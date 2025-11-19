package cn.qiuye.gtmoremachine.common.data;

import cn.qiuye.gtmoremachine.common.registry.GTMMRegistration;

public class GTMMCovers {

    static {
        GTMMRegistration.GTMMREGISTRATE.creativeModeTab(() -> GTMMCreativeModeTabs.WIRELESS_TAB);
    }

    public static void init() {}
}
