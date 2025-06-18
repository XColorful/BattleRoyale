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

    public EndCenterType endCenterType = EndCenterType.FIXED;
    public Vec3 endCenterPos = Vec3.ZERO; // fixed x, z / relative x, z
    public int endCenterZoneId = 0;
    public double endCenterProgress = 0;
    public double endCenterRange = 0;
    public int playerId = 0;
    public boolean selectStanding = false;

    public EndDimensionType endDimensionType = EndDimensionType.FIXED;
    public Vec3 endDimension = Vec3.ZERO; // radius / size / a, b
    public int endDimensionZoneId = 0; // previous zone id
    public double endDimensionProgress = 0;
    public double endDimensionScale = 1; // previous dimension scale
    public double endDimensionRange = 0;

    public EndEntry(EndCenterType endCenterType, Vec3 endCenterPos, int endCenterZoneId, double endCenterRange, int playerId, boolean selectStanding,
                    EndDimensionType endDimensionType, Vec3 endDimension, int endDimensionZoneId, double endDimensionScale, double endDimensionRange) {
        this.endCenterType = endCenterType;
        this.endCenterPos = endCenterPos;
        this.endCenterZoneId = endCenterZoneId;
        this.endCenterRange = endCenterRange;
        this.playerId = playerId;

        this.endDimensionType = endDimensionType;
        this.endDimension = endDimension;
        this.endDimensionZoneId = endDimensionZoneId;
        this.endDimensionScale = endDimensionScale;
        this.endDimensionRange = endDimensionRange;
    }

    public EndEntry() {
        ;
    }
    public void addFixedCenter(Vec3 endCenterPos) {
        this.endCenterType = EndCenterType.FIXED;
        this.endCenterPos = endCenterPos;
    }
    public void addPreviousCenter(int prevZoneId, double progress) {
        this.endCenterType = EndCenterType.PREVIOUS;
        this.endCenterZoneId = prevZoneId;
        this.endCenterProgress = Math.min(Math.max(0, progress), 1);
    }
    public void addRelativeCenter(Vec3 relativeAdd) {
        this.endCenterType = EndCenterType.RELATIVE;
        this.endCenterPos = relativeAdd;
    }
    public void addLockCenter(int playerId, boolean selectStanding) {
        this.endCenterType = EndCenterType.LOCK_PLAYER;
        this.playerId = playerId;
        this.selectStanding = selectStanding;
    }
    public void addCenterRange(double range) {
        this.endCenterRange = range;
    }

    public void addFixedDimension(Vec3 endDimension) {
        this.endDimensionType = EndDimensionType.FIXED;
        BattleRoyale.LOGGER.info("addFixedDimension before: {}", this.endDimension);
        this.endDimension = endDimension;
        BattleRoyale.LOGGER.info("addFixedDimension after: {}", this.endDimension);
    }
    public void addPreviousDimension(int prevZoneId, double progress) {
        this.endDimensionType = EndDimensionType.PREVIOUS;
        this.endDimensionZoneId = prevZoneId;
        this.endDimensionProgress = Math.min(Math.max(0, progress), 1);
    }
    public void addRelativeDimension(Vec3 relativeAdd) {
        this.endDimensionType = EndDimensionType.RELATIVE;
        this.endDimension = relativeAdd;
    }
    public void addDimensionScale(double scale) {
        this.endDimensionScale = scale;
    }
    public void addDimensionRange(double range) {
        this.endDimensionRange = range;
    }

    @Nullable
    public static EndEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = jsonObject.has(ZoneShapeTag.CENTER) ? jsonObject.getAsJsonObject(ZoneShapeTag.CENTER) : null;
        JsonObject dimensionObject = jsonObject.has(ZoneShapeTag.DIMENSION) ? jsonObject.getAsJsonObject(ZoneShapeTag.DIMENSION) : null;
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("EndEntry missing member center or dimension, skipped");
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
        EndEntry endEntry = new EndEntry();

        // center
        switch (centerType) {
            case FIXED -> {
                String centerPosString = centerObject.has(ZoneShapeTag.FIXED) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                Vec3 centerPos = StringUtils.parseVectorString(centerPosString);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end fixed centerPos string: {}", centerPosString);
                    return null;
                }
                endEntry.addFixedCenter(centerPos);
            }
            case PREVIOUS, RELATIVE -> {
                int centerZoneId = centerObject.has(ZoneShapeTag.PREVIOUS_ID) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous center zone id: {}", centerZoneId);
                    return null;
                }
                double centerProgress = centerObject.has(ZoneShapeTag.PREVIOUS_PROGRESS) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_PROGRESS).getAsDouble() : 1;
                endEntry.addPreviousCenter(centerZoneId, centerProgress);
                if (centerType == EndCenterType.RELATIVE) {
                    String centerPosString = centerObject.has(ZoneShapeTag.RELATIVE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    Vec3 centerPos = StringUtils.parseVectorString(centerPosString);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid end relative centerPos string: {}", centerPosString);
                        return null;
                    }
                    endEntry.addRelativeCenter(centerPos);
                }
            }
            case LOCK_PLAYER -> {
                int playerId = centerObject.has(ZoneShapeTag.PLAYER_ID) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.PLAYER_ID).getAsInt() : 0;
                if (playerId < 0) {
                    BattleRoyale.LOGGER.info("Invalid playerId {}, defaulting to 0 (random select)", playerId);
                    playerId = 0;
                }
                boolean selectStanding = centerObject.has(ZoneShapeTag.SELECT_STANDING) && centerObject.getAsJsonPrimitive(ZoneShapeTag.SELECT_STANDING).getAsBoolean();
                endEntry.addLockCenter(playerId, selectStanding);
            }
        }
        double centerRange = centerObject.has(ZoneShapeTag.RANDOM_RANGE) ? centerObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;
        endEntry.addCenterRange(centerRange);

        // dimension
        switch (dimensionType) {
            case FIXED -> {
                String dimensionString = dimensionObject.has(ZoneShapeTag.FIXED) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.FIXED).getAsString() : "";
                Vec3 dimension = StringUtils.parseVectorString(dimensionString);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end fixed dimension string: {}", dimensionString);
                    return null;
                }
                endEntry.addFixedDimension(dimension);
            }
            case PREVIOUS, RELATIVE -> {
                int dimensionZoneId = dimensionObject.has(ZoneShapeTag.PREVIOUS_ID) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_ID).getAsInt() : -1;
                double dimensionScale = dimensionObject.has(ZoneShapeTag.PREVIOUS_SCALE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_SCALE).getAsDouble() : 1;
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension scale: {}", dimensionScale);
                    return null;
                }
                double dimensionProgress = dimensionObject.has(ZoneShapeTag.PREVIOUS_PROGRESS) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.PREVIOUS_PROGRESS).getAsDouble() : 1;
                endEntry.addPreviousDimension(dimensionZoneId, dimensionProgress);
                endEntry.addDimensionScale(dimensionScale);
                if (dimensionType == EndDimensionType.RELATIVE) {
                    String dimensionString = dimensionObject.has(ZoneShapeTag.RELATIVE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RELATIVE).getAsString() : "";
                    Vec3 dimension = StringUtils.parseVectorString(dimensionString);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid end relative dimension string: {}", dimensionString);
                        return null;
                    }
                    endEntry.addRelativeDimension(dimension);
                }
            }
        }
        double dimensionRange = dimensionObject.has(ZoneShapeTag.RANDOM_RANGE) ? dimensionObject.getAsJsonPrimitive(ZoneShapeTag.RANDOM_RANGE).getAsDouble() : 0;
        endEntry.addDimensionRange(dimensionRange);

        return endEntry;
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
                centerObject.addProperty(ZoneShapeTag.PREVIOUS_PROGRESS, endCenterProgress);
                if (endCenterType == EndCenterType.RELATIVE) {
                    centerObject.addProperty(ZoneShapeTag.RELATIVE, StringUtils.vectorToString(endCenterPos));
                }
            }
            case LOCK_PLAYER -> {
                centerObject.addProperty(ZoneShapeTag.PLAYER_ID, playerId);
                centerObject.addProperty(ZoneShapeTag.SELECT_STANDING, selectStanding);
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
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_ID, endDimensionZoneId);
                dimensionObject.addProperty(ZoneShapeTag.PREVIOUS_PROGRESS, endDimensionProgress);
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