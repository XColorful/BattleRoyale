package xiao.battleroyale.common.game.effect.particle;

import xiao.battleroyale.api.game.effect.IEffectManager;

public class ParticleManager implements IEffectManager {

    private ParticleManager() {}

    private static class ParticleManagerHolder {
        private static final ParticleManager INSTANCE = new ParticleManager();
    }

    public static ParticleManager get() {
        return ParticleManagerHolder.INSTANCE;
    }

    @Override
    public void clear() {

    }

    @Override
    public void forceEnd() {

    }

    @Override
    public boolean shouldEnd() {
        return false;
    }
}
