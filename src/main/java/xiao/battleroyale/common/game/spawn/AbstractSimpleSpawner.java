package xiao.battleroyale.common.game.spawn;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractSimpleSpawner implements IGameSpawner {

    protected final String SPAWNER_KEY_TAG = "spawner";

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
    public void init (Supplier<Float> random, int spawnPointsTotal) {
        Map<String, String> writer = new HashMap<>();
        writer.put(SpawnTypeTag.TYPE_NAME, getSpawnerTypeString());
        writer.put(SpawnShapeTag.TYPE_NAME, shapeType.getName());
        writer.put(SpawnShapeTag.CENTER, StringUtils.vectorToString(centerPos));
        writer.put(SpawnShapeTag.DIMENSION, StringUtils.vectorToString(dimension));
        addWriterDetailProperty();
        GameManager.get().recordSpawnString(SPAWNER_KEY_TAG, writer);
    }

    public abstract String getSpawnerTypeString();

    public abstract void addWriterDetailProperty();

    @Override
    public boolean isReady() { return prepared; }

    @Override
    public boolean shouldTick() { return !finished; }
}
