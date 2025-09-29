package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;

public abstract class AbstractCommonSpawnEntry implements ISpawnEntry {

    // common
    protected final SpawnShapeType shapeType;
    protected final Vec3 centerPos;
    protected final Vec3 dimension;
    protected int preZoneId;

    public AbstractCommonSpawnEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;
    }

    @Override
    public void addPreZoneId(int zoneId) {
        this.preZoneId = zoneId;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SpawnTypeTag.TYPE_NAME, getType());
        jsonObject.addProperty(SpawnShapeTag.TYPE_NAME, shapeType.getName());
        jsonObject.addProperty(SpawnShapeTag.CENTER, StringUtils.vectorToString(centerPos));
        jsonObject.addProperty(SpawnShapeTag.DIMENSION, StringUtils.vectorToString(dimension));
        return jsonObject;
    }
}
