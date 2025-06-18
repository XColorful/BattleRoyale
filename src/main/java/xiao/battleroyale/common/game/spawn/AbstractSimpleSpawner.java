package xiao.battleroyale.common.game.spawn;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;

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
        Map<String, String> stringWriter = new HashMap<>();
        stringWriter.put(SpawnTypeTag.TYPE_NAME, getSpawnerTypeString());
        stringWriter.put(SpawnShapeTag.TYPE_NAME, shapeType.getName());
        stringWriter.put(SpawnShapeTag.CENTER, StringUtils.vectorToString(centerPos));
        stringWriter.put(SpawnShapeTag.DIMENSION, StringUtils.vectorToString(dimension));
        addSpawnDetailProperty();
        GameManager.get().recordSpawnString(SPAWNER_KEY_TAG, stringWriter);
    }

    protected abstract String getSpawnerTypeString();

    protected abstract void addSpawnDetailProperty();

    @Override
    public boolean isReady() { return prepared; }

    @Override
    public boolean shouldTick() { return !finished; }
}
