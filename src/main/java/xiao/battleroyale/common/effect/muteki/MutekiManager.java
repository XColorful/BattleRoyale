package xiao.battleroyale.common.effect.muteki;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.effect.IEffectManager;
import xiao.battleroyale.event.effect.MutekiEventHandler;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MutekiManager implements IEffectManager {

    private MutekiManager() {}

    private static class MutekiManagerHolder {
        private static final MutekiManager INSTANCE = new MutekiManager();
    }

    public static MutekiManager get() {
        return MutekiManagerHolder.INSTANCE;
    }

    public static final int MAX_MUTEKI_TIME = 20 * 10;
    private final Map<UUID, EntityMutekiTask> mutekiTasks = new HashMap<>();

    public void onTick() {
        mutekiTasks.entrySet().removeIf(entry -> {
            EntityMutekiTask task = entry.getValue();
            if (--task.remainTime <= 0 || task.level == null) {
                notifyMutekiEnd(task);
                return true;
            } else {
                return false;
            }
        });

        if (shouldEnd()) {
            MutekiEventHandler.unregister();
        }
    }

    public void notifyMutekiEnd(EntityMutekiTask task) {
        if (task.notice && task.level != null) {
            ServerPlayer player = (ServerPlayer) task.level.getEntity(task.entityUUID);
            if (player != null) {
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.muteki_end").withStyle(ChatFormatting.YELLOW));
            }
        }
        BattleRoyale.LOGGER.info("LivingEntity {} (UUID:{}) muteki time finished", task.name, task.entityUUID);
    }

    public boolean canMuteki(LivingEntity livingEntity) {
        return mutekiTasks.containsKey(livingEntity.getUUID());
    }

    public void addMutekiEntity(ServerLevel serverLevel, LivingEntity livingEntity, int duration) {
        addAndGetTask(serverLevel, livingEntity, duration);
    }
    
    public void addMutekiEntityNotify(ServerLevel serverLevel, ServerPlayer player, int duration) {
        EntityMutekiTask task = addAndGetTask(serverLevel, player, duration);
        task.notice = true;
    }
    
    private EntityMutekiTask addAndGetTask(ServerLevel serverLevel, LivingEntity livingEntity, int duration) {
        UUID uuid = livingEntity.getUUID();
        EntityMutekiTask task;
        duration = Math.min(duration, MAX_MUTEKI_TIME);
        if (mutekiTasks.containsKey(uuid)) {
            task = mutekiTasks.get(uuid);
            task.level = serverLevel;
            task.entityUUID = uuid;
            if (duration > task.remainTime) {
                task.remainTime = duration;
            }
        } else {
            task = new EntityMutekiTask(serverLevel, uuid, livingEntity.getName().getString(), duration);
            mutekiTasks.put(uuid, task);
        }
        MutekiEventHandler.register();
        return task;
    }

    @Override
    public void clear() {
        new ArrayList<>(mutekiTasks.keySet()).forEach(this::clear);
        MutekiEventHandler.unregister();
    }

    public boolean clear(UUID uuid) {
        EntityMutekiTask task = mutekiTasks.remove(uuid);
        if (task != null) {
            notifyMutekiEnd(task);
            return true;
        }
        return false;
    }

    @Override
    public void forceEnd() {
        mutekiTasks.clear();
        MutekiEventHandler.unregister();
    }

    @Override
    public boolean shouldEnd() {
        return mutekiTasks.isEmpty();
    }
}
