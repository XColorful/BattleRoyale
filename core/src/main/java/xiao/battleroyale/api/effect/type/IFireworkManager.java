package xiao.battleroyale.api.effect.type;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.effect.IEffectSubManager;
import xiao.battleroyale.common.effect.firework.FixedFireworkTask;
import xiao.battleroyale.common.effect.firework.PlayerTrackingFireworkTask;

import java.util.List;
import java.util.UUID;

public interface IFireworkManager extends IEffectSubManager {

    void addFixedPositionFireworkTask(ServerLevel level, Vec3 pos, int amount, int interval, float vRange, float hRange);

    void addPlayerTrackingFireworkTask(ServerLevel level, UUID playerUUID, int amount, int interval, float vRange, float hRange);

    void onTick();

    List<FixedFireworkTask> getFixedTasks();
    List<PlayerTrackingFireworkTask> getPlayerTrackingTasks();
}
