package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.GameManager;
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
     * 监听实体死亡事件，会被不死图腾取消
     * 当玩家死亡时，判断是否改为击倒
     * 当玩家死亡时，通知TeamManager处理
     * @param event 实体死亡事件
     */
    @SubscribeEvent(priority = EventPriority.LOW) // 不接收被不死图腾等取消的事件
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (!GameManager.get().isInGame()) {
                unregister();
                return;
            }
            GamePlayer gamePlayer = GameManager.get().getGamePlayerByUUID(serverPlayer.getUUID());
            if (gamePlayer == null) {
                return;
            }

            // TODO 击倒机制判断，现在默认直接死亡
            if (false) { // 改为倒地
                ;
            } else { // 死亡
                GameManager.get().onPlayerDeath(gamePlayer);
                GameManager.get().addChangedTeamInfo(gamePlayer.getGameTeamId());
            }
        }
    }
}