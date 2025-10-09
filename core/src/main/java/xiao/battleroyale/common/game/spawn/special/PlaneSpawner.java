package xiao.battleroyale.common.game.spawn.special;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.common.game.spawn.AbstractSimpleSpawner;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.PlaneDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 生成一个飞机
 */
public class PlaneSpawner extends AbstractSimpleSpawner<PlaneDetailEntry> {

    // common 在父类

    // detail
    protected final double planeHeight;
    protected final double planeSpeed;
    protected boolean fixedReachTime;

    protected Entity trackingPlaneEntity; // TODO 需要先完成飞机模型及实体

    public PlaneSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension, int zoneId,
                        CommonDetailType detailType,
                        PlaneDetailEntry detailEntry) {
        super(shapeType, center, dimension, zoneId, detailType, detailEntry);

        this.planeHeight = this.detailEntry.planeHeight;
        this.planeSpeed = this.detailEntry.planeSpeed;
        this.fixedReachTime = this.detailEntry.fixedReachTime;
    }

    @Override
    public void init(Supplier<Float> random, int spawnPointsTotal) {
        this.prepared = false;

        switch (detailType) {
            case FIXED -> {
                ;
            }
            case RANDOM -> {
                ;
            }
        }

        this.prepared = true;
    }

    @Override
    public String getSpawnerTypeString() {
        return SpawnTypeTag.SPAWN_TYPE_PLANE;
    }

    @Override
    public void addSpawnDetailProperty() {
        Map<String, String> stringWriter = new HashMap<>();
        stringWriter.put(SpawnDetailTag.TYPE_NAME, detailType.getName());
        GameStatsManager.recordSpawnString(SPAWNER_KEY_TAG, stringWriter);

        Map<String, Boolean> boolWriter = new HashMap<>();
        boolWriter.put(SpawnDetailTag.PLANE_FIXED_TIME, fixedReachTime);
        GameStatsManager.recordSpawnBool(SPAWNER_KEY_TAG, boolWriter);

        Map<String, Double> doubleWriter = new HashMap<>();
        doubleWriter.put(SpawnDetailTag.PLANE_HEIGHT, planeHeight);
        doubleWriter.put(SpawnDetailTag.PLANE_SPEED, planeSpeed);
        GameStatsManager.recordSpawnDouble(SPAWNER_KEY_TAG, doubleWriter);
    }

    /**
     * 第1tick将玩家传送到飞机上并骑乘
     * @param gameTime 当前游戏时间
     * @param gameTeam 当前存活的队伍列表
     */
    @Override
    public void tick(int gameTime, List<GameTeam> gameTeam) {
        switch (detailType) {
            case FIXED -> {
                ;
            }
            case RANDOM -> {
                ;
            }
        }

        if (trackingPlaneEntity == null) { // 监控的实体不存在或没有乘客
            // 立即清除实体，保证已加载（否则得 Loot 或者 GameManager 下一局清理）
            this.finished = true;
        }
    }

    @Override
    public void clearAfterGame() {
        trackingPlaneEntity = null;
    }
}
