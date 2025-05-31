package xiao.battleroyale.common.game.spawn;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

public abstract class AbstractSimpleSpawner implements IGameSpawner {

    protected final SpawnShapeType shapeType;
    protected final Vec3 centerPos;
    protected final Vec3 dimension;

    protected boolean prepared = false;
    protected boolean finished = false;

    public AbstractSimpleSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;
    }

    @Override
    public boolean isReady() { return prepared; }

    @Override
    public boolean shouldTick() { return !finished; }
}
