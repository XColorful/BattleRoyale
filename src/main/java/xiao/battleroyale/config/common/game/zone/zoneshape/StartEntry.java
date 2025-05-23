package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.util.StringUtils;

public class StartEntry {

    public static final String FIXED = "fixed";
    public static final String PREVIOUS_ID = "previousZoneId";
    public static final String RANDOM_RANGE = "randomRange";
    public static final String PREVIOUS_SCALE = "scale";

    public StartCenterType startCenterType;
    public Vec3 startCenterPos; // fixed x, z
    public int startCenterZoneId; // previous zone id
    public double startCenterRange;

    public StartDimensionType startDimensionType;
    public Vec3 startDimension; // radius / side / a, b
    public int startDimensionZoneId; // previous zone id
    public double startDimensionScale; // previous dimension scale
    public double startDimensionRange;

    public StartEntry(StartCenterType startCenterType, Vec3 startCenterPos, int startCenterZoneId, double startCenterRange,
                      StartDimensionType startDimensionType, Vec3 startDimension, int startDimensionZoneId, double startDimensionScale, double startDimensionRange) {
        this.startCenterType = startCenterType;
        this.startCenterPos = startCenterPos;
        this.startCenterZoneId = startCenterZoneId;
        this.startCenterRange = startCenterRange;

        this.startDimensionType = startDimensionType;
        this.startDimension = startDimension;
        this.startDimensionZoneId = startDimensionZoneId;
        this.startDimensionScale = startDimensionScale;
        this.startDimensionRange = startDimensionRange;
    }

    public static StartEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = jsonObject.has(ZoneShapeTag.CENTER) ? jsonObject.getAsJsonObject(ZoneShapeTag.CENTER) : null;
        JsonObject dimensionObject = jsonObject.has(ZoneShapeTag.DIMENSION) ? jsonObject.getAsJsonObject(ZoneShapeTag.DIMENSION) : null;
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("StartEntry missing member, skipped");
            return null;
        }

        String centerTypeString = centerObject.has(ZoneShapeTag.CENTER_TYPE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.CENTER_TYPE).getAsString() : null;
        StartCenterType centerType = StartCenterType.fromValue(centerTypeString);
        String dimensionTypeString = dimensionObject.has(ZoneShapeTag.DIMENSION_TYPE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.DIMENSION_TYPE).getAsString() : null;
        StartDimensionType dimensionType = StartDimensionType.fromValue(dimensionTypeString);
        if (centerType == null || dimensionType == null) {
            BattleRoyale.LOGGER.info("Skipped invalid start centerType or dimensionType");
            return null;
        }

        Vec3 centerPos = Vec3.ZERO;
        int centerZoneId = -1;
        double centerRange = 0;
        switch (centerType) {
            case FIXED -> {
                String centerPosString = centerObject.has(StartEntry.FIXED) ? centerObject.getAsJsonPrimitive(StartEntry.FIXED).getAsString() : "";
                centerPos = StringUtils.parseVectorString(centerPosString);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start centerPos string: {}", centerPosString);
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
        centerRange = centerObject.has(StartEntry.RANDOM_RANGE) ? centerObject.getAsJsonPrimitive(StartEntry.RANDOM_RANGE).getAsDouble() : 0;

        Vec3 dimension = Vec3.ZERO;
        int dimensionZoneId = -1;
        double dimensionScale = 1;
        double dimensionRange = 0;
        switch (dimensionType) {
            case FIXED -> {
                String dimensionString = dimensionObject.has(StartEntry.FIXED) ? dimensionObject.getAsJsonPrimitive(StartEntry.FIXED).getAsString() : "";
                dimension = StringUtils.parseVectorString(dimensionString);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start dimension string: {}", dimensionString);
                    return null;
                }
            }
            case PREVIOUS -> {
                dimensionZoneId = dimensionObject.has(StartEntry.PREVIOUS_ID) ? dimensionObject.getAsJsonPrimitive(StartEntry.PREVIOUS_ID).getAsInt() : -1;
                dimensionScale = dimensionObject.has(StartEntry.PREVIOUS_SCALE) ? dimensionObject.getAsJsonPrimitive(StartEntry.PREVIOUS_SCALE).getAsDouble() : 0;
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start dimension previous zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start dimension previous scale: {}", dimensionScale);
                    return null;
                }
            }
        }
        dimensionRange = dimensionObject.has(StartEntry.RANDOM_RANGE) ? dimensionObject.getAsJsonPrimitive(StartEntry.RANDOM_RANGE).getAsDouble() : 0;

        return new StartEntry(centerType, centerPos, centerZoneId, centerRange,
                dimensionType, dimension, dimensionZoneId, dimensionScale, dimensionRange);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject centerObject = new JsonObject();
        centerObject.addProperty(ZoneShapeTag.CENTER_TYPE, startCenterType.getValue());
        switch (startCenterType) {
            case FIXED -> centerObject.addProperty(StartEntry.FIXED, StringUtils.vectorToString(startCenterPos));
            case PREVIOUS -> centerObject.addProperty(StartEntry.PREVIOUS_ID, startCenterZoneId);
        }
        centerObject.addProperty(StartEntry.RANDOM_RANGE, startCenterRange);
        jsonObject.add(ZoneShapeTag.CENTER, centerObject);

        JsonObject dimensionObject = new JsonObject();
        dimensionObject.addProperty(ZoneShapeTag.DIMENSION_TYPE, startDimensionType.getValue());
        switch (startDimensionType) {
            case FIXED -> dimensionObject.addProperty(StartEntry.FIXED, StringUtils.vectorToString(startDimension));
            case PREVIOUS -> {
                dimensionObject.addProperty(StartEntry.PREVIOUS_ID, startDimensionZoneId);
                dimensionObject.addProperty(StartEntry.PREVIOUS_SCALE, startDimensionScale);
            }
        }
        dimensionObject.addProperty(StartEntry.RANDOM_RANGE, startDimensionRange);
        jsonObject.add(ZoneShapeTag.DIMENSION, dimensionObject);

        return jsonObject;
    }
}