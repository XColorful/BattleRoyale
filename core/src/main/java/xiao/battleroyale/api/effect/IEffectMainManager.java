package xiao.battleroyale.api.effect;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.effect.type.IBoostManager;
import xiao.battleroyale.api.effect.type.IFireworkManager;
import xiao.battleroyale.api.effect.type.IMutekiManager;
import xiao.battleroyale.api.effect.type.IParticleManager;

public interface IEffectMainManager {

    boolean setBoostManager(@NotNull IBoostManager boostManager);
    boolean setFireworkManager(@NotNull IFireworkManager fireworkManager);
    boolean setMutekiManager(@NotNull IMutekiManager mutekiManager);
    boolean setParticleManager(@NotNull IParticleManager particleManager);

    IBoostManager getBoostManager();
    IFireworkManager getFireworkManager();
    IMutekiManager getMutekiManager();
    IParticleManager getParticleManager();
}
