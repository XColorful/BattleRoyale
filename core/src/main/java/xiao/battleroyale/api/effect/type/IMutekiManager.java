package xiao.battleroyale.api.effect.type;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.effect.IEffectSubManager;
import xiao.battleroyale.common.effect.muteki.EntityMutekiTask;

import java.util.Map;
import java.util.UUID;

public interface IMutekiManager extends IEffectSubManager {

    void addMutekiEntity(ServerLevel serverLevel, LivingEntity livingEntity, int duration);

    void addMutekiEntityNotify(ServerLevel serverLevel, ServerPlayer player, int duration);

    boolean clear(UUID uuid);

    void onTick();

    void notifyMutekiEnd(EntityMutekiTask task);

    boolean canMuteki(LivingEntity livingEntity);

    Map<UUID, EntityMutekiTask> getMutekiTasks();

    int getMaxMutekiTime();
    int getMaxMutekiTimeDefault();
    void setMaxMutekiTime(int time);
}
