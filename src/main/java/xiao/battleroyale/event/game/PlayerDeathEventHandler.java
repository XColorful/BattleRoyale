package xiao.battleroyale.event.game;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 监听击杀(死亡)事件，击倒机制，获取击杀者，通知计算游戏是否达到结束条件
 */
public class PlayerDeathEventHandler {

    private PlayerDeathEventHandler() {}

    private static class PlayerEventHandlerHolder {
        private static final PlayerDeathEventHandler INSTANCE = new PlayerDeathEventHandler();
    }

    public static PlayerDeathEventHandler get() {
        return PlayerEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    /**
     * 监听实体死亡事件，会被不死图腾或PlayerRevive取消
     * 当玩家死亡时，判断是否改为击倒
     * 当玩家死亡时，通知TeamManager处理
     * @param event 实体死亡事件
     */
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true) // 接收被不死图腾或PlayerRevive取消的事件
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
            BattleRoyale.LOGGER.debug("PlayerDeathEventHandler: GamePlayer {} is not in standing player list, canceled onLivingDeath", gamePlayer.getPlayerName());
            return;
        }

        if (event.isCanceled()) { // 被不死图腾或PlayerRevive取消，GameManager内部检查是图腾还是倒地
            BattleRoyale.LOGGER.debug("Detected a canceled LivingDeathEvent in game");
            gameManager.onPlayerDown(gamePlayer, livingEntity);
        } else { // 死亡
            gameManager.onPlayerDeath(gamePlayer);
        }
    }
}