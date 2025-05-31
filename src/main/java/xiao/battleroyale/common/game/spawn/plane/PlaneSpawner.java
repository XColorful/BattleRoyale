package xiao.battleroyale.common.game.spawn.plane;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.config.common.game.spawn.type.PlaneEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

public class PlaneSpawner implements IGameSpawner {

    // common
    private final SpawnShapeType shapeType;
    private final Vec3 centerPos;
    private final Vec3 dimension;
    // detail
    private final CommonDetailType detailType;
    private final double planeHeight;
    private final double planeSpeed;
    private boolean fixedReachTime;

    public PlaneSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                        CommonDetailType detailType,
                        PlaneEntry.DetailInfo detailInfo) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;

        this.detailType = detailType;
        this.planeHeight = detailInfo.planeHeight();
        this.planeSpeed = detailInfo.planeSpeed();
        this.fixedReachTime = detailInfo.fixedReachTime();
    }
}
