package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import static xiao.battleroyale.common.game.zone.spatial.ShapeHelper.*;
import static xiao.battleroyale.util.Vec3Utils.randomAdjustXZ;

import xiao.battleroyale.util.Vec3Utils;

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
        double allowedProgress = Math.min(progress, 1);
        Vec3 center, dimension;
        if (Math.abs(allowedProgress - cachedProgress) < 0.001) {
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
    public void calculateShape(ServerLevel serverLevel, List<GamePlayer> gamePlayerList, Supplier<Float> random) {
        if (!determined) {
            // start center
            switch (startEntry.startCenterType) {
                case FIXED -> startCenter = startEntry.startCenterPos;
                case PREVIOUS -> startCenter = getPreviousEndCenterById(startEntry.startCenterZoneId);
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
                case PREVIOUS -> startDimension = getPreviousEndDimensionById(startEntry.startDimensionZoneId);
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
                case PREVIOUS -> endCenter = getPreviousStartCenterById(endEntry.endCenterZoneId);
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
                case PREVIOUS -> endDimension = getPreviousStartDimensionById(endEntry.endDimensionZoneId);
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
        if (additionalCalculationCheck() && startCenter != null && startDimension != null && endCenter != null && endDimension != null) {
            centerDist = new Vec3(endCenter.x - startCenter.x, endCenter.y - startCenter.y, endCenter.z - startCenter.z);
            dimensionDist = new Vec3(endDimension.x - startDimension.x, endDimension.y - startDimension.y, endDimension.z - startDimension.z);
            // 缓存，用于判断isWithinZone
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
        if (!determined) {
            return null;
        }
        return new Vec3(startCenter.x + centerDist.x * progress,
                startCenter.y + centerDist.y * progress,
                startCenter.z + centerDist.z * progress);
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
        if (!determined) {
            return null;
        }
        return new Vec3(startDimension.x + dimensionDist.x * progress,
                startDimension.y + dimensionDist.y * progress,
                startDimension.z + dimensionDist.z * progress);
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        return endDimension;
    }
}
