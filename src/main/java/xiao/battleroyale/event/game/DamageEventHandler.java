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
        DamageSource damageSource = event.getSource();

        boolean isTargetGamePlayer = false;
        boolean isAttackerGamePlayer = false;
        if (damageSource.getEntity() instanceof LivingEntity attackerEntity) {
            GamePlayer attackerGamePlayer = GameManager.get().getGamePlayerByUUID(attackerEntity.getUUID());
            if (attackerGamePlayer != null) {
                isAttackerGamePlayer = true;
            }
        }
        GamePlayer targetGamePlayer = GameManager.get().getGamePlayerByUUID(damagedEntity.getUUID());
        if (targetGamePlayer != null) {
            isTargetGamePlayer = true;
        }

        if (isAttackerGamePlayer && !isTargetGamePlayer) { // 游戏玩家打非游戏玩家
            event.setCanceled(true);
        } else if (!isAttackerGamePlayer && isTargetGamePlayer) { // 非游戏玩家打游戏玩家
            if (damageSource.getEntity() instanceof ServerPlayer) { // 需要保证是玩家
                event.setCanceled(true);
            }
        } else if (isTargetGamePlayer) {
            GameManager.get().notifyTeamChange(targetGamePlayer.getGameTeamId());
        }
    }
}