package xiao.battleroyale.event.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.server.utility.SurvivalLobby;

/**
 * 只用于实现生存模式大厅内无敌
 * 优先级设置为HiGHEST，确保在其他伤害处理前执行
 *  * 注册该事件默认开启大厅无敌
 */
public class SurvivalLobbyEventHandler {

    private static class SurvivalLobbyEventHandlerHolder {
        private static final SurvivalLobbyEventHandler INSTANCE = new SurvivalLobbyEventHandler();
    }

    public static SurvivalLobbyEventHandler get() {
        return SurvivalLobbyEventHandlerHolder.INSTANCE;
    }

    private SurvivalLobbyEventHandler() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        BattleRoyale.LOGGER.debug("SurvivalLobbyEventHandler registered");
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        BattleRoyale.LOGGER.debug("SurvivalLobbyEventHandler unregistered");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDamage(LivingDamageEvent event) {
        if (SurvivalLobby.get().canMuteki(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
