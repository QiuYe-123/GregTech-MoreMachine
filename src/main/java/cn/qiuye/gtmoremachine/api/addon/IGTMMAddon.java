package cn.qiuye.gtmoremachine.api.addon;

import java.math.BigInteger;

import static cn.qiuye.gtmoremachine.utils.BigIntegerUtils.big_integer_max_kong;

@SuppressWarnings("unused")
public interface IGTMMAddon {

    /**
     * this addon's Mod id.
     *
     * @return the Mod ID this addon uses for content.
     */
    String addonModId();

    default int addonTier() {
        return 0;
    };

    default BigInteger getCapacityComponentBlock(int tier, boolean isapacity) {
        if (isapacity) {
            return big_integer_max_kong.multiply(BigInteger.valueOf((long) tier * tier));
        } else {
            return BigInteger.ZERO;
        }
    };
}
