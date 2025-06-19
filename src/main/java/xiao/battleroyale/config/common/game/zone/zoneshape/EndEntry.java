package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.end.EndRotationType;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class EndEntry {

    public EndCenterType endCenterType = EndCenterType.FIXED;
    public Vec3 endCenterPos = Vec3.ZERO; // fixed x, z / relative x, z
    public int endCenterZoneId = 0;
    public double endCenterProgress = 0;
    public double endCenterRange = 0;
    public int centerPlayerId = 0;
    public boolean selectStanding = false;

    public EndDimensionType endDimensionType = EndDimensionType.FIXED;
    public Vec3 endDimension = Vec3.ZERO; // radius / size / a, b
    public int endDimensionZoneId = 0; // previous zone id
    public double endDimensionProgress = 0;
    public double endDimensionScale = 1; // previous dimension scale
    public double endDimensionRange = 0;

    }
    // 淘汰的玩家就没必要选择了

    public EndEntry() {
        ;
    }
    // build center
    public void addFixedCenter(Vec3 endCenterPos) {
        this.endCenterType = EndCenterType.FIXED;
        this.endCenterPos = endCenterPos;
    }
    public void addPreviousCenter(int prevZoneId, double progress) {
        this.endCenterType = EndCenterType.PREVIOUS;
        this.endCenterZoneId = prevZoneId;
        this.endCenterProgress = GameZone.allowedProgress(progress);
    }
    public void addRelativeCenter(Vec3 relativeAdd) {
        this.endCenterType = EndCenterType.RELATIVE;
        this.endCenterPos = relativeAdd;
    }
    public void addLockCenter(int playerId, boolean selectStanding) {
        this.endCenterType = EndCenterType.LOCK_PLAYER;
        this.centerPlayerId = playerId;
        this.selectStanding = selectStanding;
    }
    public void addCenterRange(double range) {
        this.endCenterRange = range;
    }
    // build dimension
    public void addFixedDimension(Vec3 endDimension) {
        this.endDimensionType = EndDimensionType.FIXED;
        this.endDimension = endDimension;
    }
    public void addPreviousDimension(int prevZoneId, double progress) {
        this.endDimensionType = EndDimensionType.PREVIOUS;
        this.endDimensionZoneId = prevZoneId;
        this.endDimensionProgress = GameZone.allowedProgress(progress);
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
    }

    @Nullable
    public static EndEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.CENTER, null);
        JsonObject dimensionObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.DIMENSION, null);
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("EndEntry missing member center or dimension, skipped");
            return null;
        }

        EndCenterType centerType = EndCenterType.fromValue(JsonUtils.getJsonString(centerObject, ZoneShapeTag.CENTER_TYPE, ""));
        EndDimensionType dimensionType = EndDimensionType.fromValue(JsonUtils.getJsonString(dimensionObject, ZoneShapeTag.DIMENSION_TYPE, ""));
        if (centerType == null || dimensionType == null) {
            BattleRoyale.LOGGER.info("Skipped invalid end centerType or dimensionType");
            return null;
        }
        EndEntry endEntry = new EndEntry();

        // center
        switch (centerType) {
            case FIXED -> {
                Vec3 centerPos = JsonUtils.getJsonVec(centerObject, ZoneShapeTag.FIXED, null);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end fixed centerPos");
                    return null;
                }
                endEntry.addFixedCenter(centerPos);
            }
            case PREVIOUS, RELATIVE -> {
                int centerZoneId = JsonUtils.getJsonInt(centerObject, ZoneShapeTag.PREVIOUS_ID, -1);
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous center zone id: {}", centerZoneId);
                    return null;
                }
                double centerProgress = JsonUtils.getJsonDouble(centerObject, ZoneShapeTag.PREVIOUS_PROGRESS, 0);
                endEntry.addPreviousCenter(centerZoneId, centerProgress);
                if (centerType == EndCenterType.RELATIVE) {
                    Vec3 centerPos = JsonUtils.getJsonVec(centerObject, ZoneShapeTag.RELATIVE, null);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid end relative centerPos");
                        return null;
                    }
                    endEntry.addRelativeCenter(centerPos);
                }
            }
            case LOCK_PLAYER -> {
                int playerId = JsonUtils.getJsonInt(centerObject, ZoneShapeTag.PLAYER_ID, 0);
                if (playerId < 0) {
                    BattleRoyale.LOGGER.info("Invalid centerPlayerId {}, defaulting to 0 (random select)", playerId);
                    playerId = 0;
                }
                boolean selectStanding = JsonUtils.getJsonBoolean(centerObject, ZoneShapeTag.SELECT_STANDING, false);
                endEntry.addLockCenter(playerId, selectStanding);
            }
        }
        double centerRange = JsonUtils.getJsonDouble(centerObject, ZoneShapeTag.RANDOM_RANGE, 0);
        endEntry.addCenterRange(centerRange);

        // dimension
        switch (dimensionType) {
            case FIXED -> {
                Vec3 dimension = JsonUtils.getJsonVec(dimensionObject, ZoneShapeTag.FIXED, null);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid end fixed dimension");
                    return null;
                }
                endEntry.addFixedDimension(dimension);
            }
            case PREVIOUS, RELATIVE -> {
                int dimensionZoneId = JsonUtils.getJsonInt(dimensionObject, ZoneShapeTag.PREVIOUS_ID, -1);
                double dimensionScale = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.PREVIOUS_SCALE, 1);
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid end previous dimension zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Invalid end previous dimension scale {}, defaulting to 0", dimensionScale);
                    dimensionScale = 0;
                }
                double dimensionProgress = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.PREVIOUS_PROGRESS, 0);
                endEntry.addPreviousDimension(dimensionZoneId, dimensionProgress);
                endEntry.addDimensionScale(dimensionScale);
                if (dimensionType == EndDimensionType.RELATIVE) {
                    Vec3 dimension = JsonUtils.getJsonVec(dimensionObject, ZoneShapeTag.RELATIVE, null);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Skipped invalid end relative dimension");
                        return null;
                    }
                    endEntry.addRelativeDimension(dimension);
                }
            }
        }
        double dimensionRange = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.RANDOM_RANGE, 0);
        endEntry.addDimensionRange(dimensionRange);

        return endEntry;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject centerObject = generateCenterJson();
        jsonObject.add(ZoneShapeTag.CENTER, centerObject);

        JsonObject dimensionObject = generateDimensionJson();
        jsonObject.add(ZoneShapeTag.DIMENSION, dimensionObject);

        return jsonObject;
    }

    @NotNull
    private JsonObject generateCenterJson() {
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
                centerObject.addProperty(ZoneShapeTag.PLAYER_ID, centerPlayerId);
                centerObject.addProperty(ZoneShapeTag.SELECT_STANDING, selectStanding);
            }
        }
        centerObject.addProperty(ZoneShapeTag.RANDOM_RANGE, endCenterRange);
        return centerObject;
    }

    @NotNull
    private JsonObject generateDimensionJson() {
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