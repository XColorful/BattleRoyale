package xiao.battleroyale.init;

import net.minecraft.server.MinecraftServer;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.init.IModEvent;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.loot.GameLootManager;

/**
 * 核心模块事件和服务逻辑的统一处理类。
 * 兼容层通过实现这些接口来触发核心功能。
 */
public class ModEvent implements IModEvent {

    private static final ModEvent INSTANCE = new ModEvent();

    public static ModEvent get() {
        return INSTANCE;
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        BattleRoyale.setStaticRegistries(server.registryAccess());
        BattleRoyale.setMinecraftServer(server);
    }

    @Override
    public void onServerStopping(MinecraftServer server) {
        GameManager.get().onServerStopping();
        GameLootManager.get().awaitTerminationOnShutdown();
        BattleRoyale.setStaticRegistries(null);
        BattleRoyale.setMinecraftServer(null);
    }
}