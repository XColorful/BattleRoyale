package xiao.battleroyale.common.game.spawn.special;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.common.game.spawn.AbstractSimpleSpawner;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.config.common.game.spawn.type.PlaneEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.util.List;
import java.util.function.Supplier;

/**
 * 生成一个飞机
 */
public class PlaneSpawner extends AbstractSimpleSpawner {

    // common 在父类

    // detail
    private final CommonDetailType detailType;
    private final double planeHeight;
    private final double planeSpeed;
    private boolean fixedReachTime;

    private Entity monitoredPlane; // TODO 需要先完成飞机模型及实体

    public PlaneSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                        CommonDetailType detailType,
                        PlaneEntry.DetailInfo detailInfo) {
        super(shapeType, center, dimension);

        this.detailType = detailType;
        this.planeHeight = detailInfo.planeHeight();
        this.planeSpeed = detailInfo.planeSpeed();
        this.fixedReachTime = detailInfo.fixedReachTime();
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

        if (true) { // 监控的实体不存在或没有乘客
            // 立即清除实体，保证已加载（否则得 Loot 或者 GameManager 下一局清理）
            this.finished = true;
        }
    }

    @Override
    public void clear() {

    }
}
