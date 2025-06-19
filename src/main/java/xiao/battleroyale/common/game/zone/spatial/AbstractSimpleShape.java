package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.util.Vec3Utils;

import static xiao.battleroyale.util.Vec3Utils.randomAdjustXZ;

import java.util.List;
import java.util.function.Supplier;


public abstract class AbstractSimpleShape implements ISpatialZone {

    protected StartEntry startEntry;
    protected EndEntry endEntry;

    protected Vec3 startCenter;
    protected Vec3 startDimension;
    protected Vec3 endCenter;
    protected Vec3 endDimension;

    protected Vec3 cachedCenter = Vec3.ZERO;
    protected Vec3 cachedDimension = Vec3.ZERO;
    protected double cachedProgress = -1;
    protected static final double EPSILON = 1.0E-9; // 移动30分钟的圈每tick的变化为 2.778 x 10^-5

    protected boolean determined = false;
    protected Vec3 centerDist;
    protected Vec3 dimensionDist;

    public AbstractSimpleShape(StartEntry startEntry, EndEntry endEntry) {
        this.startEntry = startEntry;
        this.endEntry = endEntry;
    }

    /**
     * 兼容正方形和矩形的判定
     * @param checkPos 待检查的玩家/人机位置
     * @param progress 进度，用于确定计算所需的圈的状态
     * @return 判定结果
     */
    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0) { // 进度小于0则为未创建
            return false;
        }
        if (!isDetermined()) {
            return false;
        }
        double allowedProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension;
        if (Math.abs(allowedProgress - cachedProgress) < EPSILON) {
            center = cachedCenter;
            dimension = cachedDimension;
        } else {
            center = getCenterPos(allowedProgress);
            dimension = getDimension(allowedProgress);
            cachedCenter = center;
            cachedDimension = dimension;
            cachedProgress = allowedProgress;
        }
        return Math.abs(checkPos.x - center.x) <= dimension.x
                && Math.abs(checkPos.z - center.z) <= dimension.z;
    }

    // TODO 根据玩家多的方向偏移，或增加机制防止圈刷特殊区域（暂定为防止刷海里）
    @Override
    public void calculateShape(ServerLevel serverLevel, List<GamePlayer> standingGamePlayers, Supplier<Float> random) {
        if (!determined) {
            // start center
            switch (startEntry.startCenterType) {
                case FIXED -> startCenter = startEntry.startCenterPos;
                case PREVIOUS, RELATIVE -> {
                    startCenter = getPreviousCenterById(startEntry.startCenterZoneId, startEntry.startCenterProgress);
                    if (startEntry.startCenterType == StartCenterType.RELATIVE) {
                        startCenter = Vec3Utils.addVec(startCenter, startEntry.startCenterPos);
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = startEntry.centerPlayerId;
                    if (playerId <= 0) {
                        if (startEntry.selectStanding) {
                            playerId = standingGamePlayers.get((int) (random.get() * standingGamePlayers.size())).getGameSingleId();
                        } else {
                            List<GamePlayer> gamePlayers = GameManager.get().getGamePlayers();
                            playerId = gamePlayers.get((int) (random.get() * gamePlayers.size())).getGameSingleId();
                        }
                    }
                    GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) {
                        return;
                    }
                    startCenter = gamePlayer.getLastPos();
                }
            }
            if (startCenter == null) {
                BattleRoyale.LOGGER.warn("Failed to calculate start center, type: {}", startEntry.startCenterType.getValue());
                return;
            }
            if (startEntry.startCenterRange > 0) {
                startCenter = randomAdjustXZ(startCenter, startEntry.startCenterRange, random);
            }
            // start dimension
            switch (startEntry.startDimensionType) {
                case FIXED -> startDimension = startEntry.startDimension;
                case PREVIOUS, RELATIVE -> {
                    startDimension = getPreviousDimensionById(startEntry.startDimensionZoneId, startEntry.startDimensionProgress);
                    if (startEntry.startDimensionType == StartDimensionType.RELATIVE) {
                        startDimension = Vec3Utils.addVec(startDimension, startEntry.startDimension);
                    }
                }
            }
            if (startDimension == null) {
                BattleRoyale.LOGGER.warn("Failed to calculate start dimension, type: {}", startEntry.startDimensionType.getValue());
                return;
            }
            if (startEntry.startDimensionRange > 0) {
                startDimension = randomAdjustXZ(startDimension, startEntry.startDimensionRange, random);
            }
            if (startEntry.startDimensionScale >= 0) {
                startDimension = Vec3Utils.scaleXZ(startDimension, startEntry.startDimensionScale);
            }
            // end center
            switch (endEntry.endCenterType) {
                case FIXED -> endCenter = endEntry.endCenterPos;
                case PREVIOUS, RELATIVE -> {
                    endCenter = getPreviousCenterById(endEntry.endCenterZoneId, endEntry.endCenterProgress);
                    if (endEntry.endCenterType == EndCenterType.RELATIVE) {
                        endCenter = Vec3Utils.addVec(endCenter, endEntry.endCenterPos);
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = endEntry.centerPlayerId;
                    if (playerId <= 0) {
                        if (endEntry.selectStanding) {
                            playerId = standingGamePlayers.get((int) (random.get() * standingGamePlayers.size())).getGameSingleId();
                        } else {
                            List<GamePlayer> gamePlayers = GameManager.get().getGamePlayers();
                            playerId = gamePlayers.get((int) (random.get() * gamePlayers.size())).getGameSingleId();
                        }
                    }
                    GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) {
                        return;
                    }
                    endCenter = gamePlayer.getLastPos();
                }
            }
            if (endCenter == null) {
                BattleRoyale.LOGGER.warn("Failed to calculate end center, type: {}", endEntry.endCenterType.getValue());
                return;
            }
            if (endEntry.endCenterRange > 0) {
                endCenter = randomAdjustXZ(endCenter, endEntry.endCenterRange, random);
            }
            // end dimension
            switch (endEntry.endDimensionType) {
                case FIXED -> endDimension = endEntry.endDimension;
                case PREVIOUS, RELATIVE -> {
                    endDimension = getPreviousDimensionById(endEntry.endDimensionZoneId, endEntry.endDimensionProgress);
                    if (endEntry.endDimensionType == EndDimensionType.RELATIVE) {
                        endDimension = Vec3Utils.addVec(endDimension, endEntry.endDimension);
                    }
                }
            }
            if (endDimension == null) {
                BattleRoyale.LOGGER.warn("Failed to calculate end dimension, type: {}", endEntry.endDimensionType.getValue());
            }
            if (endEntry.endDimensionRange > 0) {
                endDimension = randomAdjustXZ(endDimension, endEntry.endDimensionRange, random);
            }
            if (endEntry.endDimensionScale >= 0) {
                endDimension = Vec3Utils.scaleXZ(endDimension, endEntry.endDimensionScale);
            }
        }
        if (additionalCalculationCheck()
                && startCenter != null&& startDimension != null
                && endCenter != null && endDimension != null) {
            centerDist = new Vec3(endCenter.x - startCenter.x,
                    endCenter.y - startCenter.y,
                    endCenter.z - startCenter.z);
            dimensionDist = new Vec3(endDimension.x - startDimension.x,
                    endDimension.y - startDimension.y,
                    endDimension.z - startDimension.z);
            // 缓存，用于加速判断isWithinZone
            cachedCenter = startCenter;
            cachedDimension = startDimension;
            cachedProgress = 0;
            determined = true;
        }
    }

    protected boolean additionalCalculationCheck() {
        return true;
    }

    @Override
    public boolean isDetermined() {
        return determined;
    }

    @Override
    public @Nullable Vec3 getStartCenterPos() {
        return startCenter;
    }

    @Override
    public @Nullable Vec3 getCenterPos(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            BattleRoyale.LOGGER.warn("Shape center is not fully determined yet, may produce unexpected progress calculation");
        }
        return new Vec3(startCenter.x + centerDist.x * allowedProgress,
                startCenter.y + centerDist.y * allowedProgress,
                startCenter.z + centerDist.z * allowedProgress);
    }

    @Override
    public @Nullable Vec3 getEndCenterPos() {
        return endCenter;
    }

    @Override
    public @Nullable Vec3 getStartDimension() {
        return startDimension;
    }

    @Override
    public @Nullable Vec3 getDimension(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            BattleRoyale.LOGGER.warn("Shape dimension is not fully determined yet, may produce unexpected progress calculation");
        }
        return new Vec3(startDimension.x + dimensionDist.x * allowedProgress,
                startDimension.y + dimensionDist.y * allowedProgress,
                startDimension.z + dimensionDist.z * allowedProgress);
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        return endDimension;
    }

    @Nullable
    public Vec3 getPreviousCenterById(int zoneId, double progress) {
        IGameZone gameZone = ZoneManager.get().getZoneById(zoneId);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone end center by zoneId: {}", zoneId);
            return null;
        }

        if (progress >= 1) {
            return gameZone.getEndCenterPos();
        } else if (progress <= 0) {
            return gameZone.getStartCenterPos();
        }
        Vec3 v = gameZone.getCenterPos(progress);
        BattleRoyale.LOGGER.info("getPreviousCenterById, progress: {}, centerPos: {}", progress, v);
        return v;
    }

    @Nullable
    public Vec3 getPreviousDimensionById(int zoneId, double progress) {
        IGameZone gameZone = ZoneManager.get().getZoneById(zoneId);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone end dimension by zoneId: {}", zoneId);
            return null;
        }

        if (progress >= 1) {
            return gameZone.getEndDimension();
        } else if (progress <= 0) {
            return gameZone.getStartDimension();
        }
        Vec3 v = gameZone.getDimension(progress);
        BattleRoyale.LOGGER.info("getPreviousDimensionById, progress: {}, dimension: {}", progress, v);
        return v;
    }
}
