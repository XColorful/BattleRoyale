package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.spawn.SpawnManager;

/**
 * 只用于实现Lobby内无敌
 * 优先级设置为HIGHEST，确保在其他伤害处理前执行
 * 注册该事件默认开启大厅无敌
 */
public class LobbyEventHandler {

    private static class LobbyEventHandlerHolder {
        private static final LobbyEventHandler INSTANCE = new LobbyEventHandler();
    }

    public static LobbyEventHandler get() {
        return LobbyEventHandlerHolder.INSTANCE;
    }

    private LobbyEventHandler() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        BattleRoyale.LOGGER.debug("LobbyEventHandler registered");
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        BattleRoyale.LOGGER.debug("LobbyEventHandler unregistered");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDamage(LivingDamageEvent event) {
        if (SpawnManager.get().canMuteki(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}