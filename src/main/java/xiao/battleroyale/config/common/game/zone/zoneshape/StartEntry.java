package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartRotationType;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class StartEntry {

    public StartCenterType startCenterType = StartCenterType.FIXED;
    public Vec3 startCenterPos = Vec3.ZERO; // fixed x, z
    public int startCenterZoneId = 0; // previous zone id
    public double startCenterProgress = 1;
    public double startCenterRange = 0;
    public int centerPlayerId = 0;
    public boolean selectStanding = false;

    public StartDimensionType startDimensionType = StartDimensionType.FIXED;
    public Vec3 startDimension = Vec3.ZERO; // radius / side / a, b
    public int startDimensionZoneId = 0; // previous zone id
    public double startDimensionProgress = 1;
    public double startDimensionScale = 1; // previous dimension scale
    public double startDimensionRange = 0;

    public StartRotationType startRotationType = StartRotationType.FIXED;
    public double startRotateDegree = 0;
    public int startRotateZoneId = 0;
    public double startRotateProgress = 1;
    public double startRotateScale = 1;
    public double startRotateRange = 0;
    public int rotatePlayerId = 0;
    // 淘汰的玩家就没必要选择了

    public StartEntry() {
        ;
    }
    // build center
    public void addFixedCenter(Vec3 startCenterPos) {
        this.startCenterType = StartCenterType.FIXED;
        this.startCenterPos = startCenterPos;
    }
    public void addPreviousCenter(int prevZoneId, double progress) {
        this.startCenterType = StartCenterType.PREVIOUS;
        this.startCenterZoneId = prevZoneId;
        this.startCenterProgress = GameZone.allowedProgress(progress);
    }
    public void addRelativeCenter(Vec3 relativeAdd) {
        this.startCenterType = StartCenterType.RELATIVE;
        this.startCenterPos = relativeAdd;
    }
    public void addLockCenter(int playerId, boolean selectStanding) {
        this.startCenterType = StartCenterType.LOCK_PLAYER;
        this.centerPlayerId = playerId;
        this.selectStanding = selectStanding;
    }
    public void addCenterRange(double range) {
        this.startCenterRange = range;
    }
    // build dimension
    public void addFixedDimension(Vec3 startDimension) {
        this.startDimensionType = StartDimensionType.FIXED;
        this.startDimension = startDimension;
    }
    public void addPreviousDimension(int prevZoneId, double progress) {
        this.startDimensionType = StartDimensionType.PREVIOUS;
        this.startDimensionZoneId = prevZoneId;
        this.startDimensionProgress = GameZone.allowedProgress(progress);
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
    // build rotation
    public void addFixedRotate(double degree) {
        this.startRotationType = StartRotationType.FIXED;
        this.startRotateDegree = degree;
    }
    public void addPreviousRotate(int prevZoneId, double progress) {
        this.startRotationType = StartRotationType.PREVIOUS;
        this.startRotateZoneId = prevZoneId;
        this.startRotateProgress = GameZone.allowedProgress(progress);
    }
    public void addRelativeRotate(double degree) {
        this.startRotationType = StartRotationType.RELATIVE;
        this.startRotateDegree = degree;
    }
    public void addRotateScale(double scale) {
        this.startRotateScale = scale;
    }
    public void addRotateRange(double range) {
        this.startRotateRange = range;
    }
    public void addLockRotate(int playerId) {
        this.startRotationType = StartRotationType.LOCK_PLAYER;
        this.rotatePlayerId = playerId;
    }

    @Nullable
    public static StartEntry fromJson(JsonObject jsonObject) {
        JsonObject centerObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.CENTER, null);
        JsonObject dimensionObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.DIMENSION, null);
        JsonObject rotationObject = JsonUtils.getJsonObject(jsonObject, ZoneShapeTag.ROTATION, null);
        if (centerObject == null || dimensionObject == null) {
            BattleRoyale.LOGGER.info("StartEntry missing center or dimension member, skipped");
            return null;
        }

        StartCenterType centerType = StartCenterType.fromValue(JsonUtils.getJsonString(centerObject, ZoneShapeTag.CENTER_TYPE, ""));
        StartDimensionType dimensionType = StartDimensionType.fromValue(JsonUtils.getJsonString(dimensionObject, ZoneShapeTag.DIMENSION_TYPE, ""));
        StartRotationType rotationType = StartRotationType.fromValue(JsonUtils.getJsonString(rotationObject, ZoneShapeTag.ROTATION_TYPE, ""));
        if (centerType == null || dimensionType == null) {
            BattleRoyale.LOGGER.info("Skipped invalid start centerType or dimensionType");
            return null;
        }
        StartEntry startEntry = new StartEntry();

        // center
        switch (centerType) {
            case FIXED -> {
                Vec3 centerPos = JsonUtils.getJsonVec(centerObject, ZoneShapeTag.FIXED, null);
                if (centerPos == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start fixed centerPos");
                    return null;
                }
                startEntry.addFixedCenter(centerPos);
            }
            case PREVIOUS, RELATIVE -> {
                int centerZoneId = JsonUtils.getJsonInt(centerObject, ZoneShapeTag.PREVIOUS_ID, -1);
                if (centerZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous center zone id");
                    return null;
                }
                double centerProgress = JsonUtils.getJsonDouble(centerObject, ZoneShapeTag.PREVIOUS_PROGRESS, 1);
                startEntry.addPreviousCenter(centerZoneId, centerProgress);
                if (centerType == StartCenterType.RELATIVE) {
                    Vec3 centerPos = JsonUtils.getJsonVec(centerObject, ZoneShapeTag.RELATIVE, Vec3.ZERO);
                    if (centerPos == null) {
                        BattleRoyale.LOGGER.info("Invalid start relative center, defaulting to zero vec");
                        centerPos = Vec3.ZERO;
                    }
                    startEntry.addRelativeCenter(centerPos);
                }
            }
            case LOCK_PLAYER -> {
                int playerId = JsonUtils.getJsonInt(centerObject, ZoneShapeTag.PLAYER_ID, 0);
                if (playerId < 0) {
                    BattleRoyale.LOGGER.info("Invalid centerPlayerId {}, defaulting to 0 (random select)", playerId);
                    playerId = 0;
                }
                boolean selectStanding = JsonUtils.getJsonBoolean(centerObject, ZoneShapeTag.SELECT_STANDING, false);
                startEntry.addLockCenter(playerId, selectStanding);
            }
        }
        double centerRange = JsonUtils.getJsonDouble(centerObject, ZoneShapeTag.RANDOM_RANGE, 0);
        startEntry.addCenterRange(centerRange);

        // dimension
        switch (dimensionType) {
            case FIXED -> {
                Vec3 dimension = JsonUtils.getJsonVec(dimensionObject, ZoneShapeTag.FIXED, null);
                if (dimension == null) {
                    BattleRoyale.LOGGER.info("Skipped invalid start fixed dimension");
                    return null;
                }
                startEntry.addFixedDimension(dimension);
            }
            case PREVIOUS, RELATIVE -> {
                int dimensionZoneId = JsonUtils.getJsonInt(dimensionObject, ZoneShapeTag.PREVIOUS_ID, -1);
                double dimensionScale = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.PREVIOUS_SCALE, 1);
                if (dimensionZoneId < 0) {
                    BattleRoyale.LOGGER.info("Skipped invalid start previous dimension zone id: {}", dimensionZoneId);
                    return null;
                } else if (dimensionScale < 0) {
                    BattleRoyale.LOGGER.info("Invalid start previous dimension scale {}, defaulting to 0", dimensionScale);
                    dimensionScale = 0;
                }
                double dimensionProgress = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.PREVIOUS_PROGRESS, 1);
                startEntry.addPreviousDimension(dimensionZoneId, dimensionProgress);
                startEntry.addDimensionScale(dimensionScale);
                if (dimensionType == StartDimensionType.RELATIVE) {
                    Vec3 dimension = JsonUtils.getJsonVec(dimensionObject, ZoneShapeTag.RELATIVE, Vec3.ZERO);
                    if (dimension == null) {
                        BattleRoyale.LOGGER.info("Invalid start relative dimension, defaulting to zero vec");
                        dimension = Vec3.ZERO;
                    }
                    startEntry.addRelativeDimension(dimension);
                }
            }
        }
        double dimensionRange = JsonUtils.getJsonDouble(dimensionObject, ZoneShapeTag.RANDOM_RANGE, 0);
        startEntry.addDimensionRange(dimensionRange);

        // rotation
        if (rotationType != null && rotationObject != null) {
            switch (rotationType) {
                case FIXED -> startEntry.addFixedRotate(JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.FIXED, 0));
                case PREVIOUS, RELATIVE -> {
                    int rotateZoneId = JsonUtils.getJsonInt(rotationObject, ZoneShapeTag.PREVIOUS_ID, -1);
                    double rotateScale = JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.PREVIOUS_SCALE, 1);
                    if (rotateZoneId < 0) {
                        BattleRoyale.LOGGER.info("Skipped invalid start rotate zone id: {}", rotateZoneId);
                        return null;
                    } else if (rotateScale < 0) {
                        BattleRoyale.LOGGER.info("Invalid start previous rotate scale {}, defaulting to 0", rotateScale);
                        rotateScale = 0;
                    }
                    double rotateProgress = JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.PREVIOUS_PROGRESS, 1);
                    startEntry.addPreviousRotate(rotateZoneId, rotateProgress);
                    startEntry.addRotateScale(rotateScale);
                    if (rotationType == StartRotationType.RELATIVE) {
                        startEntry.addRelativeRotate(JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.RELATIVE, 0));
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = JsonUtils.getJsonInt(rotationObject, ZoneShapeTag.PLAYER_ID, 0);
                    if (playerId < 0) {
                        BattleRoyale.LOGGER.info("Invalid rotatePlayerId {}, defaulting to 0 (random select)", playerId);
                        playerId = 0;
                    }
                    startEntry.addLockRotate(playerId);
                }
            }
        }
        double rotateRange = JsonUtils.getJsonDouble(rotationObject, ZoneShapeTag.RANDOM_RANGE, 0);
        startEntry.addRotateRange(rotateRange);

        return startEntry;
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
                centerObject.addProperty(ZoneShapeTag.PLAYER_ID, centerPlayerId);
                centerObject.addProperty(ZoneShapeTag.SELECT_STANDING, selectStanding);
            }
        }
        centerObject.addProperty(ZoneShapeTag.RANDOM_RANGE, startCenterRange);
        return centerObject;
    }

    @NotNull
    private JsonObject generateDimensionJson() {
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

    @NotNull
    private JsonObject generateRotationJson() {
        JsonObject rotationObject = new JsonObject();
        rotationObject.addProperty(ZoneShapeTag.ROTATION_TYPE, startRotationType.getValue());
        switch (startRotationType) {
            case FIXED -> rotationObject.addProperty(ZoneShapeTag.FIXED, startRotateDegree);
            case PREVIOUS, RELATIVE -> {
                rotationObject.addProperty(ZoneShapeTag.PREVIOUS_ID, startRotateZoneId);
                rotationObject.addProperty(ZoneShapeTag.PREVIOUS_PROGRESS, startRotateProgress);
                rotationObject.addProperty(ZoneShapeTag.PREVIOUS_SCALE, startRotateScale);
                if (startRotationType == StartRotationType.RELATIVE) {
                    rotationObject.addProperty(ZoneShapeTag.RELATIVE, startRotateDegree);
                }
            }
            case LOCK_PLAYER -> rotationObject.addProperty(ZoneShapeTag.PLAYER_ID, rotatePlayerId);
        }
        rotationObject.addProperty(ZoneShapeTag.RANDOM_RANGE, startRotateRange);
        return rotationObject;
    }
}