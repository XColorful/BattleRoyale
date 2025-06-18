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

    public StartCenterType startCenterType = StartCenterType.FIXED;
    public Vec3 startCenterPos = Vec3.ZERO; // fixed x, z
    public int startCenterZoneId = 0; // previous zone id
    public double startCenterProgress = 1;
    public double startCenterRange = 0;
    public int playerId = 0;
    public boolean selectStanding = false;

    public StartDimensionType startDimensionType = StartDimensionType.FIXED;
    public Vec3 startDimension = Vec3.ZERO; // radius / side / a, b
    public int startDimensionZoneId = 0; // previous zone id
    public double startDimensionProgress = 1;
    public double startDimensionScale = 1; // previous dimension scale
    public double startDimensionRange = 0;

    public StartEntry() {
        ;
    }
    public void addFixedCenter(Vec3 startCenterPos) {
        this.startCenterType = StartCenterType.FIXED;
        this.startCenterPos = startCenterPos;
    }
    public void addPreviousCenter(int prevZoneId, double progress) {
        this.startCenterType = StartCenterType.PREVIOUS;
        this.startCenterZoneId = prevZoneId;
        this.startCenterProgress = Math.min(Math.max(0, progress), 1);
    }
    public void addRelativeCenter(Vec3 relativeAdd) {
        this.startCenterType = StartCenterType.RELATIVE;
        this.startCenterPos = relativeAdd;
    }
    public void addLockCenter(int playerId, boolean selectStanding) {
        this.startCenterType = StartCenterType.LOCK_PLAYER;
        this.playerId = playerId;
        this.selectStanding = selectStanding;
    }
    public void addCenterRange(double range) {
        this.startCenterRange = range;
    }

    public void addFixedDimension(Vec3 startDimension) {
        this.startDimensionType = StartDimensionType.FIXED;
        this.startDimension = startDimension;
    }
    public void addPreviousDimension(int prevZoneId, double progress) {
        this.startDimensionType = StartDimensionType.PREVIOUS;
        this.startDimensionZoneId = prevZoneId;
        this.startDimensionProgress = Math.min(Math.max(0, progress), 1);
    }
    public void addRelativeDimension(Vec3 relativeAdd) {
        this.startDimensionType = StartDimensionType.RELATIVE;
        this.startDimension = relativeAdd;
    }
    public void addDimensionScale(double scale) {
        this.startDimensionScale = scale;
    }
    public void addDimensionRange(double range) {
        this.startDimensionRange = range;
    }

    @Nullable
    public static StartEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = jsonObject.has(ZoneShapeTag.CENTER) ? jsonObject.getAsJsonObject(ZoneShapeTag.CENTER) : null;
        JsonObject dimensionObject = jsonObject.has(ZoneShapeTag.DIMENSION) ? jsonObject.getAsJsonObject(ZoneShapeTag.DIMENSION) : null;
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("StartEntry missing center or dimension member, skipped");
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
        StartEntry startEntry = new StartEntry();

        // center
        switch (centerType) {
            case FIXED -> {
                String centerPosString = centerObject.has(ZoneShapeTag.FIXED) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                Vec3 centerPos = StringUtils.parseVectorString(centerPosString);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start fixed centerPos string: {}", centerPosString);
                    return null;
                }
                startEntry.addFixedCenter(centerPos);
            }
            case PREVIOUS, RELATIVE -> {
                int centerZoneId = centerObject.has(ZoneShapeTag.PREVIOUS_ID) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous center zone id: {}", centerZoneId);
                    return null;
                }
                double centerProgress = centerObject.has(ZoneShapeTag.PREVIOUS_PROGRESS) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_PROGRESS).getAsDouble() : 1;
                startEntry.addPreviousCenter(centerZoneId, centerProgress);
                if (centerType == StartCenterType.RELATIVE) {
                    String centerPosString = centerObject.has(ZoneShapeTag.RELATIVE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    Vec3 centerPos = StringUtils.parseVectorString(centerPosString);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid start relative centerPos string: {}", centerPosString);
                        return null;
                    }
                    startEntry.addRelativeCenter(centerPos);
                }
            }
            case LOCK_PLAYER -> {
                int playerId = centerObject.has(ZoneShapeTag.PLAYER_ID) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PLAYER_ID).getAsInt() : 0;
                if (playerId < 0) {
                    BattleRoyale.LOGGER.info("Invalid playerId {}, defaulting to 0 (random select)", playerId);
                    playerId = 0;
                }
                boolean selectStanding = centerObject.has(ZoneShapeTag.SELECT_STANDING) && centerObject.getAsJsonPrimitive(ZoneShapeTag.SELECT_STANDING).getAsBoolean();
                startEntry.addLockCenter(playerId, selectStanding);
            }
        }
        double centerRange = centerObject.has(ZoneShapeTag.RANDOM_RANGE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;
        startEntry.addCenterRange(centerRange);

        // dimension
        switch (dimensionType) {
            case FIXED -> {
                String dimensionString = dimensionObject.has(ZoneShapeTag.FIXED) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                Vec3 dimension = StringUtils.parseVectorString(dimensionString);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start fixed dimension string: {}", dimensionString);
                    return null;
                }
                startEntry.addFixedDimension(dimension);
            }
            case PREVIOUS, RELATIVE -> {
                int dimensionZoneId = dimensionObject.has(ZoneShapeTag.PREVIOUS_ID) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                double dimensionScale = dimensionObject.has(ZoneShapeTag.PREVIOUS_SCALE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_SCALE).getAsDouble() : 0;
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension scale: {}", dimensionScale);
                    return null;
                }
                double dimensionProgress = dimensionObject.has(ZoneShapeTag.PREVIOUS_PROGRESS) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_PROGRESS).getAsDouble() : 1;
                startEntry.addPreviousDimension(dimensionZoneId, dimensionProgress);
                startEntry.addDimensionScale(dimensionScale);
                if (dimensionType == StartDimensionType.RELATIVE) {
                    String dimensionString = dimensionObject.has(ZoneShapeTag.RELATIVE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    Vec3 dimension = StringUtils.parseVectorString(dimensionString);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid start relative dimension string: {}", dimensionString);
                        return null;
                    }
                    startEntry.addRelativeDimension(dimension);
                }
            }
        }
        double dimensionRange = dimensionObject.has(ZoneShapeTag.RANDOM_RANGE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;
        startEntry.addDimensionRange(dimensionRange);

        return startEntry;
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
                centerObject.addProperty(ZoneShapeTag.PREVIOUS_PROGRESS, startCenterProgress);
                if (startCenterType == StartCenterType.RELATIVE) {
                    centerObject.addProperty(ZoneShapeTag.RELATIVE, StringUtils.vectorToString(startCenterPos));
                }
            }
            case LOCK_PLAYER -> {
                centerObject.addProperty(ZoneShapeTag.PLAYER_ID, playerId);
                centerObject.addProperty(ZoneShapeTag.SELECT_STANDING, selectStanding);
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
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_ID, startDimensionZoneId);
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_PROGRESS, startDimensionProgress);
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