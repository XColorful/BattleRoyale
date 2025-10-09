package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.config.common.game.spawn.type.detail.AbstractDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;

public abstract class AbstractCommonSpawnEntry<T extends AbstractDetailEntry<T>> implements ISpawnEntry {

    // common
    public SpawnShapeType shapeType;
    public Vec3 centerPos;
    public Vec3 dimension;
    public int preZoneId;
    // detail
    public final CommonDetailType detailType;
    public final T detailEntry;

    public AbstractCommonSpawnEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                                    CommonDetailType detailType, T detailEntry) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;
        this.detailType = detailType;
        this.detailEntry = detailEntry;
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
