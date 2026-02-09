package xiao.battleroyale.common.game;

import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 监听击杀(死亡)事件，击倒机制，获取击杀者，通知计算游戏是否达到结束条件
 */
public class PlayerDeathEventHandler implements IEventHandler {

    private PlayerDeathEventHandler() {}

    private static class PlayerEventHandlerHolder {
        private static final PlayerDeathEventHandler INSTANCE = new PlayerDeathEventHandler();
    }

    public static PlayerDeathEventHandler get() {
        return PlayerEventHandlerHolder.INSTANCE;
    }

    @Override public String getEventHandlerName() {
        return "PlayerDeathEventHandler";
    }

    protected static void register() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.LIVING_DEATH_EVENT, EventPriority.LOW, true);
    }

    protected static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(get(), EventType.LIVING_DEATH_EVENT, EventPriority.LOW, true);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.LIVING_DEATH_EVENT) {
            onLivingDeath((ILivingDeathEvent) event);
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
    /**
     * 监听实体死亡事件，会被不死图腾或PlayerRevive取消
     * 当玩家死亡时，判断是否改为击倒
     * 当玩家死亡时，通知TeamManager处理
     * @param event 实体死亡事件
     */
    private void onLivingDeath(ILivingDeathEvent event) { // 接收被不死图腾或PlayerRevive取消的事件
        LivingEntity livingEntity = event.getEntity(); // 兼容以后生物作为人机玩家
        if (livingEntity == null) {
            return;
        }
        IGameManager gameManager = BattleRoyale.getGameManager();
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(livingEntity.getUUID());
        if (gamePlayer == null) {
            return;
        }

        if (!GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) {
            BattleRoyale.LOGGER.debug("PlayerDeathEventHandler: GamePlayer {} is not in standing player list, canceled onLivingDeath", gamePlayer.getPlayerName());
            return;
        }

        if (event.isCanceled()) { // 被不死图腾或PlayerRevive取消，GameManager内部检查是图腾还是倒地
            BattleRoyale.LOGGER.debug("Detected a canceled LivingDeathEvent in game");
            gameManager.onPlayerDown(event, gamePlayer, livingEntity);
        } else { // 死亡
            gameManager.onPlayerDeath(event, gamePlayer);
        }
    }
}