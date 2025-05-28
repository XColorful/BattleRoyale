package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;

public class PlaneEntry implements ISpawnEntry {

    // common
    private final SpawnShapeType shapeType;
    private final Vec3 centerPos;
    private final Vec3 dimension;
    private final CommonDetailType detailType;
    // detail
    double planeHeight;
    double planeSpeed;
    boolean fixedReachTime;

    public PlaneEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension, CommonDetailType detailType,
                      double planeHeight, double planeSpeed, boolean fixedReachTime) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;
        this.detailType = detailType;

        this.planeHeight = planeHeight;
        this.planeSpeed = planeSpeed;
        this.fixedReachTime = fixedReachTime;
    }

    @Override
    public String getType() {
        return SpawnTypeTag.SPAWN_TYPE_PLANE;
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
        jsonObject.addProperty(SpawnDetailTag.PLANE_HEIGHT, planeHeight);
        jsonObject.addProperty(SpawnDetailTag.PLANE_SPEED, planeSpeed);
        jsonObject.addProperty(SpawnDetailTag.PLANE_FIXED_TIME, fixedReachTime);

        return jsonObject;
    }

    @Nullable
    public static PlaneEntry fromJson(JsonObject jsonObject) {
        // common
        String shapeTypeString = jsonObject.has(SpawnShapeTag.TYPE_NAME) ? jsonObject.getAsJsonPrimitive(SpawnShapeTag.TYPE_NAME).getAsString() : "";
        SpawnShapeType shapeType = SpawnShapeType.fromName(shapeTypeString);
        if (shapeType == null) {
            BattleRoyale.LOGGER.info("Unknown shapeType in PlaneEntry, skipped");
            return null;
        }
        String centerString = jsonObject.has(SpawnShapeTag.CENTER) ? jsonObject.getAsJsonPrimitive(SpawnShapeTag.CENTER).getAsString() : "";
        String dimensionString = jsonObject.has(SpawnShapeTag.DIMENSION) ? jsonObject.getAsJsonPrimitive(SpawnShapeTag.DIMENSION).getAsString() : "";
        Vec3 center = StringUtils.parseVectorString(centerString);
        Vec3 dimension = StringUtils.parseVectorString(dimensionString);
        if (center == null || dimension == null) {
            BattleRoyale.LOGGER.info("Invalid center or dimension in PlaneEntry, skipped");
            return null;
        }

        String detailTypeString = jsonObject.has(SpawnDetailTag.TYPE_NAME) ? jsonObject.getAsJsonPrimitive(SpawnDetailTag.TYPE_NAME).getAsString() : "";
        CommonDetailType detailType = CommonDetailType.fromName(detailTypeString);
        if (detailType == null) {
            BattleRoyale.LOGGER.info("Unknown detailType for GroundEntry, skipped");
            return null;
        }

        // detail
        double height = jsonObject.has(SpawnDetailTag.PLANE_HEIGHT) ? jsonObject.getAsJsonPrimitive(SpawnDetailTag.PLANE_HEIGHT).getAsDouble() : 0;
        double speed = jsonObject.has(SpawnDetailTag.PLANE_SPEED) ? jsonObject.getAsJsonPrimitive(SpawnDetailTag.PLANE_SPEED).getAsDouble() : 0;
        boolean fixedTime = jsonObject.has(SpawnDetailTag.PLANE_FIXED_TIME) && jsonObject.getAsJsonPrimitive(SpawnDetailTag.PLANE_FIXED_TIME).getAsBoolean();

        return new PlaneEntry(shapeType, center, dimension, detailType, height, speed, fixedTime);
    }
}
