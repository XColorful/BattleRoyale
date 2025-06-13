package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.spawn.SpawnManager;

/**
 * 只用于实现Lobby内无敌
 * 优先级设置为HIGHEST，确保在其他伤害处理前执行
 * 注册该事件默认开启大厅无敌
 */
public class LobbyEventHandler {

    private LobbyEventHandler() {}

    private static class LobbyEventHandlerHolder {
        private static final LobbyEventHandler INSTANCE = new LobbyEventHandler();
    }

    public static LobbyEventHandler get() {
        return LobbyEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (SpawnManager.get().canMuteki(serverPlayer)) {
                event.setCanceled(true);
            }
        }
    }
}