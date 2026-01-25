package xiao.battleroyale.api.effect.type;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.effect.IEffectSubManager;
import xiao.battleroyale.common.effect.boost.BoostData;

import java.util.Map;
import java.util.UUID;

public interface IBoostManager extends IEffectSubManager {

    void addBoost(UUID entityUUID, int amount, ServerLevel serverLevel);

    int getBoost(UUID entityUUID);

    void clear(UUID entityUUID);

    void onTick();

    Map<UUID, BoostData> getBoostData();
    BoostData getBoostData(UUID uuid);

    int syncFrequency();
    int healCooldown();
    int healCooldownDefault();
    int effectCooldown();
    int effectCooldownDefault();

    void setHealCooldown(int cooldown);
    void setEffectCooldown(int cooldown);
}
