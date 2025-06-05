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
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.init.ModDamageTypes;

/**
 * 统计造成的伤害值
 * 伤害数值调整
 */
public class DamageEventHandler {

    private static DamageEventHandler instance;

    private DamageEventHandler() {}

    public static DamageEventHandler get() {
        if (instance == null) {
            instance = new DamageEventHandler();
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
     * 取消游戏玩家与非游戏玩家之间的伤害
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity damagedEntity = event.getEntity();
        if (!(damagedEntity instanceof ServerPlayer targetPlayer)) {
            return;
        }
        DamageSource damageSource = event.getSource();
        if (!(damageSource.getEntity() instanceof ServerPlayer attackerPlayer)) {
            return;
        }

        GamePlayer targetGamePlayer = GameManager.get().getGamePlayerByUUID(targetPlayer.getUUID());
        boolean isTargetGamePlayer = targetGamePlayer != null;
        GamePlayer attackerGamePlayer = GameManager.get().getGamePlayerByUUID(attackerPlayer.getUUID());
        boolean isAttackerGamePlayer = attackerGamePlayer != null;

        if ((isAttackerGamePlayer && !isTargetGamePlayer) || (!isAttackerGamePlayer && isTargetGamePlayer)) {
            event.setCanceled(true);
            return;
        }

        int teamId = isTargetGamePlayer ? targetGamePlayer.getGameTeamId() : attackerGamePlayer.getGameTeamId();
        GameManager.get().addChangedTeamInfo(teamId);
    }

    /**
     * 监听实体受到伤害事件
     * 通知队伍更新成员信息
     * 统计玩家造成的伤害和受到的伤害
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRecordDamage(LivingDamageEvent event) {
        if (!GameManager.get().isInGame()) {
            unregister();
            return;
        }

        LivingEntity damagedEntity = event.getEntity();
        GamePlayer damagedGamePlayer = GameManager.get().getGamePlayerByUUID(damagedEntity.getUUID());
        if (damagedGamePlayer == null) {
            return;
        }


        DamageSource damageSource = event.getSource();
        float damageAmount = event.getAmount();
        damagedGamePlayer.addDamageTaken(damageAmount);
        if (damageSource.is(ModDamageTypes.SAFE_ZONE_DAMAGE) || damageSource.is(ModDamageTypes.UNSAFE_ZONE_DAMAGE)) {
            damagedGamePlayer.addZoneDamageTaken(damageAmount);
        }

        if (damageSource.getEntity() instanceof LivingEntity attackingEntity) {
            GamePlayer attackingGamePlayer = TeamManager.get().getGamePlayerByUUID(attackingEntity.getUUID());
            if (attackingGamePlayer != null) {
                if (damagedGamePlayer.getGameTeamId() != attackingGamePlayer.getGameTeamId()) { // 同队伍不计入伤害量，受伤照常记录
                    attackingGamePlayer.addDamageDealt(damageAmount);
                }
            }
        }
    }
}