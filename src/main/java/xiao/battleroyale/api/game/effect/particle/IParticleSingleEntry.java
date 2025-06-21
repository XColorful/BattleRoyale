package xiao.battleroyale.api.game.effect.particle;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.IConfigSingleEntry;
import xiao.battleroyale.common.game.effect.particle.ParticleData;

public interface IParticleSingleEntry extends IConfigSingleEntry {

    ParticleData createParticleData(ServerLevel serverLevel);
}
