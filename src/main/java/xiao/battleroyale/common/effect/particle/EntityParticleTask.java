package xiao.battleroyale.common.effect.particle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 追踪玩家生成的粒子任务
 */
public class EntityParticleTask {

    public final UUID entityUUID;
    public final Map<String, EntityParticleChannel> channels = new HashMap<>();

    public EntityParticleTask(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public boolean shouldEnd() {
        return channels.isEmpty();
    }
}