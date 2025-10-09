package xiao.battleroyale.common.game.spawn;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.config.common.game.spawn.type.detail.AbstractDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractSimpleSpawner<T extends AbstractDetailEntry<T>> implements IGameSpawner {

    protected final String SPAWNER_KEY_TAG = "spawner";

    public final SpawnShapeType shapeType;
    public final Vec3 centerPos;
    public final Vec3 dimension;
    public final int preZoneCenterId;

    public final CommonDetailType detailType;
    protected final T detailEntry;

    protected boolean prepared = false;
    protected boolean finished = false;

    public AbstractSimpleSpawner(SpawnShapeType shapeType, Vec3 center, Vec3 dimension, int preZoneCenterId,
                                 CommonDetailType detailType, T detailEntry) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;
        this.preZoneCenterId = preZoneCenterId;
        this.detailType = detailType;
        this.detailEntry = detailEntry;
    }

    @Override
    public void init (Supplier<Float> random, int spawnPointsTotal) {
        Map<String, String> stringWriter = new HashMap<>();
        stringWriter.put(SpawnTypeTag.TYPE_NAME, getSpawnerTypeString());
        stringWriter.put(SpawnShapeTag.TYPE_NAME, shapeType.getName());
        stringWriter.put(SpawnShapeTag.CENTER, StringUtils.vectorToString(centerPos));
        stringWriter.put(SpawnShapeTag.DIMENSION, StringUtils.vectorToString(dimension));
        addSpawnDetailProperty();
        GameStatsManager.recordSpawnString(SPAWNER_KEY_TAG, stringWriter);
    }

    protected abstract String getSpawnerTypeString();

    protected abstract void addSpawnDetailProperty();

    @Override
    public boolean isReady() { return prepared; }

    @Override
    public boolean shouldTick() { return !finished; }

    @Override
    public void clear() {
        if (GameManager.get().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, reject to clear Spawner (type:{})", getSpawnerTypeString());
            return;
        }
        clearAfterGame();
        this.prepared = false;
        this.finished = false;
    }
    protected abstract void clearAfterGame();

    protected void onSpawnerError() {
        this.finished = true;
    }
}
