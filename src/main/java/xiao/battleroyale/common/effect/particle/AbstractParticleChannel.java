package xiao.battleroyale.common.effect.particle;

import java.util.HashSet;
import java.util.Set;

public class AbstractParticleChannel<T> {


    public int channelCooldown;
    public final Set<T> particles = new HashSet<>();

    public AbstractParticleChannel() {
        ;
    }

    public boolean addParticle(T particleData, int channelCooldown) {
        if (channelCooldown > 0) {
            return false;
        }
        this.channelCooldown += channelCooldown;
        particles.add(particleData);
        return true;
    }

    public boolean shouldEnd() {
        return particles.isEmpty();
    }
}
