package xiao.battleroyale.api.game.effect.particle;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.sub.IConfigEntry;
import xiao.battleroyale.common.effect.particle.ParticleData;

public interface IParticleEntry extends IConfigEntry {

    ParticleData createParticleData(ServerLevel serverLevel);

    @Override
    @NotNull
    IParticleEntry copy();
}
