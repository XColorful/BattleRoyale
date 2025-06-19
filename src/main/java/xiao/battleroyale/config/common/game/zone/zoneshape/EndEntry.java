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
    public double playerCenterLerp = 0;

    public EndDimensionType endDimensionType = EndDimensionType.FIXED;
    public Vec3 endDimension = Vec3.ZERO; // radius / size / a, b
    public int endDimensionZoneId = 0; // previous zone id
    public double endDimensionProgress = 0;
    public double endDimensionScale = 1; // previous dimension scale
    public double endDimensionRange = 0;

    public EndRotationType endRotationType = EndRotationType.FIXED;
    public double endRotateDegree = 0;
    public int endRotateZoneId = 0;
    public double endRotateProgress = 0;
    public double endRotateScale = 1;
    public double endRotateRange = 0;
    public int rotatePlayerId = 0;
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
    public void addPlayerCenterLerp(double lerp) {
        this.playerCenterLerp = lerp;
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
    // build rotation
    public void addFixedRotate(double degree) {
        this.endRotationType = EndRotationType.FIXED;
        this.endRotateDegree = degree;
    }
    public void addPreviousRotate(int prevZoneId, double progress) {
        this.endRotationType = EndRotationType.PREVIOUS;
        this.endRotateZoneId = prevZoneId;
        this.endRotateProgress = GameZone.allowedProgress(progress);
    }
    public void addRelativeRotate(double degree) {
        this.endRotationType = EndRotationType.RELATIVE;
        this.endRotateDegree = degree;
    }
    public void addRotateScale(double scale) {
        this.endRotateScale = scale;
    }
    public void addRotateRange(double range) {
        this.endRotateRange = range;
    }
    public void addLockRotate(int playerId) {
        this.endRotationType = EndRotationType.LOCK_PLAYER;
        this.rotatePlayerId = playerId;
    }

    @Nullable
    public static EndEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.CENTER, null);
        JsonObject dimensionObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.DIMENSION, null);
        JsonObject rotationObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.ROTATION, null);
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("EndEntry missing member center or dimension, skipped");
            return null;
        }

        EndCenterType centerType = EndCenterType.fromValue(JsonUtils.getJsonString(centerObject, ZoneShapeTag.CENTER_TYPE, ""));
        EndDimensionType dimensionType = EndDimensionType.fromValue(JsonUtils.getJsonString(dimensionObject, ZoneShapeTag.DIMENSION_TYPE, ""));
        EndRotationType rotationType = EndRotationType.fromValue(JsonUtils.getJsonString(rotationObject, ZoneShapeTag.ROTATION_TYPE, ""));
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
                    Vec3 centerPos = JsonUtils.getJsonVec(centerObject, ZoneShapeTag.RELATIVE, Vec3.ZERO);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Invalid end relative center, defaulting to zero vec");
                        centerPos = Vec3.ZERO;
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
        double playerCenterLerp = JsonUtils.getJsonDouble(centerObject, ZoneShapeTag.PLAYER_CENTER_LERP, 0);
        endEntry.addPlayerCenterLerp(playerCenterLerp);

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
                    Vec3 dimension = JsonUtils.getJsonVec(dimensionObject, ZoneShapeTag.RELATIVE, Vec3.ZERO);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Invalid end relative dimension, defaulting to zero vec");
                        dimension = Vec3.ZERO;
                    }
                    endEntry.addRelativeDimension(dimension);
                }
            }
        }
        double dimensionRange = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.RANDOM_RANGE, 0);
        endEntry.addDimensionRange(dimensionRange);

        // rotation
        if (rotationType != null && rotationObject != null) {
            switch (rotationType) {
                case FIXED -> endEntry.addFixedRotate(JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.FIXED, 0));
                case PREVIOUS, RELATIVE -> {
                    int rotateZoneId = JsonUtils.getJsonInt(rotationObject, ZoneShapeTag.PREVIOUS_ID, -1);
                    double rotateScale = JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.PREVIOUS_SCALE, 1);
                    if (rotateZoneId < 0) {
                        BattleRoyale.LOGGER.info("Skipped invalid end rotate zone id: {}", rotateZoneId);
                        return null;
                    } else if (rotateScale < 0) {
                        BattleRoyale.LOGGER.info("Invalid end previous rotate scale {}, defaulting to 0", rotateScale);
                        rotateScale = 0;
                    }
                    double rotateProgress = JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.PREVIOUS_PROGRESS, 0);
                    endEntry.addPreviousRotate(rotateZoneId, rotateProgress);
                    endEntry.addRotateScale(rotateScale);
                    if (rotationType == EndRotationType.RELATIVE) {
                        endEntry.addRelativeRotate(JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.RELATIVE, 0));
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = JsonUtils.getJsonInt(rotationObject, ZoneShapeTag.PLAYER_ID, 0);
                    if (playerId < 0) {
                        BattleRoyale.LOGGER.info("Invalid rotatePlayerId {}, defaulting to 0 (random select)", playerId);
                        playerId = 0;
                    }
                    endEntry.addLockRotate(playerId);
                }
            }
        }
        double rotateRange = JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.RANDOM_RANGE, 0);
        endEntry.addRotateRange(rotateRange);

        return endEntry;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonObject centerObject = generateCenterJson();
        jsonObject.add(ZoneShapeTag.CENTER, centerObject);

        JsonObject dimensionObject = generateDimensionJson();
        jsonObject.add(ZoneShapeTag.DIMENSION, dimensionObject);

        JsonObject rotationObject = generateRotationJson();
        jsonObject.add(ZoneShapeTag.ROTATION, rotationObject);

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
        centerObject.addProperty(ZoneShapeTag.PLAYER_CENTER_LERP, playerCenterLerp);
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

    @NotNull
    private JsonObject generateRotationJson() {
        JsonObject rotationObject = new JsonObject();
        rotationObject.addProperty(ZoneShapeTag.ROTATION_TYPE, endRotationType.getValue());
        switch (endRotationType) {
            case FIXED -> rotationObject.addProperty(ZoneShapeTag.FIXED, endRotateDegree);
            case PREVIOUS, RELATIVE -> {
                rotationObject.addProperty(ZoneShapeTag.PREVIOUS_ID, endRotateZoneId);
                rotationObject.addProperty(ZoneShapeTag.PREVIOUS_PROGRESS, endRotateProgress);
                rotationObject.addProperty(ZoneShapeTag.PREVIOUS_SCALE, endRotateScale);
                if (endRotationType == EndRotationType.RELATIVE) {
                    rotationObject.addProperty(ZoneShapeTag.RELATIVE, endRotateDegree);
                }
            }
            case LOCK_PLAYER -> rotationObject.addProperty(ZoneShapeTag.PLAYER_ID, rotatePlayerId);
        }
        rotationObject.addProperty(ZoneShapeTag.RANDOM_RANGE, endRotateRange);
        return rotationObject;
    }
}