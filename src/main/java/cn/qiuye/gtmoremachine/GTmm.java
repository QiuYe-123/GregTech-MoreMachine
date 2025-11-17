package cn.qiuye.gtmoremachine;

import cn.qiuye.gtmoremachine.client.ClientProxy;
import cn.qiuye.gtmoremachine.common.CommonProxy;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GTmm.MOD_ID)
public class GTmm {

    public static final String MOD_ID = "gtmoremachine";
    public static final String MOD_NAME = "GTMoreMachine";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public GTmm() {
        init();
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

    private static void init() {
        LOGGER.info("GTMoreMachine init!");
    }
}
