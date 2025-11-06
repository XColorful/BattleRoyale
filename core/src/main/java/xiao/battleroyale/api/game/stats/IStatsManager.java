package xiao.battleroyale.api.game.stats;

import net.minecraft.world.damagesource.DamageSource;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.Map;

public interface IStatsManager extends IGameSubManager {

    boolean shouldRecordStats();

    void onRecordIntGamerule(Map<String, Integer> intGameruleWriter);

    void onRecordBoolGamerule(Map<String, Boolean> boolGameruleWriter);

    void onRecordDoubleGamerule(Map<String, Double> doubleGameruleWriter);

    void onRecordStringGamerule(Map<String, String> stringGameruleWriter);

    void onRecordSpawnInt(String key, Map<String, Integer> spawnIntWriter);

    void onRecordSpawnBool(String key, Map<String, Boolean> spawnBoolWriter);

    void onRecordSpawnDouble(String key, Map<String, Double> spawnDoubleWriter);

    void onRecordSpawnString(String key, Map<String, String> spawnStringWriter);

    void onRecordZoneInt(int zoneId, Map<String, Integer> zoneIntWriter);

    void onRecordZoneBool(int zoneId, Map<String, Boolean> zoneBoolWriter);

    void onRecordZoneDouble(int zoneId, Map<String, Double> zoneDoubleWriter);

    void onRecordZoneString(int zoneId, Map<String, String> zoneStringWriter);

    void onRecordDamage(GamePlayer gamePlayer, DamageSource damageSource, float damage);

    void onRecordDamage(GamePlayer gamePlayer, ILivingDamageEvent livingDamageEvent);

    void onRecordInstantRevive(GamePlayer gamePlayer, ILivingDeathEvent event);

    void onRecordDown(GamePlayer gamePlayer, ILivingDeathEvent event);

    void onRecordKill(GamePlayer gamePlayer, ILivingDeathEvent event);
}
