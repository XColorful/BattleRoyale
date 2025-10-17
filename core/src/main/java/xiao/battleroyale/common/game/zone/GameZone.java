package xiao.battleroyale.common.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.zone.ZoneCompleteEvent;
import xiao.battleroyale.api.event.game.zone.ZoneCreatedEvent;
import xiao.battleroyale.api.game.zone.ZoneConfigTag;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.NBTUtils;
import xiao.battleroyale.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 各功能圈通用的部分
 */
public class GameZone implements IGameZone {

    // common
    private static final String COMMON_TAG = "common";
    private static final String CREATE_TIME = COMMON_TAG + "-worldTime";
    private static final String ZONE_NAME_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_NAME;
    private static final String ZONE_COLOR_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_COLOR;
    private static final String ZONE_DELAY_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_DELAY;
    private static final String ZONE_TIME_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_TIME;
    // func
    private static final String FUNC_TAG = "func";
    private static final String FUNC_TYPE_TAG = FUNC_TAG + "-" + ZoneFuncTag.TYPE_NAME;
    private static final String FUNC_MOVE_DELAY_TAG = FUNC_TAG + "-" + ZoneFuncTag.MOVE_DELAY;
    private static final String FUNC_MOVE_TIME_TAG = FUNC_TAG + "-" + ZoneFuncTag.MOVE_TIME;
    private static final String FUNC_TICK_FREQUENCY_TAG = FUNC_TAG + "-" + ZoneFuncTag.TICK_FREQUENCY;
    private static final String FUNC_TICK_OFFSET_TAG = FUNC_TAG + "-" + ZoneFuncTag.TICK_OFFSET;
    // shape
    private static final String SHAPE_TAG = "shape";
    private static final String SHAPE_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.TYPE_NAME;
    private static final String SHAPE_START_CENTER_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.START + "-" + ZoneShapeTag.CENTER_TYPE;
    private static final String SHAPE_START_CENTER_TAG = SHAPE_TAG + "-" + ZoneShapeTag.START + "-" + ZoneShapeTag.CENTER;
    private static final String SHAPE_START_DIMENSION_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.START + "-" + ZoneShapeTag.DIMENSION_TYPE;
    private static final String SHAPE_START_DIMENSION_TAG = SHAPE_TAG + "-" + ZoneShapeTag.START + "-" + ZoneShapeTag.DIMENSION;
    private static final String SHAPE_START_ROTATION_TAG = SHAPE_TAG + "-" + ZoneShapeTag.START + "-" + ZoneShapeTag.ROTATION;
    private static final String SHAPE_START_ROTATION_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.START + "-" + ZoneShapeTag.ROTATION_TYPE;
    private static final String SHAPE_END_CENTER_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.CENTER_TYPE;
    private static final String SHAPE_END_CENTER_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.CENTER;
    private static final String SHAPE_END_DIMENSION_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.DIMENSION_TYPE;
    private static final String SHAPE_END_DIMENSION_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.DIMENSION;
    private static final String SHAPE_END_ROTATION_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.ROTATION;
    private static final String SHAPE_END_ROTATION_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.ROTATION_TYPE;
    private static final String SHAPE_HAS_BAD_SHAPE = SHAPE_TAG + "-hasBadShape";
    private static final String SHAPE_SEGMENTS = SHAPE_TAG + "-" + ZoneShapeTag.SEGMENTS;

    public static final int FORCE_SYNC_FREQUENCY = 20 * 3; // 3秒强制通信

    private final int zoneId;
    private final String zoneName;
    private final String zoneColor; // 格式如 #0000FF
    private final int preZoneDelayId;
    private int zoneDelay;
    private final int zoneTime;

    private final ITickableZone tickableZone;
    private final ISpatialZone spatialZone;

    private boolean created = false;
    private boolean present = false;
    private boolean finished = false;
    private double prevShapeProgress = -1;

    // 构造函数，由 Builder 调用
    public GameZone(int zoneId, String zoneName, String zoneColor, int preZoneDelayId, int zoneDelay, int zoneTime,
                    ITickableZone tickableZone, ISpatialZone spatialZone) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.zoneColor = zoneColor;
        this.preZoneDelayId = preZoneDelayId;
        this.zoneDelay = zoneDelay;
        this.zoneTime = zoneTime;
        this.tickableZone = tickableZone;
        this.spatialZone = spatialZone;
    }

    @Override
    public void createZone(ZoneContext zoneContext) {
        if (!created) {
            tickableZone.initFunc(zoneContext);
            spatialZone.calculateShape(zoneContext);
        }
        if (tickableZone.isReady() && spatialZone.isDetermined()) {
            addZoneDetailProperty();
            created = true;
            present = true;
            EventPoster.postEvent(new ZoneCreatedEvent(GameManager.get(), this, true));
        } else {
            addFailedZoneProperty();
            present = false;
            finished = true;
            EventPoster.postEvent(new ZoneCreatedEvent(GameManager.get(), this, false));
        }
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public boolean isPresent() {
        return present;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    private boolean shouldTick(int gameTime) {
        return !checkShouldFinish(gameTime)
                && isCreated() && isPresent() && !isFinished() // GameZone
                && !ZoneManager.shouldStopGame; // 每tick多一个bool检查，少一个列表对象复制（直接用视图遍历）
    }

    private boolean checkShouldFinish(int gameTime) {
        if (gameTime > zoneDelay + zoneTime) { // 圈存在时间取决于GameZone，代替shape以实现停留在终点位置
            present = false;
            finished = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void gameTick(ZoneContext zoneContext) {
        if (!shouldTick(zoneContext.gameTime)) {
            GameMessageManager.addZoneNbtMessage(this.zoneId, null); // 传入null视为提醒置空NBT
            EventPoster.postEvent(new ZoneCompleteEvent(GameManager.get(), this));
            return;
        }

        double shapeProgress = tickableZone.getShapeProgress(zoneContext.gameTime, zoneDelay);
        // 同步客户端
        if (Math.abs(shapeProgress - prevShapeProgress) > 0.001F) { // 圈在移动，频繁更新
            prevShapeProgress = shapeProgress;
            CompoundTag zoneInfo = toNBT(shapeProgress);
            GameMessageManager.addZoneNbtMessage(this.zoneId, zoneInfo);
        } else if (zoneContext.gameTime % FORCE_SYNC_FREQUENCY == 0) { // 圈不在频繁移动，延长时间
            MessageManager.get().extendZoneMessageTime(zoneId, FORCE_SYNC_FREQUENCY);
        }
        if ((zoneContext.gameTime + getTickOffset()) % getTickFrequency() == 0) {
            funcTick(new ZoneTickContext(zoneContext, zoneId, shapeProgress, spatialZone));
        }
    }

    @Override
    public int getZoneId() {
        return zoneId;
    }

    @Override
    public int previousZoneDelayId() {
        return preZoneDelayId;
    }

    @Override
    public void setZoneDelay(int zoneDelay) {
        this.zoneDelay = zoneDelay;
    }

    @Override
    public int getZoneDelay() {
        return zoneDelay;
    }

    @Override
    public String getZoneName() { return zoneName; }

    @Override
    public String getZoneColor() { return zoneColor; }

    @Override
    public CompoundTag toNBT(double shapeProgress) {
        return NBTUtils.serializeZoneToNBT(
                this.zoneId,
                this.zoneName,
                this.zoneColor,
                this.tickableZone,
                this.spatialZone,
                shapeProgress
        );
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        tickableZone.funcTick(zoneTickContext);
    }

    @Override
    public int getTickFrequency() { return tickableZone.getTickFrequency(); }
    @Override
    public void setTickFrequency(int tickFreq) { tickableZone.setTickFrequency(tickFreq);}
    @Override
    public int getTickOffset() { return tickableZone.getTickOffset(); }
    @Override
    public void setTickOffset(int tickOff) { tickableZone.setTickOffset(tickOff); }

    private void addZoneDetailProperty() {
        Map<String, Integer> intWriter = new HashMap<>();
        Map<String, Boolean> boolWriter = new HashMap<>();
        Map<String, Double> doubleWriter = new HashMap<>();
        Map<String, String> stringWriter = new HashMap<>();

        // common
        intWriter.put(CREATE_TIME, GameManager.get().getGameTime());
        stringWriter.put(ZONE_NAME_TAG, zoneName);
        stringWriter.put(ZONE_COLOR_TAG, zoneColor);
        intWriter.put(ZONE_DELAY_TAG, zoneDelay);
        intWriter.put(ZONE_TIME_TAG, zoneTime);
        // func
        stringWriter.put(FUNC_TYPE_TAG, getFuncType().getName());
        intWriter.put(FUNC_MOVE_DELAY_TAG, getShapeMoveDelay());
        intWriter.put(FUNC_MOVE_TIME_TAG, getShapeMoveTime());
        intWriter.put(FUNC_TICK_FREQUENCY_TAG, getTickFrequency());
        intWriter.put(FUNC_TICK_OFFSET_TAG, getTickOffset());
        // shape
        stringWriter.put(SHAPE_TYPE_TAG, getShapeType().getName());
        stringWriter.put(SHAPE_START_CENTER_TAG, StringUtils.vectorToString(getStartCenterPos()));
        stringWriter.put(SHAPE_START_DIMENSION_TAG, StringUtils.vectorToString(getStartDimension()));
        doubleWriter.put(SHAPE_START_ROTATION_TAG, getStartRotateDegree());
        stringWriter.put(SHAPE_END_CENTER_TAG, StringUtils.vectorToString(getEndCenterPos()));
        stringWriter.put(SHAPE_END_DIMENSION_TAG, StringUtils.vectorToString(getEndDimension()));
        doubleWriter.put(SHAPE_END_ROTATION_TAG, getEndRotateDegree());
        boolWriter.put(SHAPE_HAS_BAD_SHAPE, hasBadShape());
        intWriter.put(SHAPE_SEGMENTS, getSegments());

        GameStatsManager.recordZoneInt(this.zoneId, intWriter);
        GameStatsManager.recordZoneBool(this.zoneId, boolWriter);
        GameStatsManager.recordZoneDouble(this.zoneId, doubleWriter);
        GameStatsManager.recordZoneString(this.zoneId, stringWriter);
    }
    private void addFailedZoneProperty() {
        Map<String, Integer> intWriter = new HashMap<>();
        Map<String, String> stringWriter = new HashMap<>();

        // common
        intWriter.put(CREATE_TIME, GameManager.get().getGameTime());
        stringWriter.put(ZONE_NAME_TAG, zoneName);

        GameStatsManager.recordZoneInt(this.zoneId, intWriter);
        GameStatsManager.recordZoneString(this.zoneId, stringWriter);
    }

    public static double allowedProgress(double progress) {
        return Math.min(Math.max(0, progress), 1);
    }

    // ISpatialZone
    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        return spatialZone.isWithinZone(checkPos, progress);
    }
    @Override
    public ZoneShapeType getShapeType() { return spatialZone.getShapeType(); }
    @Override
    public void calculateShape(@NotNull ZoneContext zoneContext) {
        spatialZone.calculateShape(zoneContext);
    }
    @Override
    public boolean isDetermined() { return spatialZone.isDetermined(); }
    @Nullable
    public @Override Vec3 getStartCenterPos() { return spatialZone.getStartCenterPos(); }
    @Override
    public @Nullable Vec3 getCenterPos(double progress) { return spatialZone.getCenterPos(progress); }
    @Override
    public @Nullable Vec3 getEndCenterPos() { return spatialZone.getEndCenterPos(); }
    @Override
    public @Nullable Vec3 getStartDimension() { return spatialZone.getStartDimension(); }
    @Override
    public @Nullable Vec3 getDimension(double progress) { return spatialZone.getDimension(progress); }
    @Override
    public @Nullable Vec3 getEndDimension() { return spatialZone.getEndDimension(); }
    @Override
    public double getStartRotateDegree() { return spatialZone.getStartRotateDegree(); }
    @Override
    public double getRotateDegree(double progress) { return spatialZone.getRotateDegree(progress); }
    @Override
    public double getEndRotateDegree() { return spatialZone.getEndRotateDegree(); }

    @Override
    public boolean hasBadShape() {
        return spatialZone.hasBadShape();
    }

    @Override
    public int getSegments() {
        return spatialZone.getSegments();
    }

    // ITickableZone
    @Override
    public void initFunc(ZoneContext zoneContext) {
        tickableZone.initFunc(zoneContext);
    }
    @Override
    public boolean isReady() { return tickableZone.isReady(); }
    @Override
    public ZoneFuncType getFuncType() { return tickableZone.getFuncType(); }
    @Override
    public double getShapeProgress(int currentGameTime, int zoneDelay) { return tickableZone.getShapeProgress(currentGameTime, zoneDelay); }
    @Override
    public int getShapeMoveDelay() { return tickableZone.getShapeMoveDelay(); }
    @Override
    public int getShapeMoveTime() { return tickableZone.getShapeMoveTime(); }
}
