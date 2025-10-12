package xiao.battleroyale.common.game.spawn.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.algorithm.Distribution;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static xiao.battleroyale.util.Vec3Utils.randomAdjustXZExpandY;
import static xiao.battleroyale.util.Vec3Utils.randomCircleXZ;

public class TeleportSpawnerCalculator {

    public static boolean calculateFixedPos(TeleportSpawner context, Supplier<Float> random, int spawnPointsTotal) {
        if (!context.fixedPos.isEmpty()) {
            List<Vec3> fixedPos = new ArrayList<>(context.fixedPos);
            // 打乱顺序
            if (context.needShuffle) {
                Collections.shuffle(fixedPos, BattleRoyale.COMMON_RANDOM);
            }
            // 循环添加，但不继承随机偏移
            int size = fixedPos.size();
            for (int i = 0; i < spawnPointsTotal; i++) {
                Vec3 basePos = fixedPos.get(i % size);
                context.spawnPos.add(randomAdjustXZExpandY(basePos, context.randomRange, random));
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
        // 本身就随机生成，不需要再打乱
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

    public static boolean calculatedDistributedPos(TeleportSpawner context, Supplier<Float> random, int spawnPointsTotal, int simulationCount) {
        if (simulationCount <= 0) {
            BattleRoyale.LOGGER.warn("TeleportSpawner config error: invalid simulationCount {}", simulationCount);
            return false;
        }

        List<Vec3> basePositions;
        switch (context.shapeType) {
            case CIRCLE -> {
                if (context.useGoldenSpiral) { // 黄金螺旋
                    basePositions = Distribution.GoldenSpiral.distributed(context.centerPos, context.dimension, simulationCount, context.allowOnBorder, context.globalShrinkRatio);
                } else { // 双圆心网格
                    basePositions = Distribution.CircleGrid.distributed(context.centerPos, context.dimension, simulationCount, context.allowOnBorder, context.globalShrinkRatio);
                }
            }
            case SQUARE, RECTANGLE -> {
                basePositions = Distribution.RectangleGrid.distributed(context.centerPos, context.dimension, simulationCount, context.allowOnBorder, context.globalShrinkRatio);
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

        // 打乱基准点
        if (context.needShuffle) {
            Collections.shuffle(basePositions, BattleRoyale.COMMON_RANDOM);
        }

        // 循环添加，但不继承随机偏移
        for (int i = 0; i < spawnPointsTotal; i++) {
            Vec3 basePos = basePositions.get(i % basePositions.size());
            context.spawnPos.add(randomAdjustXZExpandY(basePos, context.randomRange, random));
        }

        return true;
    }
}
