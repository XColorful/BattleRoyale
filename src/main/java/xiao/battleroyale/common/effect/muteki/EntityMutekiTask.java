package xiao.battleroyale.common.effect.muteki;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class EntityMutekiTask {
    ServerLevel level;
    UUID entityUUID;
    String name;
    int remainTime;
    boolean notice = false;

    public EntityMutekiTask(ServerLevel level, UUID playerUUID, String name, int remainTime) {
        this.level = level;
        this.entityUUID = playerUUID;
        this.name = name;
        this.remainTime = remainTime;
    }
}
