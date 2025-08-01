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
     * 对非玩家无效
     * 通知队伍更新成员信息
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(LivingDamageEvent event) {

        LivingEntity damagedEntity = event.getEntity(); // 被攻击方
        DamageSource damageSource = event.getSource(); // 攻击方

        GamePlayer targetGamePlayer = GameManager.get().getGamePlayerByUUID(damagedEntity.getUUID());
        GamePlayer attackerGamePlayer = null;
        if (damageSource.getEntity() instanceof LivingEntity attackerEntity) {
            attackerGamePlayer = GameManager.get().getGamePlayerByUUID(attackerEntity.getUUID());
        }

        // 游戏玩家攻击非游戏玩家
        if (attackerGamePlayer != null && targetGamePlayer == null) {
            if (damagedEntity instanceof ServerPlayer) {
                event.setCanceled(true);
            }
        }
        // 非游戏玩家攻击游戏玩家
        else if (attackerGamePlayer == null && targetGamePlayer != null) {
            if (damageSource.getEntity() instanceof ServerPlayer) {
                event.setCanceled(true);
            }
        }
        // 游戏玩家之间的伤害
        else if (attackerGamePlayer != null && targetGamePlayer != null) {
            // 如果双方在同一队伍，且友伤关闭，则取消伤害
            if (attackerGamePlayer.getGameTeamId() == targetGamePlayer.getGameTeamId()) {
                if (!GameManager.get().getGameEntry().friendlyFire) {
                    event.setCanceled(true);
                }
            }

            // 通知队伍更新成员信息
            GameManager.get().notifyTeamChange(targetGamePlayer.getGameTeamId());
        }
    }
}