package xiao.battleroyale;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xiao.battleroyale.algorithm.AlgorithmFacade;
import xiao.battleroyale.api.algorithm.IAlgorithmApi;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.client.render.game.IClientGuiRenderer;
import xiao.battleroyale.api.client.render.game.IClientLevelRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.api.compat.tacz.ITaczEventRegister;
import xiao.battleroyale.api.compat.tacz.ITaczGunOperator;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.event.ICustomEventPoster;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.renderer.BlockModelRenderer;
import xiao.battleroyale.client.renderer.game.ClientGuiRenderer;
import xiao.battleroyale.client.renderer.game.ClientLevelRenderer;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.event.EventRegister;
import xiao.battleroyale.event.custom.CustomEventHandler;
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
    public record CompatApi(IJmApi jmApi, ITaczEventRegister taczEventRegister, ITaczGunOperator taczGunOperator) {}
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

        gameManager = GameManager.get();
        modConfigManager = ModConfigManager.getApi();
        if (mcSide.isClientSide()) {
            clientGameDataManager = ClientGameDataManager.get();
            clientGuiRenderer = ClientGuiRenderer.get();
            clientLevelRenderer = ClientLevelRenderer.get();
        }

        NetworkHandler.initialize(networkAdapter);
        NetworkHook.initialize(networkHook);
        EventRegister.initialize(eventRegister);
        CustomEventHandler.registerAll(getEventRegister());
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
    public static IAlgorithmApi getAlgorithmApi() {
        return AlgorithmFacade.get();
    }

    private static IGameManager gameManager;
    private static IModConfigManager modConfigManager;
    private static IClientGameDataManager clientGameDataManager;
    private static IClientGuiRenderer clientGuiRenderer;
    private static IClientLevelRenderer clientLevelRenderer;
    public static IGameManager getGameManager() {
        return BattleRoyale.gameManager;
    }
    public static IModConfigManager getModConfigManager() {
        return modConfigManager;
    }
    public static IClientGameDataManager getClientGameDataManager() {
        return BattleRoyale.clientGameDataManager;
    }
    public static IClientGuiRenderer getClientGuiRenderer() {
        return clientGuiRenderer;
    }
    public static IClientLevelRenderer getClientLevelRenderer() {
        return BattleRoyale.clientLevelRenderer;
    }
    /**
     * @deprecated 除非需要深度定制, 否则不应该调用
     */
    @Deprecated(forRemoval=false)
    public static void setGameManager(@NotNull IGameManager gameManager) {
        BattleRoyale.gameManager = gameManager;
    }
    @Deprecated(forRemoval = false)
    public static void setModConfigManager(@NotNull IModConfigManager modConfigManager) {
        BattleRoyale.modConfigManager = modConfigManager;
    }
    @Deprecated(forRemoval = false)
    public static void setClientGameDataManager(@NotNull IClientGameDataManager clientGameDataManager) {
        BattleRoyale.clientGameDataManager = clientGameDataManager;
    }
    @Deprecated(forRemoval = false)
    public static void setClientGuiRenderer(@NotNull IClientGuiRenderer clientGuiRenderer) {
        BattleRoyale.clientGuiRenderer = clientGuiRenderer;
    }
    @Deprecated(forRemoval = false)
    public static void setClientLevelRenderer(@NotNull IClientLevelRenderer clientLevelRenderer) {
        BattleRoyale.clientLevelRenderer = clientLevelRenderer;
    }
}
