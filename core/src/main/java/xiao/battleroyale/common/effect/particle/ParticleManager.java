package xiao.battleroyale.common.effect.particle;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.effect.IEffectManager;
import xiao.battleroyale.event.effect.ParticleEventHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ParticleManager implements IEffectManager {

    private ParticleManager() {}

    private static class ParticleManagerHolder {
        private static final ParticleManager INSTANCE = new ParticleManager();
    }

    public static ParticleManager get() {
        return ParticleManagerHolder.INSTANCE;
    }

    private final Map<UUID, EntityParticleTask> entityParticles = new HashMap<>(); // entityUUID -> particleTask -> channel
    private final Map<String, FixedParticleChannel> fixedParticles = new HashMap<>(); // channelString -> channel
    private static boolean registered = false;

    public Map<UUID, EntityParticleTask> getEntityParticles() {
        return Collections.unmodifiableMap(entityParticles);
    }
    public Map<String, FixedParticleChannel> getFixedParticles() {
        return Collections.unmodifiableMap(fixedParticles);
    }

    public void onTick() {
        entityParticles.entrySet().removeIf(entry -> {
            EntityParticleTask task = entry.getValue();
            UUID entityUUID = task.entityUUID;
            AtomicReference<ServerLevel> serverLevel = new AtomicReference<>();
            AtomicReference<LivingEntity> livingEntity = new AtomicReference<>();
            task.channels.entrySet().removeIf(channelEntry -> {
                EntityParticleChannel channel = channelEntry.getValue();
                channel.channelCooldown--;
                channel.particles.removeIf(particleData -> {
                    if (--particleData.delayRemain > 0) {
                        return false;
                    }
                    if (serverLevel.get() != particleData.serverLevel) {
                        if (particleData.serverLevel == null) {
                            return true;
                        }
                        serverLevel.set(particleData.serverLevel);
                        livingEntity.set((LivingEntity) serverLevel.get().getEntity(entityUUID));
                    }
                    if (livingEntity.get() == null) {
                        return true;
                    }
                    // 生成玩家追踪粒子
                    Vec3 spawnPos = livingEntity.get().position();
                    particleData.spawnParticle(spawnPos);
                    particleData.delayRemain = particleData.particle.interval;
                    return ++particleData.finishedRepeat >= particleData.particle.repeat;
                });
                return channel.shouldEnd();
            });
            return task.shouldEnd();
        });

        fixedParticles.entrySet().removeIf(entry -> {
            FixedParticleChannel channel = entry.getValue();
            channel.channelCooldown--;
            channel.particles.removeIf(particleData -> {
                if (--particleData.delayRemain > 0) {
                    return false;
                }
                // 生成固定位置粒子
                particleData.spawnParticle(particleData.particlePos);
                particleData.delayRemain = particleData.particle.interval;
                return ++particleData.finishedRepeat >= particleData.particle.repeat;
            });
            return channel.shouldEnd();
        });

        if (shouldEnd()) {
            ParticleEventHandler.unregister();
            registered = false;
        }
    }

    public boolean addEntityParticle(UUID entityUUID, String channelKey, ParticleData particleData, int cooldown) {
        EntityParticleChannel channel = getOrCreateChannel(entityUUID, channelKey);
        if (channel.addParticle(particleData, cooldown)) {
            if (!registered) { // 降低大量添加粒子效果时的开销
                ParticleEventHandler.register();
                registered = true;
            }
            return true;
        }
        return false;
    }
    public boolean addFixedParticle(String channelKey, FixedParticleData particleData, int cooldown) {
        FixedParticleChannel channel = getOrCreateChannel(fixedParticles, channelKey);
        if (channel.addParticle(particleData, cooldown)) {
            if (!registered) { // 降低大量添加粒子效果时的开销
                ParticleEventHandler.register();
                registered = true;
            }
            return true;
        }
        return false;
    }

    private @NotNull EntityParticleTask getOrCreateEntityTask(UUID entityUUID) {
        return entityParticles.computeIfAbsent(entityUUID, e -> new EntityParticleTask(entityUUID));
    }
    private @NotNull FixedParticleChannel getOrCreateChannel(Map<String, FixedParticleChannel> channels, String channelKey) {
        return channels.computeIfAbsent(channelKey, e -> new FixedParticleChannel(channelKey));
    }
    private @NotNull EntityParticleChannel getOrCreateChannel(UUID entityUUID, String channelKey) {
        return getOrCreateEntityTask(entityUUID).channels.computeIfAbsent(channelKey, e -> new EntityParticleChannel());
    }

    @Override
    public void clear() {
        forceEnd();
    }
    public void clear(UUID entityUUID) {
        entityParticles.remove(entityUUID);
    }
    public void clear(UUID entityUUID, String channelKey) {
        EntityParticleTask task = entityParticles.get(entityUUID);
        if (task != null) {
            task.channels.remove(channelKey);
        }
    }
    public void clear(String channelKey) {
        fixedParticles.remove(channelKey);
    }

    @Override
    public void forceEnd() {
        entityParticles.clear();
        fixedParticles.clear();
    }

    @Override
    public boolean shouldEnd() {
        return entityParticles.isEmpty() && fixedParticles.isEmpty();
    }
}
