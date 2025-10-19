package xiao.battleroyale.common.effect.boost;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.effect.IEffectManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.event.handler.effect.BoostEventHandler;

import java.util.*;

public class BoostManager implements IEffectManager {

    private BoostManager() {}

    private static class BoostManagerHolder {
        private static final BoostManager INSTANCE = new BoostManager();
    }

    public static BoostManager get() {
        return BoostManagerHolder.INSTANCE;
    }

    private static final int SYNC_FREQUENCY = 20 * 5; // 客户端本地会递减Boost，不用频繁通知
    private static int HEAL_COOLDOWN = 160;
    public static void setHealCooldown(int cooldown) { HEAL_COOLDOWN = cooldown; }
    private static int EFFECT_COOLDOWN = 20;
    public static void setEffectCooldown(int cooldown) { EFFECT_COOLDOWN = cooldown; }

    private final Map<UUID, BoostData> boostData = new HashMap<>();

    public int syncFrequency() { return SYNC_FREQUENCY; }
    public int healCooldown() { return HEAL_COOLDOWN; }
    public int healCooldownDefault() { return 160; }
    public int effectCooldown() { return EFFECT_COOLDOWN; }
    public int effectCooldownDefault() { return 20; }

    public Map<UUID, BoostData> getBoostData() { return boostData; }
    public BoostData getBoostData(UUID uuid) { return boostData.get(uuid); }

    /**
     * 消耗boost，给予效果
     */
    public void onTick() {
        boostData.entrySet().removeIf(entry -> {
            BoostData data = entry.getValue();
            int boost = data.dropBoost();
            if (boost <= 0) {
                return true;
            }
            int boostLevel = BoostData.getBoostLevel(boost);
            // 回血
            if (--data.healCooldown <= 0) {
                if (data.serverLevel == null
                        || !(data.serverLevel.getEntity(data.uuid) instanceof LivingEntity livingEntity)) {
                    return true;
                }
                double healAmount = 0;
                switch (boostLevel) {
                    case BoostData.BOOST_LV4 -> healAmount = 0.8F;
                    case BoostData.BOOST_LV3 -> healAmount = 0.6F;
                    case BoostData.BOOST_LV2 -> healAmount = 0.4F;
                    case BoostData.BOOST_LV1 -> healAmount = 0.2F;
                }
                livingEntity.heal((float) healAmount);
                data.healCooldown = HEAL_COOLDOWN;
            }
            // 加速
            if (--data.effectCooldown <= 0) {
                if (data.serverLevel == null
                        || !(data.serverLevel.getEntity(data.uuid) instanceof LivingEntity livingEntity)) {
                    return true;
                }
                int boostEffect = boostLevel - 3;
                if (boostEffect >= 0) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_COOLDOWN, boostEffect, false, false));
                }
                data.effectCooldown = EFFECT_COOLDOWN;
            }
            // 同步消息
            if (--data.syncCooldown <= 0) {
                GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(data.uuid);
                if (gamePlayer != null) {
                    GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
                }
                data.syncCooldown = SYNC_FREQUENCY;
            }
            return false;
        });

        if (shouldEnd()) {
            BoostEventHandler.unregister();
        }
    }

    /**
     * 为 LivingEntity 添加能量条
     * 通常是使用物品时触发，立即更新冷却
     * 检查是否为GamePlayer并通知GameManager同步消息
     */
    public void addBoost(UUID entityUUID, int amount, ServerLevel serverLevel) {
        BoostData data = getOrCreateData(entityUUID, serverLevel);
        int preLevel = BoostData.getBoostLevel(data.boost());
        int curLevel = BoostData.getBoostLevel(data.addBoost(amount));
        if (preLevel < curLevel) {
            data.healCooldown = 0;
            data.effectCooldown = 0;
        }
        // 通知立即更新
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(entityUUID);
        if (gamePlayer != null) {
            GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
        }
        BoostEventHandler.register();
    }

    public int getBoost(UUID entityUUID) {
        BoostData data = boostData.get(entityUUID);
        if (data != null) {
            return data.boost();
        } else {
            return 0;
        }
    }

    @NotNull
    private BoostData getOrCreateData(UUID entityUUID, ServerLevel serverLevel) {
        BoostData data = boostData.computeIfAbsent(entityUUID, e -> new BoostData(e, serverLevel));
        data.serverLevel = serverLevel;
        return data;
    }

    @Override
    public void clear() {
        forceEnd();
    }

    public void clear(UUID entityUUID) {
        BoostData data = boostData.remove(entityUUID);
        if (data != null) {
            GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(data.uuid);
            if (gamePlayer != null) {
                GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            }
        }
    }

    @Override
    public void forceEnd() {
        // 通知所有队伍立即更新
        boostData.entrySet().removeIf(entry -> {
            BoostData data = entry.getValue();
            GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(data.uuid);
            if (gamePlayer != null) {
                GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            }
            return true;
        });

        boostData.clear();
    }

    @Override
    public boolean shouldEnd() {
        return boostData.isEmpty();
    }
}
