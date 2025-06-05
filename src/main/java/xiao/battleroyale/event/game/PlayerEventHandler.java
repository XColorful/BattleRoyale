package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;

/**
 * 监听击杀(死亡)事件，击倒机制，获取击杀者，通知计算游戏是否达到结束条件
 */
public class PlayerEventHandler {

    private static PlayerEventHandler instance;

    private PlayerEventHandler() {}

    public static PlayerEventHandler get() {
        if (instance == null) {
            instance = new PlayerEventHandler();
        }
        return instance;
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        instance = null;
    }

    /**
     * 监听实体死亡事件。
     * 当玩家死亡时，通知TeamManager处理。
     * @param event 实体死亡事件
     */
    @SubscribeEvent
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
            GameManager.get().onPlayerDeath(gamePlayer);
            GameManager.get().addChangedTeamInfo(gamePlayer.getGameTeamId());

            // TODO: 击倒机制的实现需要更复杂的逻辑，例如判断伤害来源、玩家状态等
            // 如果有击倒机制，这里可能需要判断是真正的死亡还是击倒
            // event.isCanceled() 可以用于取消死亡事件，实现击倒效果
            // 获取击杀者：event.getSource().getEntity()
        }
    }
}
