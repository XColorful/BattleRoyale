package xiao.battleroyale.event.game;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.init.ModDamageTypes;

/**
 * 统计造成的伤害值
 */
public class StatsEventHandler {

    private StatsEventHandler() {}

    private static class StatsEventHandlerHolder {
        private static final StatsEventHandler INSTANCE = new StatsEventHandler();
    }

    public static StatsEventHandler get() {
        return StatsEventHandlerHolder.INSTANCE;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
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
