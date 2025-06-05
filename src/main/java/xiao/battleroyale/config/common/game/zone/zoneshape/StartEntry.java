package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.util.StringUtils;

public class StartEntry {

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

    @Nullable
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
                String centerPosString = centerObject.has(ZoneShapeTag.FIXED) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                centerPos = StringUtils.parseVectorString(centerPosString);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start fixed centerPos string: {}", centerPosString);
                    return null;
                }
            }
            case PREVIOUS, RELATIVE -> {
                centerZoneId = centerObject.has(ZoneShapeTag.PREVIOUS_ID) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous center zone id: {}", centerZoneId);
                    return null;
                }
                if (centerType == StartCenterType.RELATIVE) {
                    String centerPosString = centerObject.has(ZoneShapeTag.RELATIVE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    centerPos = StringUtils.parseVectorString(centerPosString);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid start relative centerPos string: {}", centerPosString);
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
                    BattleRoyale.LOGGER.info("Skipped invalid start fixed dimension string: {}", dimensionString);
                    return null;
                }
            }
            case PREVIOUS, RELATIVE -> {
                dimensionZoneId = dimensionObject.has(ZoneShapeTag.PREVIOUS_ID) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                dimensionScale = dimensionObject.has(ZoneShapeTag.PREVIOUS_SCALE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_SCALE).getAsDouble() : 0;
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension scale: {}", dimensionScale);
                    return null;
                }
                if (dimensionType == StartDimensionType.RELATIVE) {
                    String dimensionString = dimensionObject.has(ZoneShapeTag.RELATIVE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    dimension = StringUtils.parseVectorString(dimensionString);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid start relative dimension string: {}", dimensionString);
                        return null;
                    }
                }
            }
        }
        dimensionRange = dimensionObject.has(ZoneShapeTag.RANDOM_RANGE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;

        return new StartEntry(centerType, centerPos, centerZoneId, centerRange,
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
        centerObject.addProperty(ZoneShapeTag.CENTER_TYPE, startCenterType.getValue());
        switch (startCenterType) {
            case FIXED -> centerObject.addProperty(ZoneShapeTag.FIXED, StringUtils.vectorToString(startCenterPos));
            case PREVIOUS, RELATIVE -> {
                centerObject.addProperty(ZoneShapeTag.PREVIOUS_ID, startCenterZoneId);
                if (startCenterType == StartCenterType.RELATIVE) {
                    centerObject.addProperty(ZoneShapeTag.RELATIVE, StringUtils.vectorToString(startCenterPos));
                }
            }
        }
        centerObject.addProperty(ZoneShapeTag.RANDOM_RANGE, startCenterRange);
        return centerObject;
    }

    @NotNull
    private JsonObject getDimensionJsonObject() {
        JsonObject dimensionObject = new JsonObject();
        dimensionObject.addProperty(ZoneShapeTag.DIMENSION_TYPE, startDimensionType.getValue());
        switch (startDimensionType) {
            case FIXED -> dimensionObject.addProperty(ZoneShapeTag.FIXED, StringUtils.vectorToString(startDimension));
            case PREVIOUS, RELATIVE -> {
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_ID, startCenterZoneId);
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_SCALE, startDimensionScale);
                if (startDimensionType == StartDimensionType.RELATIVE) {
                    dimensionObject.addProperty(ZoneShapeTag.RELATIVE, StringUtils.vectorToString(startDimension));
                }
            }
        }
        dimensionObject.addProperty(ZoneShapeTag.RANDOM_RANGE, startDimensionRange);
        return dimensionObject;
    }
}