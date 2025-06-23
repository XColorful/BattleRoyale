package xiao.battleroyale.common.effect.particle;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.effect.particle.ParticleDetailEntry;

/**
 * 固定位置生成的粒子任务
 */
public class FixedParticleData extends ParticleData {

    public final Vec3 particlePos;

    public FixedParticleData(ServerLevel serverLevel, ParticleDetailEntry detailEntry, Vec3 particlePos) {
        super(serverLevel, detailEntry);
        this.particlePos = particlePos;
    }
}
