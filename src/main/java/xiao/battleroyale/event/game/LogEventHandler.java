package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.team.TeamManager;

/**
 * 监听玩家登出/登入
 */
public class LogEventHandler {

    private static LogEventHandler instance;

    private LogEventHandler() {}

    public static LogEventHandler getInstance() {
        if (instance == null) {
            instance = new LogEventHandler();
        }
        return instance;
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(getInstance());
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(getInstance());
        instance = null;
    }

    /**
     * 监听玩家登录事件。
     * 当玩家登录时，通知TeamManager。
     * @param event 玩家登录事件
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            TeamManager.get().onPlayerLoggedIn(serverPlayer);
        }
    }

    /**
     * 监听玩家登出事件。
     * 当玩家登出时，通知TeamManager。
     * @param event 玩家登出事件
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            TeamManager.get().onPlayerLoggedOut(serverPlayer);
        }
    }
}
