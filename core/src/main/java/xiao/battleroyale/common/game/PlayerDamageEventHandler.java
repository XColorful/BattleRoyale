package xiao.battleroyale.common.game;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class PlayerDamageEventHandler implements IEventHandler {

    private PlayerDamageEventHandler() {}

    private static class PlayerDamageEventHandlerHolder {
        private static final PlayerDamageEventHandler INSTANCE = new PlayerDamageEventHandler();
    }

    public static  PlayerDamageEventHandler get() {
        return PlayerDamageEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return String.format("%s:PlayerDamageEventHandler", BattleRoyale.MOD_ID);
    }

    protected static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.LOWEST, true);
    }

    protected static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.LIVING_DAMAGE_EVENT, EventPriority.LOWEST, true);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.LIVING_DAMAGE_EVENT) {
            onLivingDamage((ILivingDamageEvent) event);
        } else {
            onReceiveWrongEvent(eventType);
        }
    }

    /**
     * 监听实体受到伤害事件
     * 作为游戏机制认可的伤害记录
     * @param event 实体受到伤害事件
     */
    private void onLivingDamage(ILivingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) {
            return;
        }
        IGameManager gameManager = BattleRoyale.getGameManager();
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(livingEntity.getUUID());
        if (gamePlayer == null) {
            return;
        }

        if (!GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) {
            BattleRoyale.LOGGER.debug("PlayerDamageEventHandler: GamePlayer {} is not in standing player list, canceled onLivingDamage", gamePlayer.getPlayerName());
            return;
        }

        if (event.isCanceled()) {
            BattleRoyale.LOGGER.debug("Detected a canceled LivingDamageEvent in game");
        } else {
            gameManager.onPlayerDamage(event, gamePlayer);
        }
    }
}
