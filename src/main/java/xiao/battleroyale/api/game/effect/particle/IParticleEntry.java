package xiao.battleroyale.api.game.effect.particle;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.config.IConfigEntry;
import xiao.battleroyale.common.effect.particle.ParticleData;

public interface IParticleEntry extends IConfigEntry {

    ParticleData createParticleData(ServerLevel serverLevel);
}
