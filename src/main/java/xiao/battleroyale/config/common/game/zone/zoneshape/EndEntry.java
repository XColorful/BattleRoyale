package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.util.StringUtils;

public class EndEntry {

    public EndCenterType endCenterType;
    public Vec3 endCenterPos; // fixed x, z / relative x, z
    public int endCenterZoneId;
    public double endCenterRange;

    public EndDimensionType endDimensionType;
    public Vec3 endDimension; // radius / size / a, b
    public int endDimensionZoneId; // previous zone id
    public double endDimensionScale; // previous dimension scale
    public double endDimensionRange;

    public EndEntry(EndCenterType endCenterType, Vec3 endCenterPos, int endCenterZoneId, double endCenterRange,
                    EndDimensionType endDimensionType, Vec3 endDimension, int endDimensionZoneId, double endDimensionScale, double endDimensionRange) {
        this.endCenterType = endCenterType;
        this.endCenterPos = endCenterPos;
        this.endCenterZoneId = endCenterZoneId;
        this.endCenterRange = endCenterRange;

        this.endDimensionType = endDimensionType;
        this.endDimension = endDimension;
        this.endDimensionZoneId = endDimensionZoneId;
        this.endDimensionScale = endDimensionScale;
        this.endDimensionRange = endDimensionRange;
    }

    @Nullable
    public static EndEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = jsonObject.has(ZoneShapeTag.CENTER) ? jsonObject.getAsJsonObject(ZoneShapeTag.CENTER) : null;
        JsonObject dimensionObject = jsonObject.has(ZoneShapeTag.DIMENSION) ? jsonObject.getAsJsonObject(ZoneShapeTag.DIMENSION) : null;
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("EndEntry missing member (center or dimension), skipped");
            return null;
        }

        String centerTypeString = centerObject.has(ZoneShapeTag.CENTER_TYPE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.CENTER_TYPE).getAsString() : null;
        EndCenterType centerType = EndCenterType.fromValue(centerTypeString);
        String dimensionTypeString = dimensionObject.has(ZoneShapeTag.DIMENSION_TYPE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.DIMENSION_TYPE).getAsString() : null;
        EndDimensionType dimensionType = EndDimensionType.fromValue(dimensionTypeString);
        if (centerType == null || dimensionType == null) {
            BattleRoyale.LOGGER.info("Skipped invalid end centerType or dimensionType");
            return null;
        }

        Vec3 centerPos = Vec3.ZERO;
        int centerZoneId = -1;
        double centerRange = 0;
        switch (centerType) {
            case FIXED -> {
                String centerPosString = centerObject.has(ZoneShapeTag.FIXED) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                centerPos = StringUtils.parseVectorString(centerPosString);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end fixed centerPos string: {}", centerPosString);
                    return null;
                }
            }
            case PREVIOUS, RELATIVE -> {
                centerZoneId = centerObject.has(ZoneShapeTag.PREVIOUS_ID) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous center zone id: {}", centerZoneId);
                    return null;
                }
                if (centerType == EndCenterType.RELATIVE) {
                    String centerPosString = centerObject.has(ZoneShapeTag.RELATIVE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    centerPos = StringUtils.parseVectorString(centerPosString);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid end relative centerPos string: {}", centerPosString);
                        return null;
                    }
                }
            }
        }
        centerRange = centerObject.has(ZoneShapeTag.RANDOM_RANGE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;

        Vec3 dimension = Vec3.ZERO;
        int dimensionZoneId = -1;
        double dimensionScale = 1;
        double dimensionRange = 0;
        switch (dimensionType) {
            case FIXED -> {
                String dimensionString = dimensionObject.has(ZoneShapeTag.FIXED) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                dimension = StringUtils.parseVectorString(dimensionString);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end fixed dimension string: {}", dimensionString);
                    return null;
                }
            }
            case PREVIOUS, RELATIVE -> {
                dimensionZoneId = dimensionObject.has(ZoneShapeTag.PREVIOUS_ID) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                dimensionScale = dimensionObject.has(ZoneShapeTag.PREVIOUS_SCALE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_SCALE).getAsDouble() : 1;
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension scale: {}", dimensionScale);
                    return null;
                }
                if (dimensionType == EndDimensionType.RELATIVE) {
                    String dimensionString = dimensionObject.has(ZoneShapeTag.RELATIVE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    dimension = StringUtils.parseVectorString(dimensionString);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid end relative dimension string: {}", dimensionString);
                        return null;
                    }
                }
            }
        }
        dimensionRange = dimensionObject.has(ZoneShapeTag.RANDOM_RANGE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;

        return new EndEntry(centerType, centerPos, centerZoneId, centerRange,
                dimensionType, dimension, dimensionZoneId, dimensionScale, dimensionRange);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject centerObject = getCenterJsonObject();
        jsonObject.add(ZoneShapeTag.CENTER, centerObject);

        JsonObject dimensionObject = getDimensionJsonObject();
        jsonObject.add(ZoneShapeTag.DIMENSION, dimensionObject);

        return jsonObject;
    }

    @NotNull
    private JsonObject getCenterJsonObject() {
        JsonObject centerObject = new JsonObject();
        centerObject.addProperty(ZoneShapeTag.CENTER_TYPE, endCenterType.getValue());
        switch (endCenterType) {
            case FIXED -> centerObject.addProperty(ZoneShapeTag.FIXED, StringUtils.vectorToString(endCenterPos));
            case PREVIOUS, RELATIVE -> {
                centerObject.addProperty(ZoneShapeTag.PREVIOUS_ID, endCenterZoneId);
                if (endCenterType == EndCenterType.RELATIVE) {
                    centerObject.addProperty(ZoneShapeTag.RELATIVE, StringUtils.vectorToString(endCenterPos));
                }
            }
        }
        centerObject.addProperty(ZoneShapeTag.RANDOM_RANGE, endCenterRange);
        return centerObject;
    }

    @NotNull
    private JsonObject getDimensionJsonObject() {
        JsonObject dimensionObject = new JsonObject();
        dimensionObject.addProperty(ZoneShapeTag.DIMENSION_TYPE, endDimensionType.getValue());
        switch (endDimensionType) {
            case FIXED -> dimensionObject.addProperty(ZoneShapeTag.FIXED, StringUtils.vectorToString(endDimension));
            case PREVIOUS, RELATIVE -> {
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_ID, endCenterZoneId);
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_SCALE, endDimensionScale);
                if (endDimensionType == EndDimensionType.RELATIVE) {
                    dimensionObject.addProperty(ZoneShapeTag.RELATIVE, StringUtils.vectorToString(endDimension));
                }
            }
        }
        dimensionObject.addProperty(ZoneShapeTag.RANDOM_RANGE, endDimensionRange);
        return dimensionObject;
    }
}