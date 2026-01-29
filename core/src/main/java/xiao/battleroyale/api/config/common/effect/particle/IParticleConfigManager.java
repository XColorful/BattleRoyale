package xiao.battleroyale.api.config.common.effect.particle;

import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;

public interface IParticleConfigManager<T extends IParticleSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.PARTICLE;
    }
}
