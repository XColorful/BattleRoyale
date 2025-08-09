package xiao.battleroyale.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
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
     * 取消存活游戏玩家与非存活游戏玩家之间的伤害
     * 通知队伍更新成员信息
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(LivingDamageEvent event) {

        LivingEntity damagedEntity = event.getEntity(); // 被攻击方
        DamageSource damageSource = event.getSource(); // 攻击方

        GameManager gameManager = GameManager.get();

        GamePlayer targetGamePlayer = gameManager.hasStandingGamePlayer(damagedEntity.getUUID()) ? gameManager.getGamePlayerByUUID(damagedEntity.getUUID()) : null;
        if (targetGamePlayer != null && targetGamePlayer.isEliminated()) {
            targetGamePlayer = null;
        }
        GamePlayer attackerGamePlayer = null;
        if (damageSource.getEntity() instanceof LivingEntity attackerEntity) {
            attackerGamePlayer = gameManager.hasStandingGamePlayer(attackerEntity.getUUID()) ? gameManager.getGamePlayerByUUID(attackerEntity.getUUID()) : null;
            if (attackerGamePlayer != null && attackerGamePlayer.isEliminated()) {
                attackerGamePlayer = null;
            }
        }

        // 游戏玩家之间的伤害
        if (attackerGamePlayer != null && targetGamePlayer != null) {
            // 如果双方在同一队伍，且友伤关闭，则取消伤害
            if (attackerGamePlayer.getGameTeamId() == targetGamePlayer.getGameTeamId()) {
                if (!gameManager.getGameEntry().friendlyFire) {
                    event.setCanceled(true);
                }
            }
            // 通知队伍更新成员信息
            gameManager.notifyTeamChange(targetGamePlayer.getGameTeamId());
        }
        // 游戏玩家攻击非游戏玩家
        else if (attackerGamePlayer != null) {
            if (damagedEntity instanceof ServerPlayer) {
                event.setCanceled(true);
            }
        }
        // 非游戏玩家攻击游戏玩家
        else if (targetGamePlayer != null) {
            if (damageSource.getEntity() instanceof ServerPlayer) {
                event.setCanceled(true);
            }
        }
        // 非游戏玩家打非游戏玩家
    }
}