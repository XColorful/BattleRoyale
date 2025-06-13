package xiao.battleroyale.common.game.effect.firework;

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
import xiao.battleroyale.event.game.FireworkEventHandler;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FireworkManager {

    private final List<FixedFireworkTask> fixedTasks = new ArrayList<>();
    private final List<PlayerTrackingFireworkTask> playerTrackingTasks = new ArrayList<>();

    private FireworkManager() {}

    private static class FireworkManagerHolder {
        private static final FireworkManager INSTANCE = new FireworkManager();
    }

    public static FireworkManager get() {
        return FireworkManagerHolder.INSTANCE;
    }

    public void onTick() {
        RandomSource random = null;
        if (!fixedTasks.isEmpty()) {
            random = fixedTasks.get(0).level.getRandom();
        } else if (!playerTrackingTasks.isEmpty()) {
            random = playerTrackingTasks.get(0).level.getRandom();
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
                    spawnFireworkAtLocation(task.level, task.initialPos, random, task.vRange, task.hRange);
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
                    ServerPlayer player = task.level.getServer().getPlayerList().getPlayer(task.playerUUID);
                    if (player != null) {
                        spawnFireworkAtLocation(task.level, player.position(), random, task.vRange, task.hRange);
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
    }

    public static void spawnFireworkAtLocation(@NotNull ServerLevel level, Vec3 centerPos, RandomSource random, float vRange, float hRange) {
        Vec3 spawnPos = Vec3Utils.randomCircleXZExpandY(centerPos, new Vec3(hRange, vRange, 0), random::nextFloat);
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworkNbt = fireworkStack.getOrCreateTagElement("Fireworks");
        ListTag explosionsList = new ListTag();

        CompoundTag explosionTag = new CompoundTag();
        explosionTag.putIntArray("Colors", ColorUtils.generateRandomColors(random, 1, 2));
        explosionTag.putByte("Type", (byte) random.nextInt(5)); // 0=small ball, 1=large ball, 2=star, 3=creeper, 4=burst
        explosionTag.putBoolean("Flicker", random.nextBoolean());
        explosionTag.putBoolean("Trail", random.nextBoolean());

        explosionsList.add(explosionTag);
        fireworkNbt.put("Explosions", explosionsList);
        fireworkNbt.putByte("Flight", (byte) (random.nextInt(3)));

        FireworkRocketEntity fireworkEntity = new FireworkRocketEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, fireworkStack);
        level.addFreshEntity(fireworkEntity);
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
        fixedTasks.clear();
        playerTrackingTasks.clear();
        FireworkEventHandler.unregister();
    }

    public void forceEnd() {
        clear();
    }

    public boolean shouldEnd() {
        return fixedTasks.isEmpty() && playerTrackingTasks.isEmpty();
    }
}