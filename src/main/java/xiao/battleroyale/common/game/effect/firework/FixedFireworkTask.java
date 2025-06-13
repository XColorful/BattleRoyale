package xiao.battleroyale.common.game.effect.firework;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class FixedFireworkTask {
    ServerLevel level;
    Vec3 initialPos;
    int remainingAmount;
    int interval;
    int currentDelay;
    float vRange;
    float hRange;

    public FixedFireworkTask(ServerLevel level, Vec3 initialPos, int amount, int interval, float vRange, float hRange) {
        this.level = level;
        this.initialPos = initialPos;
        this.remainingAmount = amount;
        this.interval = interval;
        this.currentDelay = interval;
        this.vRange = vRange;
        this.hRange = hRange;
    }
}