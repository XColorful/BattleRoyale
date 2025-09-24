package xiao.battleroyale.event.game;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
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
        BattleRoyale.LOGGER.debug("Unregistered StatsEventHandler");
    }


    /**
     * 监听实体受到伤害事件
     * 统计玩家造成的伤害和受到的伤害
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRecordDamage(LivingDamageEvent event) {
        LivingEntity damagedEntity = event.getEntity();
        GamePlayer damagedGamePlayer = GameTeamManager.getGamePlayerByUUID(damagedEntity.getUUID());
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
        LivingEntity livingEntity = event.getEntity(); // 兼容以后生物作为人机玩家
        if (livingEntity == null) {
            return;
        }
        GameManager gameManager = GameManager.get();
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(livingEntity.getUUID());
        if (gamePlayer == null) {
            return;
        }

        if (!GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) {
            BattleRoyale.LOGGER.debug("StatsEventHandler: GamePlayer {} is not in standing player list, canceled onLivingDeath", gamePlayer.getPlayerName());
            return;
        }

        if (gamePlayer.isAlive()) { // GamePlayer.isAlive不代表Entity.isAlive
            BattleRoyale.LOGGER.debug("onLivingDeath, gamePlayer {} (UUID: {}) is alive", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
            StatsManager.get().onRecordInstantRevive(gamePlayer, event);
        } else if (!gamePlayer.isEliminated()) { // 非Alive但未被淘汰则判定为倒地
            StatsManager.get().onRecordDown(gamePlayer, event);
        } else { // 淘汰
            StatsManager.get().onRecordKill(gamePlayer, event);
        }
    }
}
