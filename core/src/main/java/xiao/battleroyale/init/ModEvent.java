package xiao.battleroyale.init;

import net.minecraft.server.MinecraftServer;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.init.IModEvent;

/**
 * 核心模块事件和服务逻辑的统一处理类。
 * 兼容层通过实现这些接口来触发核心功能。
 */
public class ModEvent implements IModEvent {

    private static final ModEvent INSTANCE = new ModEvent();

    public static ModEvent get() {
        return INSTANCE;
    }

    private static boolean reloaded = false;
    @Override
    public void onServerStarting(MinecraftServer server) {
        BattleRoyale.setStaticRegistries(server.registryAccess());
        BattleRoyale.setMinecraftServer(server);
        if (!reloaded) {
            BattleRoyale.LOGGER.debug("onServerStarting, reloadAllConfigs:");
            BattleRoyale.getModConfigManager().reloadAllConfigs();
            reloaded = true;
        }
    }

    @Override
    public void onServerStopping(MinecraftServer server) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        gameManager.onServerStopping();
        gameManager.getGameLootManager().awaitTerminationOnShutdown();
        BattleRoyale.setMinecraftServer(null);
    }
}