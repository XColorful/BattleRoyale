package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.util.StringUtils;

public class EndEntry {

    public static final String FIXED = "fixed";
    public static final String PREVIOUS_ID = "previousZoneId";
    public static final String RANDOM_RANGE = "randomRange";
    public static final String PREVIOUS_SCALE = "scale";

    public EndCenterType endCenterType;
    public Vec3 endCenterPos; // fixed x, z
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
                String centerPosString = centerObject.has(FIXED) ? centerObject.getAsJsonPrimitive(FIXED).getAsString() : "";
                centerPos = StringUtils.parseVectorString(centerPosString);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end centerPos string: {}", centerPosString);
                    return null;
                }
            }
            case PREVIOUS -> {
                centerZoneId = centerObject.has(StartEntry.PREVIOUS_ID) ? centerObject.getAsJsonPrimitive(StartEntry.PREVIOUS_ID).getAsInt() : -1;
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start center previous zone id: {}", centerZoneId);
                    return null;
                }
            }
        }
        centerRange = centerObject.has(RANDOM_RANGE) ? centerObject.getAsJsonPrimitive(RANDOM_RANGE).getAsDouble() : 0;

        Vec3 dimension = Vec3.ZERO;
        int dimensionZoneId = -1;
        double dimensionScale = 1;
        double dimensionRange = 0;
        switch (dimensionType) {
            case FIXED -> {
                String dimensionString = dimensionObject.has(FIXED) ? dimensionObject.getAsJsonPrimitive(FIXED).getAsString() : "";
                dimension = StringUtils.parseVectorString(dimensionString);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end dimension string: {}", dimensionString);
                    return null;
                }
            }
            case PREVIOUS -> {
                dimensionZoneId = dimensionObject.has(StartEntry.PREVIOUS_ID) ? dimensionObject.getAsJsonPrimitive(StartEntry.PREVIOUS_ID).getAsInt() : -1;
                dimensionScale = dimensionObject.has(PREVIOUS_SCALE) ? dimensionObject.getAsJsonPrimitive(PREVIOUS_SCALE).getAsDouble() : 1;
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start dimension previous zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start dimension previous scale: {}", dimensionScale);
                    return null;
                }
            }
        }
        dimensionRange = dimensionObject.has(RANDOM_RANGE) ? dimensionObject.getAsJsonPrimitive(RANDOM_RANGE).getAsDouble() : 0;

        return new EndEntry(centerType, centerPos, centerZoneId, centerRange,
                dimensionType, dimension, dimensionZoneId, dimensionScale, dimensionRange);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject centerObject = new JsonObject();
        centerObject.addProperty(ZoneShapeTag.CENTER_TYPE, endCenterType.getValue());
        switch (endCenterType) {
            case FIXED -> centerObject.addProperty(EndEntry.FIXED, StringUtils.vectorToString(endCenterPos));
            case PREVIOUS -> centerObject.addProperty(EndEntry.PREVIOUS_ID, endCenterZoneId);
        }
        centerObject.addProperty(EndEntry.RANDOM_RANGE, endCenterRange);
        jsonObject.add(ZoneShapeTag.CENTER, centerObject);

        JsonObject dimensionObject = new JsonObject();
        dimensionObject.addProperty(ZoneShapeTag.DIMENSION_TYPE, endDimensionType.getValue());
        switch (endDimensionType) {
            case FIXED -> dimensionObject.addProperty(EndEntry.FIXED, StringUtils.vectorToString(endDimension));
            case PREVIOUS -> {
                dimensionObject.addProperty(EndEntry.PREVIOUS_ID, endCenterZoneId);
                dimensionObject.addProperty(EndEntry.PREVIOUS_SCALE, endDimensionScale);
            }
        }
        dimensionObject.addProperty(EndEntry.RANDOM_RANGE, endDimensionRange);
        jsonObject.add(ZoneShapeTag.DIMENSION, dimensionObject);

        return jsonObject;
    }
}