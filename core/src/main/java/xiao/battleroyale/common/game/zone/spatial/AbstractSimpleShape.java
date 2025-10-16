package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.end.EndRotationType;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartRotationType;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.util.GameUtils;
import xiao.battleroyale.util.Vec3Utils;

import static xiao.battleroyale.util.Vec3Utils.randomAdjustXZ;
import static xiao.battleroyale.util.Vec3Utils.randomCircleXZ;

import java.util.List;


public abstract class AbstractSimpleShape implements ISpatialZone {

    protected final StartEntry startEntry;
    protected final EndEntry endEntry;
    protected final boolean allowBadShape; // 配置项
    protected boolean hasBadShape = false; // 外部调用
    protected boolean checkBadShape = false; // 类内使用

    protected Vec3 startCenter = null;
    protected Vec3 startDimension = null;
    protected double startRotateDegree = 0;
    protected Vec3 endCenter = null;
    protected Vec3 endDimension = null;
    protected double endRotateDegree = 0;

    protected Vec3 cachedCenter = Vec3.ZERO;
    protected Vec3 cachedDimension = Vec3.ZERO;
    protected double cachedRotateDegree = 0;
    protected double cachedProgress = -1;
    protected static final double EPSILON = 1.0E-9; // 移动30分钟的圈每tick的变化为 2.778 x 10^-5

    protected boolean determined = false;
    protected Vec3 centerDist;
    protected Vec3 dimensionDist;
    protected double rotateDist;

    public AbstractSimpleShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        this.startEntry = startEntry;
        this.endEntry = endEntry;
        this.allowBadShape = allowBadShape;
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
        double rotateDegree;
        if (Math.abs(allowedProgress - cachedProgress) < EPSILON) {
            center = cachedCenter;
            dimension = cachedDimension;
            rotateDegree = cachedRotateDegree;
        } else {
            center = getCenterPos(allowedProgress);
            dimension = getDimension(allowedProgress);
            rotateDegree = getRotateDegree(allowedProgress);
            cachedCenter = center;
            cachedDimension = dimension;
            cachedRotateDegree = rotateDegree;
            cachedProgress = allowedProgress;
        }

        double finalCheckX;
        double finalCheckZ;
        double finalHalfWidth = Math.abs(dimension.x);
        double finalHalfDepth = Math.abs(dimension.z);
        boolean invertX = dimension.x < 0;
        boolean invertZ = dimension.z < 0;

        if (Math.abs(rotateDegree) < EPSILON) {
            finalCheckX = checkPos.x - center.x;
            finalCheckZ = checkPos.z - center.z;
        } else {
            double dx = checkPos.x - center.x;
            double dz = checkPos.z - center.z;

            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            finalCheckX = dx * cosDegree + dz * sinDegree;
            finalCheckZ = -dx * sinDegree + dz * cosDegree;
        }

        boolean isWithinAbsX = Math.abs(finalCheckX) <= finalHalfWidth;
        boolean isWithinAbsZ = Math.abs(finalCheckZ) <= finalHalfDepth;

        return (isWithinAbsX != invertX)
                && (isWithinAbsZ != invertZ);
    }

    @Override
    public void calculateShape(@NotNull ZoneContext zoneContext) {
        if (!determined) {
            // GameManager全局修改，仅用在Fixed类型
            Vec3 globalCenterOffset = GameManager.get().getGlobalCenterOffset();

            // start center
            switch (startEntry.startCenterType) {
                case FIXED -> startCenter = startEntry.startCenterPos.add(globalCenterOffset);
                case PREVIOUS, RELATIVE -> {
                    startCenter = getPreviousCenterById(startEntry.startCenterZoneId, startEntry.startCenterProgress);
                    if (startEntry.startCenterType == StartCenterType.RELATIVE) {
                        startCenter = Vec3Utils.addVec(startCenter, startEntry.startCenterPos);
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = startEntry.centerPlayerId;
                    if (playerId <= 0) {
                        if (zoneContext.gamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        if (startEntry.selectStanding) {
                            playerId = zoneContext.gamePlayers.get((int) (zoneContext.random.get() * zoneContext.gamePlayers.size())).getGameSingleId();
                        } else {
                            List<GamePlayer> gamePlayers = GameTeamManager.getGamePlayers(); // 更不可能为空的情况，最好直接在下一行崩掉
                            playerId = gamePlayers.get((int) (zoneContext.random.get() * gamePlayers.size())).getGameSingleId();
                        }
                    }
                    GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) { // 非预期，因为GameManager需要保证列表有效
                        BattleRoyale.LOGGER.error("Failed to generate shape center: failed to get game player by id: {}", playerId);
                        return;
                    }
                    startCenter = gamePlayer.getLastPos();
                }
            }
            if (startCenter == null) {
                BattleRoyale.LOGGER.warn("Failed to calculate start center, type: {}", startEntry.startCenterType.getValue());
                return;
            }
            startCenter = randomAdjustXZ(startCenter, startEntry.startCenterRange, zoneContext.random);
            startCenter = GameUtils.calculateCenterAndLerp(startCenter, zoneContext.gamePlayers, startEntry.playerCenterLerp);
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
            startDimension = randomAdjustXZ(startDimension, startEntry.startDimensionRange, zoneContext.random);
            startDimension = Vec3Utils.scaleXZ(startDimension, startEntry.startDimensionScale);
            // start rotation
            switch (startEntry.startRotationType) {
                case FIXED -> startRotateDegree = startEntry.startRotateDegree;
                case PREVIOUS, RELATIVE -> {
                    startRotateDegree = getPreviousRotateById(startEntry.startRotateZoneId, startEntry.startRotateProgress);
                    if (startEntry.startRotationType == StartRotationType.RELATIVE) {
                        startRotateDegree += startEntry.startRotateDegree;
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = startEntry.rotatePlayerId;
                    if (playerId <= 0) {
                        if (zoneContext.gamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        playerId = zoneContext.gamePlayers.get((int) (zoneContext.random.get() * zoneContext.gamePlayers.size())).getGameSingleId();
                    }
                    GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) { // 非预期，因为GameManager需要保证列表有效
                        BattleRoyale.LOGGER.error("Failed to generate shape rotation: failed to get game player by id: {}", playerId);
                        return;
                    }
                    @Nullable ServerPlayer player = zoneContext.serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                    if (player == null) {
                        BattleRoyale.LOGGER.info("Failed to generate shape rotation: can't find ServerPlayer {} (UUID:{})", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
                        return;
                    }
                    startRotateDegree = player.getYRot();
                }
            }
            startRotateDegree += zoneContext.random.get() * startEntry.startRotateRange;
            startRotateDegree *= startEntry.startRotateScale;
            // end center
            switch (endEntry.endCenterType) {
                case FIXED -> endCenter = endEntry.endCenterPos.add(globalCenterOffset);
                case PREVIOUS, RELATIVE -> {
                    endCenter = getPreviousCenterById(endEntry.endCenterZoneId, endEntry.endCenterProgress);
                    if (endEntry.endCenterType == EndCenterType.RELATIVE) {
                        endCenter = Vec3Utils.addVec(endCenter, endEntry.endCenterPos);
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = endEntry.centerPlayerId;
                    if (playerId <= 0) {
                        if (zoneContext.gamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        if (endEntry.selectStanding) {
                            playerId = zoneContext.gamePlayers.get((int) (zoneContext.random.get() * zoneContext.gamePlayers.size())).getGameSingleId();
                        } else {
                            List<GamePlayer> gamePlayers = GameTeamManager.getGamePlayers(); // 更不可能为空的情况，最好直接在下一行崩掉
                            playerId = gamePlayers.get((int) (zoneContext.random.get() * gamePlayers.size())).getGameSingleId();
                        }
                    }
                    GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) { // 非预期，因为GameManager需要保证列表有效
                        BattleRoyale.LOGGER.error("Failed to generate end center: failed to get game player by id: {}", playerId);
                        return;
                    }
                    endCenter = gamePlayer.getLastPos();
                }
            }
            if (endCenter == null) {
                BattleRoyale.LOGGER.warn("Failed to calculate end center, type: {}", endEntry.endCenterType.getValue());
                return;
            }
            if (endEntry.useRangeAsStartDimScale) {
                Vec3 endRangeVec = Vec3Utils.scaleXZ(startDimension, endEntry.endCenterRange);
                if (endEntry.useCircleRange) {
                    endCenter = randomCircleXZ(endCenter, endRangeVec, zoneContext.random);
                } else {
                    endCenter = randomAdjustXZ(endCenter, endRangeVec, zoneContext.random);
                }
            } else {
                endCenter = randomAdjustXZ(endCenter, endEntry.endCenterRange, zoneContext.random);
            }
            endCenter = GameUtils.calculateCenterAndLerp(endCenter, zoneContext.gamePlayers, endEntry.playerCenterLerp);
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
                return;
            }
            endDimension = randomAdjustXZ(endDimension, endEntry.endDimensionRange, zoneContext.random);
            endDimension = Vec3Utils.scaleXZ(endDimension, endEntry.endDimensionScale);
            // end rotation
            switch (endEntry.endRotationType) {
                case FIXED -> endRotateDegree = endEntry.endRotateDegree;
                case PREVIOUS, RELATIVE -> {
                    endRotateDegree = getPreviousRotateById(endEntry.endRotateZoneId, endEntry.endRotateProgress);
                    if (endEntry.endRotationType == EndRotationType.RELATIVE) {
                        endRotateDegree += endEntry.endRotateDegree;
                    }
                }
                case LOCK_PLAYER -> {
                    int playerId = endEntry.rotatePlayerId;
                    if (playerId <= 0) {
                        if (zoneContext.gamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        playerId = zoneContext.gamePlayers.get((int) (zoneContext.random.get() * zoneContext.gamePlayers.size())).getGameSingleId();
                    }
                    GamePlayer gamePlayer = GameTeamManager.getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) { // 非预期，因为GameManager需要保证列表有效
                        BattleRoyale.LOGGER.error("Failed to generate end rotation: failed to get game player by id: {}", playerId);
                        return;
                    }
                    @Nullable ServerPlayer player = zoneContext.serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                    if (player == null) {
                        BattleRoyale.LOGGER.info("Failed to generate end rotation: can't find ServerPlayer {} (UUID:{})", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
                        return;
                    }
                    endRotateDegree = player.getYRot();
                }
            }
            endRotateDegree += zoneContext.random.get() * endEntry.endRotateRange;
            endRotateDegree *= endEntry.endRotateScale;
        }
        if (additionalCalculationCheck()
                && startCenter != null && startDimension != null
                && endCenter != null && endDimension != null) {
            // 预计算
            centerDist = endCenter.subtract(startCenter);
            dimensionDist = endDimension.subtract(startDimension);
            rotateDist = endRotateDegree - startRotateDegree;
            // 缓存，用于加速判断isWithinZone
            cachedCenter = startCenter;
            cachedDimension = startDimension;
            cachedProgress = 0;
            determined = true;
        }
    }

    /**
     * 检查是否需要自动更正为几何约束的形状，并设置标记
     * 该父类方法仅假设区域单方向线性变化，只检查维度是否为负
     * 如区域形状无特殊要求，此函数应始终返回true
     */
    protected boolean additionalCalculationCheck() {
        hasBadShape = hasNegativeDimension();
        checkBadShape = hasBadShape && !allowBadShape; // 会生成坏形状，并且不允许出现坏形状 -> 需要在运行时检查
        return true;
    }

    protected boolean hasNegativeDimension() {
        return Vec3Utils.hasNegative(startDimension) || Vec3Utils.hasNegative(endDimension);
    }

    protected boolean hasEqualXZAbsDimension() {
        return Vec3Utils.equalXZAbs(startDimension) && Vec3Utils.equalXZAbs(endDimension);
    }

    @Override
    public boolean hasBadShape() {
        return hasBadShape;
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
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected center calculation");
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
        return checkBadShape ? Vec3Utils.positive(startDimension) : startDimension;
    }

    @Override
    public @Nullable Vec3 getDimension(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            if (dimensionDist == null) {
                return null;
            }
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected dimension calculation");
        }
        Vec3 baseVec = getDimensionNoCheck(allowedProgress);
        return checkBadShape ? Vec3Utils.positive(baseVec) : baseVec;
    }

    protected Vec3 getDimensionNoCheck(double progress) {
        return new Vec3(startDimension.x + dimensionDist.x * progress,
                startDimension.y + dimensionDist.y * progress,
                startDimension.z + dimensionDist.z * progress);
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        return checkBadShape ? Vec3Utils.positive(endDimension) : endDimension;
    }

    @Override
    public double getStartRotateDegree() {
        return startRotateDegree;
    }

    @Override
    public double getRotateDegree(double progress) {
        double allowedProgress = GameZone.allowedProgress(progress);
        if (!determined) {
            BattleRoyale.LOGGER.warn("Shape is not fully determined yet, may produce unexpected rotation calculation");
        }
        return startRotateDegree + rotateDist * allowedProgress;
    }

    @Override
    public double getEndRotateDegree() {
        return endRotateDegree;
    }

    @Nullable
    public Vec3 getPreviousCenterById(int zoneId, double progress) {
        IGameZone gameZone = ZoneManager.get().getGameZone(zoneId);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone center by zoneId: {}", zoneId);
            return null;
        }

        if (progress >= 1) {
            return gameZone.getEndCenterPos();
        } else if (progress <= 0) {
            return gameZone.getStartCenterPos();
        }
        return gameZone.getCenterPos(progress);
    }

    @Nullable
    public Vec3 getPreviousDimensionById(int zoneId, double progress) {
        IGameZone gameZone = ZoneManager.get().getGameZone(zoneId);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone dimension by zoneId: {}", zoneId);
            return null;
        }

        if (progress >= 1) {
            return gameZone.getEndDimension();
        } else if (progress <= 0) {
            return gameZone.getStartDimension();
        }
        return gameZone.getDimension(progress);
    }

    public double getPreviousRotateById(int zoneId, double progress) {
        IGameZone gameZone = ZoneManager.get().getGameZone(zoneId);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone rotation by zoneId: {}, defaulting to 0", zoneId);
            return 0;
        }

        if (progress >= 1) {
            return gameZone.getEndRotateDegree();
        } else if (progress <= 0) {
            return gameZone.getStartRotateDegree();
        }
        return gameZone.getRotateDegree(progress);
    }
}
