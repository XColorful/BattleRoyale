package xiao.battleroyale.api.game.stats;

import net.minecraft.world.damagesource.DamageSource;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.common.game.team.GamePlayer;

public interface IGameEventStatsRecorder {

    void onRecordDamage(GamePlayer gamePlayer, DamageSource damageSource, float damage);

    void onRecordDamage(GamePlayer gamePlayer, ILivingDamageEvent livingDamageEvent);

    void onRecordInstantRevive(GamePlayer gamePlayer, ILivingDeathEvent event);

    void onRecordDown(GamePlayer gamePlayer, ILivingDeathEvent event);

    void onRecordKill(GamePlayer gamePlayer, ILivingDeathEvent event);
}
