package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.common.game.spawn.special.PlaneSpawner;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class PlaneEntry implements ISpawnEntry {

    // common
    private final SpawnShapeType shapeType;
    private final Vec3 centerPos;
    private final Vec3 dimension;
    // detail
    private final CommonDetailType detailType;
    private final DetailInfo detailInfo;

    public record DetailInfo(double planeHeight,
                             double planeSpeed,
                             boolean fixedReachTime) {}

    public PlaneEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                      CommonDetailType detailType,
                      DetailInfo detailInfo) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;

        this.detailType = detailType;
        this.detailInfo = detailInfo;
    }

    @Override
    public String getType() {
        return SpawnTypeTag.SPAWN_TYPE_PLANE;
    }

    @Override
    public IGameSpawner createGameSpawner() {
        return new PlaneSpawner(shapeType, centerPos, dimension, detailType, detailInfo);
    }

    @Override
    public JsonObject toJson() {
        // common
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SpawnTypeTag.TYPE_NAME, getType());
        jsonObject.addProperty(SpawnShapeTag.TYPE_NAME, shapeType.getName());
        jsonObject.addProperty(SpawnShapeTag.CENTER, StringUtils.vectorToString(centerPos));
        jsonObject.addProperty(SpawnShapeTag.DIMENSION, StringUtils.vectorToString(dimension));

        jsonObject.addProperty(SpawnDetailTag.TYPE_NAME, detailType.getName());

        // detail
        jsonObject.addProperty(SpawnDetailTag.PLANE_HEIGHT, this.detailInfo.planeHeight);
        jsonObject.addProperty(SpawnDetailTag.PLANE_SPEED, this.detailInfo.planeSpeed);
        jsonObject.addProperty(SpawnDetailTag.PLANE_FIXED_TIME, this.detailInfo.fixedReachTime);

        return jsonObject;
    }

    @Nullable
    public static PlaneEntry fromJson(JsonObject jsonObject) {
        // common
        SpawnShapeType shapeType = SpawnShapeType.fromName(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.TYPE_NAME, ""));
        if (shapeType == null) {
            BattleRoyale.LOGGER.info("Unknown shapeType in PlaneEntry, skipped");
            return null;
        }
        Vec3 center = StringUtils.parseVectorString(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.CENTER, ""));
        Vec3 dimension = StringUtils.parseVectorString(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.DIMENSION, ""));
        if (center == null || dimension == null) {
            BattleRoyale.LOGGER.info("Invalid center or dimension in PlaneEntry, skipped");
            return null;
        }

        CommonDetailType detailType = CommonDetailType.fromName(JsonUtils.getJsonString(jsonObject, SpawnDetailTag.TYPE_NAME, ""));
        if (detailType == null) {
            BattleRoyale.LOGGER.info("Unknown detailType for GroundEntry, skipped");
            return null;
        }

        // detail
        double height = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.PLANE_HEIGHT, 0);
        double speed = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.PLANE_SPEED, 1);
        boolean fixedTime = JsonUtils.getJsonBoolean(jsonObject, SpawnDetailTag.PLANE_FIXED_TIME, false);

        return new PlaneEntry(shapeType, center, dimension,
                detailType,
                new DetailInfo(height, speed, fixedTime)
        );
    }
}
