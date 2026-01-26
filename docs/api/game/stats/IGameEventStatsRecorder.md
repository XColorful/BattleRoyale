```java
package xiao.battleroyale.api.game.stats;

public interface IGameEventStatsRecorder {
    void onRecordDamage(GamePlayer gamePlayer, DamageSource damageSource, float damage);
    void onRecordDamage(GamePlayer gamePlayer, ILivingDamageEvent livingDamageEvent);
    void onRecordInstantRevive(GamePlayer gamePlayer, ILivingDeathEvent event);
    void onRecordDown(GamePlayer gamePlayer, ILivingDeathEvent event);
    void onRecordKill(GamePlayer gamePlayer, ILivingDeathEvent event);
}
```