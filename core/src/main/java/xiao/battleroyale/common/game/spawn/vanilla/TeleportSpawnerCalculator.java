package xiao.battleroyale.common.game.spawn.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.util.ChatUtils;

import java.util.function.Supplier;

import static xiao.battleroyale.util.Vec3Utils.randomAdjustXZExpandY;
import static xiao.battleroyale.util.Vec3Utils.randomCircleXZ;

public class TeleportSpawnerCalculator {

    public static boolean calculateFixedPos(TeleportSpawner context, Supplier<Float> random, int spawnPointsTotal) {
        if (!context.fixedPos.isEmpty()) {
            int size = context.fixedPos.size();
            for (int i = 0; i < spawnPointsTotal; i++) {
                Vec3 basePos = context.fixedPos.get(i % size);
                context.spawnPos.add(randomAdjustXZExpandY(basePos, context.randomRange, random)); // 简单的二次偏移会导致落概率不均匀
            }
        } else {
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel != null) {
                ChatUtils.sendMessageToAllPlayers(serverLevel, "TeleportSpawner config error: no fixed position");
            }
            BattleRoyale.LOGGER.warn("GroundSpawner detailType is '{}', but has no fixedPos", CommonDetailType.FIXED.getName());
            return false;
        }
        return true;
    }

    public static boolean calculateRandomPos(TeleportSpawner context, Supplier<Float> random, int spawnPointsTotal) {
        switch (context.shapeType) {
            case CIRCLE -> {
                for (int i = 0; i < spawnPointsTotal; i++) {
                    Vec3 basePos = randomCircleXZ(context.centerPos, context.dimension, random);
                    context.spawnPos.add(randomAdjustXZExpandY(basePos, context.randomRange, random));
                }
            }
            case SQUARE, RECTANGLE -> {
                for (int i = 0; i < spawnPointsTotal; i++) {
                    Vec3 basePos = randomAdjustXZExpandY(context.centerPos, context.dimension, random);
                    context.spawnPos.add(randomAdjustXZExpandY(basePos, context.randomRange, random));
                }
            }
            default -> {
                ServerLevel serverLevel = GameManager.get().getServerLevel();
                if (serverLevel != null) {
                    ChatUtils.sendMessageToAllPlayers(serverLevel, "TeleportSpawner config error: unsupported shapeType");
                }
                BattleRoyale.LOGGER.warn("Unsupported SpawnShapeType in TeleportSpawner");
                return false;
            }
        }
        return true;
    }
}
