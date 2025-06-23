package xiao.battleroyale.common.effect.particle;

import java.util.HashSet;
import java.util.Set;

public class AbstractParticleChannel<T> {


    public int channelCooldown = 0; // 往通道添加粒子的冷却
    public final Set<T> particles = new HashSet<>();

    public AbstractParticleChannel() {
        ;
    }

    public boolean addParticle(T particleData, int channelCooldown) {
        if (this.channelCooldown > 0) {
            return false;
        }
        particles.add(particleData);
        this.channelCooldown += channelCooldown;
        return true;
    }

    public boolean shouldEnd() {
        return particles.isEmpty();
    }
}
