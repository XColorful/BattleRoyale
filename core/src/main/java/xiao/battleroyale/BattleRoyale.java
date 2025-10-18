package xiao.battleroyale;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import xiao.battleroyale.algorithm.AlgorithmFacade;
import xiao.battleroyale.api.algorithm.IAlgorithmApi;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.event.ICustomEventPoster;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.client.renderer.BlockModelRenderer;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.event.EventRegister;
import xiao.battleroyale.network.NetworkHandler;
import xiao.battleroyale.network.NetworkHook;
import xiao.battleroyale.resource.ResourceLoader;

import java.util.Random;

public class BattleRoyale {
    public static final String MOD_ID = "battleroyale";
    public static final String MOD_NAME_SHORT = "cbr";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random COMMON_RANDOM = new Random();

    protected static boolean initialized;
    protected static McSide mcSide = McSide.CLIENT;
    protected static MinecraftServer minecraftServer;
    private static IRegistrarFactory registrarFactory;
    private static IMcRegistry mcRegistry;
    public record CompatApi(IJmApi jmApi) {}
    private static CompatApi compatApi;

    public static void init(McSide mcSide,
                            IRegistrarFactory factory, IMcRegistry mcRegistry,
                            INetworkAdapter networkAdapter, INetworkHook networkHook,
                            IEventRegister eventRegister,
                            IBlockModelRenderer blockModelRenderer,
                            CompatApi compatApi) {
        if (initialized) return;

        BattleRoyale.mcSide = mcSide;
        BattleRoyale.registrarFactory = factory;
        BattleRoyale.mcRegistry = mcRegistry;
        NetworkHandler.initialize(networkAdapter);
        NetworkHook.initialize(networkHook);
        EventRegister.initialize(eventRegister);
        BlockModelRenderer.initialize(blockModelRenderer);
        BattleRoyale.compatApi = compatApi;

        ModConfigManager.init(mcSide);
        GameManager.init(mcSide);

        ResourceLoader.INSTANCE.packType = mcSide.isClientSide() ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;

        initialized = true;
    }

    public static McSide getMcSide() {
        return mcSide;
    }
    public static IRegistrarFactory getRegistrarFactory() {
        if (registrarFactory == null) {
            throw new IllegalStateException("Registrar factory has not been initialized. Call init() first.");
        }
        return registrarFactory;
    }
    public static IMcRegistry getMcRegistry() {
        if (mcRegistry == null) {
            throw new IllegalStateException("Mc registry has not been initialized. Call init() first.");
        }
        return mcRegistry;
    }
    public static CompatApi getCompatApi() {
        if (compatApi == null) {
            throw new IllegalStateException("Compat api has not initialized. Call init() first.");
        }
        return compatApi;
    }
    public static void setMinecraftServer(MinecraftServer server) {
        minecraftServer = server;
    }
    public static MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }
    public static ICustomEventPoster getEventPoster() {
        return EventPoster.get();
    }
    public static ICustomEventRegister getEventRegister() {
        return EventRegister.get();
    }
    public static IGameManager getGameManager() {
        return GameManager.get();
    }
    public static IModConfigManager getModConfigManager() {
        return ModConfigManager.getApi();
    }
    public static IAlgorithmApi getAlgorithmApi() {
        return AlgorithmFacade.get();
    }
}
