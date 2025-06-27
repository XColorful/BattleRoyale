package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 伤害数值调整
 */
public class DamageEventHandler {

    private DamageEventHandler() {}

    private static class DamageEventHandlerHolder {
        private static final DamageEventHandler INSTANCE = new DamageEventHandler();
    }

    public static DamageEventHandler get() {
        return DamageEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    /**
     * 监听实体受到伤害事件
     * 取消游戏玩家与非游戏玩家之间的伤害
     * 通知队伍更新成员信息
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity damagedEntity = event.getEntity();
        if (!(damagedEntity instanceof ServerPlayer targetPlayer)) { // 受伤方为玩家
            return;
        }
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getEntity() instanceof ServerPlayer attackerPlayer)) { // 攻击方为玩家
            return;
        }

        GamePlayer targetGamePlayer = GameManager.get().getGamePlayerByUUID(targetPlayer.getUUID());
        boolean isTargetGamePlayer = targetGamePlayer != null;
        GamePlayer attackerGamePlayer = GameManager.get().getGamePlayerByUUID(attackerPlayer.getUUID());
        boolean isAttackerGamePlayer = attackerGamePlayer != null;

        if ((isAttackerGamePlayer && !isTargetGamePlayer) // 非游戏玩家打游戏玩家
                || (!isAttackerGamePlayer && isTargetGamePlayer)) { // 游戏玩家打非游戏玩家
            event.setCanceled(true);
            return;
        }

        if (isTargetGamePlayer) { // 被攻击方
            int teamId = targetGamePlayer.getGameTeamId();
            GameManager.get().notifyTeamChange(teamId);
        }
    }
}