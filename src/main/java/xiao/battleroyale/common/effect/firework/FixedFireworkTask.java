package xiao.battleroyale.common.effect.firework;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class FixedFireworkTask extends AbstractFireworkTask {

    protected Vec3 initialPos;

    public Vec3 getInitialPos() { return initialPos; }

    public FixedFireworkTask(ServerLevel serverLevel, Vec3 initialPos, int amount, int interval, float vRange, float hRange) {
        super(serverLevel, amount, interval, vRange, hRange);
        this.initialPos = initialPos;
    }
}