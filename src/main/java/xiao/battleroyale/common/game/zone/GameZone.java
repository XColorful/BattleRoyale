package xiao.battleroyale.common.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.ZoneConfigTag;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.NBTUtils;
import xiao.battleroyale.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 各功能圈通用的部分
 */
public class GameZone implements IGameZone {

    // common
    private static final String COMMON_TAG = "common";
    private static final String CREATE_TIME = COMMON_TAG + "-createTime";
    private static final String ZONE_NAME_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_NAME;
    private static final String ZONE_COLOR_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_COLOR;
    private static final String ZONE_DELAY_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_DELAY;
    private static final String ZONE_TIME_TAG = COMMON_TAG + "-" + ZoneConfigTag.ZONE_TIME;
    // func
    private static final String FUNC_TAG = "func";
    private static final String FUNC_TYPE_TAG = FUNC_TAG + "-" + ZoneFuncTag.TYPE_NAME;
    private static final String FUNC_DAMAGE_TAG = FUNC_TAG + "-" + ZoneFuncTag.DAMAGE;
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
    private static final String SHAPE_END_CENTER_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.CENTER_TYPE;
    private static final String SHAPE_END_CENTER_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.CENTER;
    private static final String SHAPE_END_DIMENSION_TYPE_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.DIMENSION_TYPE;
    private static final String SHAPE_END_DIMENSION_TAG = SHAPE_TAG + "-" + ZoneShapeTag.END + "-" + ZoneShapeTag.DIMENSION;
    private static final String SHAPE_SEGMENTS = SHAPE_TAG + "-" + ZoneShapeTag.SEGMENTS;

    private final int zoneId;
    private final String zoneName;
    private final String zoneColor; // 格式如 #0000FF
    private final int zoneDelay;
    private final int zoneTime;

    private final ITickableZone tickableZone;
    private final ISpatialZone spatialZone;

    private boolean created = false;
    private boolean present = false;
    private boolean finished = false;
    private double prevShapeProgress = -1;

    // 构造函数，由 Builder 调用
    public GameZone(int zoneId, String zoneName, String zoneColor, int zoneDelay, int zoneTime,
                    ITickableZone tickableZone, ISpatialZone spatialZone) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.zoneColor = zoneColor;
        this.zoneDelay = zoneDelay;
        this.zoneTime = zoneTime;
        this.tickableZone = tickableZone;
        this.spatialZone = spatialZone;
    }

    @Override
    public void createZone(ServerLevel serverLevel, List<GamePlayer> standingGamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        if (!created) {
            tickableZone.initFunc(serverLevel, standingGamePlayers, gameZones, random);
            spatialZone.calculateShape(serverLevel, standingGamePlayers, random);
        }
        if (tickableZone.isReady() && spatialZone.isDetermined()) {
            addZoneDetailProperty();
            created = true;
            present = true;
        } else {
            addFailedZoneProperty();
            present = false;
            finished = true;
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
        return isCreated() && isPresent() && !isFinished(); // GameZone
    }

    /**
     * @param serverLevel 当前世界
     * @param gamePlayerList 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例，但通常圈自身逻辑与其他圈无关
     * @param random 随机数生产者
     * @param gameTime 游戏进行时间
     */
    @Override
    public void tick(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {
        if (!shouldTick(gameTime)) {
            return;
        }

        if (gameTime > zoneDelay + zoneTime) { // 圈存在时间取决于GameZone，代替shape以实现停留在终点位置
            present = false;
            finished = true;
            GameManager.get().addZoneInfo(this.zoneId, null); // 传入null视为提醒置空NBT
            return;
        }

        double progress = tickableZone.getShapeProgress(gameTime, zoneDelay);
        if (Math.abs(progress - prevShapeProgress) > 0.001F || gameTime % 20 == 0) { // 同步客户端，最多1秒间隔
            prevShapeProgress = progress;
            CompoundTag zoneInfo = toNBT(progress);
            GameManager.get().addZoneInfo(this.zoneId, zoneInfo);
        }
        if ((gameTime + getTickOffset()) % getTickFrequency() == 0) {
            tick(serverLevel, gamePlayerList, gameZones, random, gameTime, progress, spatialZone);
        }
    }

    @Override
    public int getZoneId() {
        return zoneId;
    }

    @Override
    public int getZoneDelay() {
        return zoneDelay;
    }

    @Override
    public String getZoneName() { return zoneName; }

    @Override
    public CompoundTag toNBT(double progress) {
        return NBTUtils.serializeZoneToNBT(
                this.zoneId,
                this.zoneName,
                this.zoneColor,
                this.tickableZone,
                this.spatialZone,
                progress
        );
    }

    @Override
    public void tick(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random,
                     int gameTime, double progress, ISpatialZone spatialZone) {
        tickableZone.tick(serverLevel, gamePlayerList, gameZones, random, gameTime, progress, spatialZone);
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
        doubleWriter.put(FUNC_DAMAGE_TAG, getDamage());
        intWriter.put(FUNC_MOVE_DELAY_TAG, getShapeMoveDelay());
        intWriter.put(FUNC_MOVE_TIME_TAG, getShapeMoveTime());
        intWriter.put(FUNC_TICK_FREQUENCY_TAG, getTickFrequency());
        intWriter.put(FUNC_TICK_OFFSET_TAG, getTickOffset());
        // shape
        stringWriter.put(SHAPE_TYPE_TAG, getShapeType().getName());
        stringWriter.put(SHAPE_START_CENTER_TAG, StringUtils.vectorToString(getStartCenterPos()));
        stringWriter.put(SHAPE_START_DIMENSION_TAG, StringUtils.vectorToString(getStartDimension()));
        stringWriter.put(SHAPE_END_CENTER_TAG, StringUtils.vectorToString(getEndCenterPos()));
        stringWriter.put(SHAPE_END_DIMENSION_TAG, StringUtils.vectorToString(getEndDimension()));
        intWriter.put(SHAPE_SEGMENTS, getSegments());

        GameManager.get().recordZoneInt(this.zoneId, intWriter);
        GameManager.get().recordZoneDouble(this.zoneId, doubleWriter);
        GameManager.get().recordZoneString(this.zoneId, stringWriter);
    }
    private void addFailedZoneProperty() {
        Map<String, Integer> intWriter = new HashMap<>();
        Map<String, String> stringWriter = new HashMap<>();

        // common
        intWriter.put(CREATE_TIME, GameManager.get().getGameTime());
        stringWriter.put(ZONE_NAME_TAG, zoneName);

        GameManager.get().recordZoneInt(this.zoneId, intWriter);
        GameManager.get().recordZoneString(this.zoneId, stringWriter);
    }

    // ISpatialZone
    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        return spatialZone.isWithinZone(checkPos, progress);
    }
    @Override
    public ZoneShapeType getShapeType() { return spatialZone.getShapeType(); }
    @Override
    public void calculateShape(ServerLevel serverLevel, List<GamePlayer> standingGamePlayers, Supplier<Float> random) {
        spatialZone.calculateShape(serverLevel, standingGamePlayers, random);
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
    public int getSegments() {
        return spatialZone.getSegments();
    }

    // ITickableZone
    @Override
    public void initFunc(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        tickableZone.initFunc(serverLevel, gamePlayerList, gameZones, random);
    }
    @Override
    public boolean isReady() { return tickableZone.isReady(); }
    @Override
    public ZoneFuncType getFuncType() { return tickableZone.getFuncType(); }
    @Override
    public double getDamage() { return tickableZone.getDamage(); }
    @Override
    public double getShapeProgress(int currentGameTime, int zoneDelay) { return tickableZone.getShapeProgress(currentGameTime, zoneDelay); }
    @Override
    public int getShapeMoveDelay() { return tickableZone.getShapeMoveDelay(); }
    @Override
    public int getShapeMoveTime() { return tickableZone.getShapeMoveTime(); }
}
