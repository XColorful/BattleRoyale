package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 统计造成的伤害值
 */
public class StatsEventHandler {

    private StatsEventHandler() {}

    private static class StatsEventHandlerHolder {
        private static final StatsEventHandler INSTANCE = new StatsEventHandler();
    }

    public static StatsEventHandler get() {
        return StatsEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }


    /**
     * 监听实体受到伤害事件
     * 统计玩家造成的伤害和受到的伤害
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRecordDamage(LivingDamageEvent event) {
        LivingEntity damagedEntity = event.getEntity();
        GamePlayer damagedGamePlayer = GameManager.get().getGamePlayerByUUID(damagedEntity.getUUID());
        if (damagedGamePlayer == null) {
            return;
        }

        StatsManager.get().onRecordDamage(damagedGamePlayer, event);
    }

    /**
     * 监听实体死亡事件
     * 统计倒地或击杀信息
     * @param event 实体死亡事件
     */
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            GamePlayer gamePlayer = GameManager.get().getGamePlayerByUUID(serverPlayer.getUUID());
            if (gamePlayer == null) {
                return;
            } else if (gamePlayer.isAlive()) { // 不死图腾等机制，取消了死亡事件，因此没有通知GameManager处理死亡逻辑，gamePlayer逻辑上仍然isAlive = true
                BattleRoyale.LOGGER.warn("onLivingDeath, gamePlayer {} (UUID: {}) is alive", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
                StatsManager.get().onRecordInstantRevive(gamePlayer, event);
                return;
            }

            if (!gamePlayer.isEliminated()) { // 未被淘汰则判定为倒地
                StatsManager.get().onRecordDown(gamePlayer, event);
            } else { // 淘汰
                StatsManager.get().onRecordKill(gamePlayer, event);
            }
        }
    }
}
