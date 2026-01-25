package xiao.battleroyale.api.effect.type;

import xiao.battleroyale.api.effect.IEffectSubManager;
import xiao.battleroyale.common.effect.particle.EntityParticleTask;
import xiao.battleroyale.common.effect.particle.FixedParticleChannel;
import xiao.battleroyale.common.effect.particle.FixedParticleData;
import xiao.battleroyale.common.effect.particle.ParticleData;

import java.util.Map;
import java.util.UUID;

public interface IParticleManager extends IEffectSubManager {

    boolean addEntityParticle(UUID entityUUID, String channelKey, ParticleData particleData, int cooldown);

    boolean addFixedParticle(String channelKey, FixedParticleData particleData, int cooldown);

    void clear(UUID entityUUID);

    void clear(UUID entityUUID, String channelKey);

    void clear(String channelKey);

    void onTick();

    Map<UUID, EntityParticleTask> getEntityParticles();
    Map<String, FixedParticleChannel> getFixedParticles();
}
