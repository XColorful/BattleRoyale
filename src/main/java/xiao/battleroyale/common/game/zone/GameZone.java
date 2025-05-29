package xiao.battleroyale.common.game.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.ZoneNBTSerializer;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 各功能圈通用的部分
 */
public class GameZone implements IGameZone {
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
    public void createZone(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        if (!created) {
            tickableZone.initFunc(serverLevel, gamePlayerList, gameZones, random);
            spatialZone.calculateShape(serverLevel, gamePlayerList, gameZones, random);
        }
        if (tickableZone.isReady() && spatialZone.isDetermined()) {
            created = true;
            present = true;
        } else {
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
     * 只关注
     * @param serverLevel 当前世界
     * @param gamePlayerList 当前游戏玩家列表
     * @param gameZones 当前游戏所有圈实例，但通常圈自身逻辑与其他圈无关
     * @param random 随机数生产者
     * @param gameTime 游戏进行时间
     */
    @Override
    public void tick(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {


        if (!shouldTick(gameTime)) {
            return;
        }

        double progress = tickableZone.getShapeProgress(gameTime, zoneDelay);
        if (Math.abs(progress - prevShapeProgress) > 0.001F || gameTime % 20 == 0) { // 同步客户端，最多1秒间隔
            prevShapeProgress = progress;
            CompoundTag zoneInfo = toNBT(progress);
            GameManager.get().addZoneInfo(this.zoneId, zoneInfo);
        }
        tickableZone.tick(serverLevel, gamePlayerList, gameZones, random, gameTime);
        if (gameTime > zoneDelay + zoneTime) { // 圈存在时间取决于GameZone，代替shape以实现停留在终点位置
            present = false;
            finished = true;
            GameManager.get().addZoneInfo(this.zoneId, null); // 传入null视为提醒置空NBT
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
        return ZoneNBTSerializer.serializeZoneToNBT(
                this.zoneId,
                this.zoneName,
                this.zoneColor,
                this.tickableZone,
                this.spatialZone,
                progress
        );
    }

    @Override
    public int getFuncFrequency() { return tickableZone.getFuncFrequency(); }
    @Override
    public void setFuncFrequency(int funcFreq) { tickableZone.setFuncFrequency(funcFreq);}
    @Override
    public int getFuncOffset() { return tickableZone.getFuncOffset(); }
    @Override
    public void setFuncOffset(int funcOff) { tickableZone.setFuncOffset(funcOff); }

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
