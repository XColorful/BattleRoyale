package xiao.battleroyale.common.effect.firework;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.effect.IEffectManager;
import xiao.battleroyale.event.effect.FireworkEventHandler;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FireworkManager implements IEffectManager {

    private FireworkManager() {}

    private static class FireworkManagerHolder {
        private static final FireworkManager INSTANCE = new FireworkManager();
    }

    public static FireworkManager get() {
        return FireworkManagerHolder.INSTANCE;
    }

    private final List<FixedFireworkTask> fixedTasks = new ArrayList<>();
    private final List<PlayerTrackingFireworkTask> playerTrackingTasks = new ArrayList<>();

    public List<FixedFireworkTask> getFixedTasks() { return fixedTasks; }
    public List<PlayerTrackingFireworkTask> getPlayerTrackingTasks() { return playerTrackingTasks; }

    public void onTick() {
        RandomSource random = null;
        if (!fixedTasks.isEmpty()) {
            random = fixedTasks.get(0).serverLevel.getRandom();
        } else if (!playerTrackingTasks.isEmpty()) {
            random = playerTrackingTasks.get(0).serverLevel.getRandom();
        }

        if (random == null) {
            return;
        }

        Iterator<FixedFireworkTask> fixedIterator = fixedTasks.iterator();
        while (fixedIterator.hasNext()) {
            FixedFireworkTask task = fixedIterator.next();
            task.currentDelay--;

            if (task.currentDelay <= 0) {
                if (task.remainingAmount > 0) {
                    spawnFixedFirework(task.serverLevel, task.initialPos, random, task.vRange, task.hRange);
                    task.remainingAmount--;
                    task.currentDelay = task.interval;
                } else {
                    fixedIterator.remove();
                }
            }
        }

        Iterator<PlayerTrackingFireworkTask> playerIterator = playerTrackingTasks.iterator();
        while (playerIterator.hasNext()) {
            PlayerTrackingFireworkTask task = playerIterator.next();
            task.currentDelay--;

            if (task.currentDelay <= 0) {
                if (task.remainingAmount > 0) {
                    ServerPlayer player = task.serverLevel.getServer().getPlayerList().getPlayer(task.playerUUID);
                    if (player != null) {
                        spawnPlayerFirework(task.serverLevel, player.position(), random, task.vRange, task.hRange);
                        task.remainingAmount--;
                        task.currentDelay = task.interval;
                    } else {
                        playerIterator.remove();
                    }
                } else {
                    playerIterator.remove();
                }
            }
        }

        if (shouldEnd()) {
            FireworkEventHandler.unregister();
        }
    }

    public static void spawnFireworkAtExactPos(@NotNull ServerLevel level, @NotNull Vec3 exactPos) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkNbt = fireworkStack.getOrCreateTagElement("Fireworks");
        ListTag explosionsList = new ListTag();

        CompoundTag explosionTag = new CompoundTag();
        explosionTag.putIntArray("Colors", ColorUtils.generateRandomColors(level.getRandom(), 1, 2));
        explosionTag.putByte("Type", (byte) level.getRandom().nextInt(5)); // 0=small ball, 1=large ball, 2=star, 3=creeper, 4=burst
        explosionTag.putBoolean("Flicker", level.getRandom().nextBoolean());
        explosionTag.putBoolean("Trail", level.getRandom().nextBoolean());

        explosionsList.add(explosionTag);
        fireworkNbt.put("Explosions", explosionsList);
        fireworkNbt.putByte("Flight", (byte) (level.getRandom().nextInt(3)));

        FireworkRocketEntity fireworkEntity = new FireworkRocketEntity(level, exactPos.x, exactPos.y, exactPos.z, fireworkStack);
        level.addFreshEntity(fireworkEntity);
    }

    public static void spawnFixedFirework(@NotNull ServerLevel level, @NotNull Vec3 centerPos, @NotNull RandomSource random, float vRange, float hRange) {
        Vec3 spawnPos = Vec3Utils.randomCircleXZExpandY(centerPos, new Vec3(hRange, vRange, 0), random::nextFloat);
        spawnFireworkAtExactPos(level, spawnPos);
    }

    /**
     * 文明烟花，安全燃放
     * 如果距离玩家水平距离小于1，则保证至少在2格高
     */
    public static void spawnPlayerFirework(@NotNull ServerLevel level, @NotNull Vec3 playerPos, @NotNull RandomSource random, float vRange, float hRange) {
        Vec3 spawnPos = Vec3Utils.randomCircleXZExpandY(playerPos, new Vec3(hRange, vRange, 0), random::nextFloat);

        double dx = spawnPos.x - playerPos.x;
        double dz = spawnPos.z - playerPos.z;
        double horizontalDistSq = dx * dx + dz * dz;
        if (horizontalDistSq < 1.0) {
            spawnPos = new Vec3(spawnPos.x, Math.max(spawnPos.y, playerPos.y + 2), spawnPos.z);
        }

        spawnFireworkAtExactPos(level, spawnPos);
    }

    public void addFixedPositionFireworkTask(ServerLevel level, Vec3 pos, int amount, int interval, float vRange, float hRange) {
        fixedTasks.add(new FixedFireworkTask(level, pos, amount, interval, vRange, hRange));
        FireworkEventHandler.register();
    }

    public void addPlayerTrackingFireworkTask(ServerLevel level, UUID playerUUID, int amount, int interval, float vRange, float hRange) {
        playerTrackingTasks.add(new PlayerTrackingFireworkTask(level, playerUUID, amount, interval, vRange, hRange));
        FireworkEventHandler.register();
    }

    public void clear() {
        forceEnd();
    }

    public void forceEnd() {
        fixedTasks.clear();
        playerTrackingTasks.clear();
        FireworkEventHandler.unregister();
    }

    public boolean shouldEnd() {
        return fixedTasks.isEmpty() && playerTrackingTasks.isEmpty();
    }
}