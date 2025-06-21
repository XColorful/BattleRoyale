package xiao.battleroyale.api.game.effect.particle;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.IConfigEntry;
import xiao.battleroyale.common.game.effect.particle.ParticleData;

public interface IParticleEntry extends IConfigEntry {

    ParticleData createParticleData(ServerLevel serverLevel);
}
