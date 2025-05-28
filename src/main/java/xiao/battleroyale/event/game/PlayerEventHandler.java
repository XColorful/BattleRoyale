package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent; // 玩家死亡事件
import net.minecraftforge.event.entity.player.PlayerEvent; // 玩家登录/登出事件
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.TeamManager;

/**
 * 监听玩家登出/登入
 * 监听击杀(死亡)事件，击倒机制，获取击杀着，通知计算游戏是否达到结束条件
 */
public class PlayerEventHandler {

    private static PlayerEventHandler instance;

    private PlayerEventHandler() {}

    public static PlayerEventHandler getInstance() {
        if (instance == null) {
            instance = new PlayerEventHandler();
        }
        return instance;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(getInstance());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(getInstance());
        instance = null;
    }

    /**
     * 监听玩家登录事件。
     * 当玩家登录时，通知TeamManager。
     * @param event 玩家登录事件
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            TeamManager.get().onPlayerLoggedIn(serverPlayer);
        }
    }

    /**
     * 监听玩家登出事件。
     * 当玩家登出时，通知TeamManager。
     * @param event 玩家登出事件
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            TeamManager.get().onPlayerLoggedOut(serverPlayer);
        }
    }

    /**
     * 监听实体死亡事件。
     * 当玩家死亡时，通知TeamManager处理。
     * @param event 实体死亡事件
     */
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 确保只有在游戏进行中才处理死亡事件
            if (!GameManager.get().isInGame()) {
                unregister();
                return;
            }
            TeamManager.get().onPlayerDeath(serverPlayer);

            // TODO: 击倒机制的实现需要更复杂的逻辑，例如判断伤害来源、玩家状态等
            // 如果有击倒机制，这里可能需要判断是真正的死亡还是击倒
            // event.isCanceled() 可以用于取消死亡事件，实现击倒效果
            // 获取击杀者：event.getSource().getEntity()
        }
    }
}
