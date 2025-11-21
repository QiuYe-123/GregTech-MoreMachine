package cn.qiuye.gtmoremachine;

import cn.qiuye.gtmoremachine.api.GTMMValues;
import cn.qiuye.gtmoremachine.client.ClientProxy;
import cn.qiuye.gtmoremachine.common.CommonProxy;
import cn.qiuye.gtmoremachine.utils.FormattingUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod(GTmm.MOD_ID)
public class GTmm {

    public static final String MOD_ID = "gtmoremachine";
    public static final String MOD_NAME = "GTMoreMachine";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MOD_ID, "");

    public GTmm() {
        init();
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

    private static void init() {
        LOGGER.info("GTMoreMachine init!");
    }

    public static ResourceLocation id(String path) {
        if (path.isBlank()) {
            return TEMPLATE_LOCATION;
        }

        int i = path.indexOf(':');
        if (i > 0) {
            return ResourceLocation.tryParse(path);
        } else if (i == 0) {
            path = path.substring(i + 1);
        }
        // only convert it to camel_case if it has any uppercase to begin with
        if (FormattingUtil.hasUpperCase(path)) {
            path = FormattingUtil.toLowerCaseUnderscore(path);
        }
        return TEMPLATE_LOCATION.withPath(path);
    }

    /**
     * 是否在生产环境中运行
     *
     * @return if we're running in a production environment
     */
    public static boolean isProd() {
        return FMLLoader.isProduction();
    }

    /**
     * 是否不在生产环境中运行
     *
     * @return if we're not running in a production environment
     */
    public static boolean isDev() {
        return !isProd();
    }

    /**
     * 是否正在运行数据生成
     *
     * @return if we're running data generation
     */
    public static boolean isDataGen() {
        return FMLLoader.getLaunchHandler().isData();
    }

    /**
     * 一个友好的提醒：服务器实例仅在服务器端填充，因此请进行空/端检查！
     * A friendly reminder that the server instance is populated on the server side only, so null/side check it!
     *
     * @return 当前的Minecraft服务器实例 the current minecraft server instance
     */
    public static MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    /**
     * 检查指定mod是否已加载
     *
     * @param modId 要检查的mod ID the mod id to check for
     * @return 指定ID的mod是否已加载 if the mod whose id is {@code modId} is loaded or not
     */
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    /**
     * 对于异步操作使用此方法，否则使用 {@link GTmm isClientSide}
     * For async stuff use this, otherwise use {@link GTmm isClientSide}
     *
     * @return 当前线程是否为客户端线程 if the current thread is the client thread
     */
    public static boolean isClientThread() {
        return isClientSide() && Minecraft.getInstance().isSameThread();
    }

    /**
     * 游戏是否为<strong>物理</strong>客户端，例如不是专用服务器
     *
     * @return if the game is the <strong>PHYSICAL</strong> client, e.g. not a dedicated server.
     * @apiNote 不要使用此方法来检查你是否在服务器线程上执行端特定操作！它<strong>不</strong>适用于此。请使用 {@link #isClientThread()} 代替。
     *          Do not use this to check if you're currently on the server thread for side-specific actions!
     *          It does <strong>NOT</strong> work for that. Use {@link #isClientThread()} instead.
     * @see #isClientThread()
     */
    public static boolean isClientSide() {
        return FMLEnvironment.dist.isClient();
    }

    /**
     * 此检查在客户端和服务器上并不相同！
     * This check isn't the same for client and server!
     *
     * @return 在客户端上访问当前实例{@link net.minecraft.world.level.Level Level}是否安全，或者在服务器上访问任何level是否安全。
     *         if it's safe to access the current instance {@link net.minecraft.world.level.Level Level} on client or if
     *         it's safe to access any level on server.
     */
    public static boolean canGetServerLevel() {
        if (isClientSide()) {
            return Minecraft.getInstance().level != null;
        }
        var server = getMinecraftServer();
        return server != null &&
                !(server.isStopped() || server.isShutdown() || !server.isRunning() || server.isCurrentlySaving());
    }

    /**
     * 获取Minecraft实例目录的路径
     *
     * @return Minecraft实例目录的路径 the path to the minecraft instance directory
     */
    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static class Mods {

        public static boolean isAE2Loaded() {
            return isModLoaded(GTMMValues.MODID_APPENG);
        }

        public static boolean isFTBTeamsLoaded() {
            return isModLoaded(GTMMValues.MODID_FTB_TEAMS);
        }
    }
}
