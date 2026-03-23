package xiao.battleroyale;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xiao.battleroyale.algorithm.AlgorithmFacade;
import xiao.battleroyale.api.algorithm.IAlgorithmApi;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import xiao.battleroyale.api.client.render.IClientRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.api.compat.tacz.ITaczEventRegister;
import xiao.battleroyale.api.compat.tacz.ITaczGunOperator;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.effect.IEffectManager;
import xiao.battleroyale.api.event.ICustomEventPoster;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.api.event.register.RegisterManagerEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.init.ISelectorRegistry;
import xiao.battleroyale.api.init.registry.IRegistrarFactory;
import xiao.battleroyale.api.loot.ICommonInventoryManager;
import xiao.battleroyale.api.loot.ICommonLootManager;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.api.network.INetworkAdapter;
import xiao.battleroyale.api.network.INetworkHook;
import xiao.battleroyale.api.server.IServerManager;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.renderer.BlockModelRenderer;
import xiao.battleroyale.client.renderer.ClientRenderer;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.loot.CommonInventoryManager;
import xiao.battleroyale.common.loot.CommonLootManager;
import xiao.battleroyale.common.server.ServerManager;
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
    private static HolderLookup.Provider STATIC_REGISTRIES;
    protected static McSide mcSide = McSide.CLIENT;
    protected static MinecraftServer minecraftServer;
    private static IRegistrarFactory registrarFactory;
    private static ISelectorRegistry selectorRegistry;
    private static IMcRegistry mcRegistry;
    public record CompatApi(IJmApi jmApi, ITaczEventRegister taczEventRegister, ITaczGunOperator taczGunOperator) {}
    private static CompatApi compatApi;

    public static void init(McSide mcSide,
                            IRegistrarFactory factory, ISelectorRegistry selectorRegistry, IMcRegistry mcRegistry,
                            INetworkAdapter networkAdapter, INetworkHook networkHook,
                            IEventRegister eventRegister,
                            IBlockModelRenderer blockModelRenderer,
                            CompatApi compatApi) {
        if (initialized) return;

        BattleRoyale.mcSide = mcSide;
        BattleRoyale.registrarFactory = factory;
        BattleRoyale.selectorRegistry = selectorRegistry;
        BattleRoyale.mcRegistry = mcRegistry;

        // 最早可使用的事件机制
        EventRegister.initialize(eventRegister);
        BattleRoyale.eventRegister = EventRegister.get();
        eventPoster = EventPoster.get();

        NetworkHandler.initialize(networkAdapter);
        NetworkHook.initialize(networkHook);
        BlockModelRenderer.initialize(blockModelRenderer);
        BattleRoyale.compatApi = compatApi;
        Object ignored = getEventPoster();

        modConfigManager = ModConfigManager.getApi();
        ModConfigManager.init(mcSide);

        setGameManagerInternal(GameManager.get());
        GameManager.init(mcSide);

        effectManager = EffectManager.get();
        serverManager = ServerManager.get();
        commonLootManager = CommonLootManager.get();
        commonInventoryManager = CommonInventoryManager.get();
        if (mcSide.isClientSide()) {
            clientGameDataManager = ClientGameDataManager.get();
            setClientRendererInternal(ClientRenderer.get());
        }
        EffectManager.init(mcSide);
        ServerManager.init(mcSide);
        CommonLootManager.init(mcSide);
        CommonInventoryManager.init(mcSide);
        if (mcSide.isClientSide()) {
            ClientGameDataManager.init(mcSide);
            ClientRenderer.init(mcSide);
        }

        CustomEventHandler.registerAll(getEventRegister());

        ResourceLoader.INSTANCE.packType = mcSide.isClientSide() ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;

        initialized = true;
    }

    public static void setStaticRegistries(@Nullable HolderLookup.Provider STATIC_REGISTRIES) {
        BattleRoyale.STATIC_REGISTRIES = STATIC_REGISTRIES;
    }
    public static @Nullable HolderLookup.Provider getStaticRegistries() {
        if (STATIC_REGISTRIES == null) {
            LOGGER.warn("HolderLookup.Provider is being accessed too early or has not been set!");
            return STATIC_REGISTRIES;
        }
        return STATIC_REGISTRIES;
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
    public static ISelectorRegistry getSelectorRegistry() {
        if (selectorRegistry == null) {
            throw new IllegalStateException("Selector registry has not been initialized. Call init() first.");
        }
        return selectorRegistry;
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
    public static IAlgorithmApi getAlgorithmApi() {
        return AlgorithmFacade.get();
    }

    private static ICustomEventRegister eventRegister;
    private static ICustomEventPoster eventPoster;
    private static IGameManager gameManager;
    private static IModConfigManager modConfigManager;
    private static IEffectManager effectManager;
    private static IServerManager serverManager;
    private static ICommonLootManager commonLootManager;
    private static ICommonInventoryManager commonInventoryManager;
    private static IClientGameDataManager clientGameDataManager;
    private static IClientRenderer clientRenderer;
    public static ICustomEventRegister getEventRegister() {
        return BattleRoyale.eventRegister != null ? BattleRoyale.eventRegister : EventRegister.get(); // 在模组加载前就能触发
    }
    public static ICustomEventPoster getEventPoster() {
        return BattleRoyale.eventPoster != null ? BattleRoyale.eventPoster : EventPoster.get();
    }
    public static IGameManager getGameManager() {
        return BattleRoyale.gameManager;
    }
    public static IModConfigManager getModConfigManager() {
        return modConfigManager;
    }
    public static IEffectManager getEffectManager() {
        return effectManager;
    }
    public static IServerManager getServerManager() {
        return serverManager;
    }
    public static ICommonLootManager getCommonLootManager() {
        return commonLootManager;
    }
    public static ICommonInventoryManager getCommonInventoryManager() {
        return commonInventoryManager;
    }
    public static IClientGameDataManager getClientGameDataManager() {
        return BattleRoyale.clientGameDataManager;
    }
    public static IClientRenderer getClientRenderer() {
        return BattleRoyale.clientRenderer;
    }
    /**
     * @deprecated 除非需要深度定制, 否则不应该调用
     */
    @Deprecated(forRemoval=false)
    public static void setGameManager(@NotNull IGameManager gameManager) {
        setGameManagerInternal(gameManager);
    }
    @Deprecated(forRemoval = false)
    public static void setModConfigManager(@NotNull IModConfigManager modConfigManager) {
        BattleRoyale.modConfigManager = modConfigManager;
    }
    @Deprecated(forRemoval = false)
    public static void setEffectManager(@NotNull IEffectManager effectManager) {
        BattleRoyale.effectManager = effectManager;
    }
    @Deprecated(forRemoval = false)
    public static void setServerManager(@NotNull IServerManager serverManager) {
        BattleRoyale.serverManager = serverManager;
    }
    @Deprecated(forRemoval = false)
    public static void setCommonLootManager(@NotNull ICommonLootManager commonLootManager) {
        BattleRoyale.commonLootManager = commonLootManager;
    }
    @Deprecated(forRemoval = false)
    public static void setCommonInventoryManager(@NotNull ICommonInventoryManager commonInventoryManager) {
        BattleRoyale.commonInventoryManager = commonInventoryManager;
    }
    @Deprecated(forRemoval = false)
    public static void setClientGameDataManager(@NotNull IClientGameDataManager clientGameDataManager) {
        BattleRoyale.clientGameDataManager = clientGameDataManager;
    }
    @Deprecated(forRemoval = false)
    public static void setClientRenderer(@NotNull IClientRenderer clientRenderer) {
        setClientRendererInternal(clientRenderer);
    }

    // 跟IGameSubManager同样的机制
    private static void setGameManagerInternal(@NotNull IGameManager gameManager) {
        if (BattleRoyale.gameManager != null) BattleRoyale.gameManager.unregisterGameEventHandler();
        BattleRoyale.gameManager = gameManager;
        gameManager.registerGameEventHandler();
    }
    // 跟IClientSubRenderer同样的机制
    private static void setClientRendererInternal(@NotNull IClientRenderer clientRenderer) {
        if (BattleRoyale.clientRenderer != null) BattleRoyale.clientRenderer.unregisterRenderEventHandler();
        BattleRoyale.clientRenderer = clientRenderer;
        clientRenderer.registerRenderEventHandler();
    }

    public static boolean registerManager(@Nullable CommandSourceStack source, String protocol) {
        if (BattleRoyale.gameManager.isInGame()) return false;
        return getEventPoster().postCustomEvent(new RegisterManagerEvent(source, protocol));
    }
}
