package xiao.battleroyale.common.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.GameTag;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 各功能圈通用的部分
 */
public class GameZone implements IGameZone {
    private final int zoneId;
    private final String zoneName;
    private final String zoneColor;
    private final int zoneDelay;
    private final int zoneTime;

    private ITickableZone tickableZone;
    private ISpatialZone spatialZone;

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
    public void createZone(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        if (!created) {
            tickableZone.initFunc(serverLevel, gamePlayerList, gameZones, random);
            spatialZone.calculateShape(serverLevel, gamePlayerList, gameZones, random);
        }
        if (tickableZone.isReady() && spatialZone.isDetermined()) {
            created = true;
        } else {
            BattleRoyale.LOGGER.warn("Failed to create zone, finished");
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

    private boolean shouldTick() {
        return isCreated() && isPresent() && !isFinished(); // GameZone
    }

    @Override
    public void tick(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {
        if (!shouldTick()) {
            return;
        }
        double progress = tickableZone.getShapeProgress(gameTime, zoneDelay);
        if (progress != prevShapeProgress || gameTime % 20 == 0) { // 同步客户端
            prevShapeProgress = progress;
            CompoundTag nbt = toNBT(progress);
        }
        tickableZone.tick(serverLevel, gamePlayerList, gameZones, random, gameTime);
        if (gameTime >= zoneDelay + zoneTime) { // 圈存在时间取决于GameZone，代替shape以实现停留在终点位置
            present = false;
            finished = true;
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
    public CompoundTag toNBT(double progress) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(GameTag.ZONE_ID, zoneId);
        tag.putString(GameTag.ZONE_NAME, zoneName);
        tag.putString(GameTag.ZONE_COLOR, zoneColor);
        tag.putString(GameTag.FUNC, tickableZone.getFuncType().getName());
        tag.putString(GameTag.SHAPE, spatialZone.getShapeType().getName());
        Vec3 center = spatialZone.getCenterPos(progress);
        Vec3 dim = spatialZone.getDimension(progress);
        if (center != null && dim != null) {
            tag.putString(GameTag.CENTER, center.toString());
            tag.putString(GameTag.DIMENSION, dim.toString());
        } else {
            BattleRoyale.LOGGER.warn("Failed to parse zone center or dim, skipped");
        }
        return tag;
    }

    // ISpatialZone
    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        return spatialZone.isWithinZone(checkPos, progress);
    }
    @Override
    public ZoneShapeType getShapeType() { return spatialZone.getShapeType(); }
    @Override
    public void calculateShape(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        spatialZone.calculateShape(serverLevel, gamePlayerList, gameZones, random);
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
}
