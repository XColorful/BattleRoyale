package xiao.battleroyale.common.effect.firework;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class PlayerTrackingFireworkTask extends AbstractFireworkTask{

    protected UUID playerUUID;

    public UUID getPlayerUUID() { return playerUUID; }

    public PlayerTrackingFireworkTask(ServerLevel serverLevel, UUID playerUUID, int amount, int interval, float vRange, float hRange) {
        super(serverLevel, amount, interval, vRange, hRange);
        this.playerUUID = playerUUID;
    }
}