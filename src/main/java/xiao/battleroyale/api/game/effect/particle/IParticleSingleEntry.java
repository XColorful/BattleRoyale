package xiao.battleroyale.api.game.effect.particle;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.IConfigSingleEntry;
import xiao.battleroyale.common.effect.particle.FixedParticleData;
import xiao.battleroyale.common.effect.particle.ParticleData;

public interface IParticleSingleEntry extends IConfigSingleEntry {

    ParticleData createParticleData(ServerLevel serverLevel);

    FixedParticleData createParticleData(ServerLevel serverLevel, Vec3 fixedPos);
}
