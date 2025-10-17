package xiao.battleroyale.event.handler.game;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.event.EventRegister;

/**
 * 统计造成的伤害值
 */
public class StatsEventHandler implements IEventHandler {

    private StatsEventHandler() {}

    private static class StatsEventHandlerHolder {
        private static final StatsEventHandler INSTANCE = new StatsEventHandler();
    }

    public static StatsEventHandler get() {
        return StatsEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "StatsEventHandler";
    }

    public static void register() {
        EventRegister.register(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.LOWEST, true);
        EventRegister.register(get(), EventType.LIVING_DEATH_EVENT, EventPriority.LOWEST, true);
    }

    public static void unregister() {
        EventRegister.unregister(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.LOWEST, true);
        EventRegister.unregister(get(), EventType.LIVING_DEATH_EVENT, EventPriority.LOWEST, true);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        switch (eventType) {
            case LIVING_DAMAGE_EVENT -> onRecordDamage((ILivingDamageEvent) event);
            case LIVING_DEATH_EVENT -> onLivingDeath((ILivingDeathEvent) event);
            default -> BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }

    /**
     * 监听实体受到伤害事件
     * 统计玩家造成的伤害和受到的伤害
     * @param event 实体受到伤害事件
     */
    private void onRecordDamage(ILivingDamageEvent event) {
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
    public void onLivingDeath(ILivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity(); // 兼容以后生物作为人机玩家
        if (livingEntity == null) {
            return;
        }

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
