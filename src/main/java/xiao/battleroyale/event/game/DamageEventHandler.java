package xiao.battleroyale.event.game;

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

    public static DamageEventHandler getInstance() {
        if (instance == null) {
            instance = new DamageEventHandler();
        }
        return instance;
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(getInstance());
    }

    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(getInstance());
        instance = null;
    }

    /**
     * 监听实体受到伤害事件。
     * 用于统计玩家造成的伤害和受到的伤害。
     * @param event 实体受到伤害事件
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDamage(LivingDamageEvent event) {
        if (!GameManager.get().isInGame()) {
            unregister();
            return;
        }

        LivingEntity damagedEntity = event.getEntity();
        GamePlayer damagedGamePlayer = TeamManager.get().getGamePlayerByUUID(damagedEntity.getUUID());
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
                attackingGamePlayer.addDamageDealt(damageAmount);
            }
        }
    }
}