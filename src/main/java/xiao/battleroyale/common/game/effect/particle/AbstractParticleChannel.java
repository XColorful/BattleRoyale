package xiao.battleroyale.common.game.effect.particle;

import java.util.HashSet;
import java.util.Set;

public class AbstractParticleChannel<T> {


    public int channelCooldown;
    public final Set<T> particles = new HashSet<>();

    public AbstractParticleChannel() {
        ;
    }

    public void addParticle(T particleData, int channelCooldown) {
        if (channelCooldown > 0) {
            return;
        }
        this.channelCooldown += channelCooldown;
        particles.add(particleData);
    }

    public boolean shouldEnd() {
        return particles.isEmpty();
    }
}
