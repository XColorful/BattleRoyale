package xiao.battleroyale.common.game.spawn.ground;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.config.common.game.spawn.type.GroundEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

import java.util.List;

public class GroundSpawner implements IGameSpawner {

    // common
    private final SpawnShapeType shapeType;
    private final Vec3 centerPos;
    private final Vec3 dimension;
    // detail
    private final CommonDetailType detailType;
    private final List<Vec3> fixedPos;
    private final boolean teamTogether;
    private final boolean findGround;
    private final double randomRange;

    public GroundSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                         CommonDetailType detailType,
                         GroundEntry.DetailInfo detailInfo) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;

        this.detailType = detailType;
        this.fixedPos = detailInfo.fixedPos();
        this.teamTogether = detailInfo.teamTogether();
        this.findGround = detailInfo.findGround();
        this.randomRange = detailInfo.randomRange();
    }
}
