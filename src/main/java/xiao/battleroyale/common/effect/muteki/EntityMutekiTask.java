package xiao.battleroyale.common.effect.muteki;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class EntityMutekiTask {

    protected ServerLevel serverLevel;
    protected final long worldTime;
    protected UUID entityUUID;
    protected String name;
    protected int remainTime;
    protected boolean notice = false;

    public ServerLevel getServerLevel() { return serverLevel; }
    public long getWorldTime() { return worldTime; }
    public UUID getEntityUUID() { return entityUUID; }
    public String getName() { return name; }
    public int getRemainTime() { return remainTime; }
    public boolean isNotice() { return notice; }

    public EntityMutekiTask(ServerLevel serverLevel, UUID playerUUID, String name, int remainTime) {
        this.serverLevel = serverLevel;
        this.worldTime = serverLevel.getGameTime();
        this.entityUUID = playerUUID;
        this.name = name;
        this.remainTime = remainTime;
    }
}
