package xiao.battleroyale.common.effect.firework;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class PlayerTrackingFireworkTask {
    ServerLevel level;
    UUID playerUUID;
    int remainingAmount;
    int interval;
    int currentDelay;
    float vRange;
    float hRange;

    public PlayerTrackingFireworkTask(ServerLevel level, UUID playerUUID, int amount, int interval, float vRange, float hRange) {
        this.level = level;
        this.playerUUID = playerUUID;
        this.remainingAmount = amount;
        this.interval = interval;
        this.currentDelay = interval;
        this.vRange = vRange;
        this.hRange = hRange;
    }
}