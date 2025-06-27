package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.end.EndRotationType;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartRotationType;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.util.GameUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.List;
import java.util.function.Supplier;

import static xiao.battleroyale.util.Vec3Utils.randomAdjustXYZ;

public abstract class Abstract3DShape extends AbstractSimpleShape {

    public Abstract3DShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    /**
     * 兼容正方体和长方体的判定
     * @param checkPos 待检查的玩家/人机位置
     * @param progress 进度，用于确定计算所需的圈的状态
     * @return 判定结果
     */
    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0 || !isDetermined()) {
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

        // dimension.x, dimension.y, dimension.z 分别为长方体在 X, Y, Z 轴方向上的半长
        double finalHalfWidth = Math.abs(dimension.x);
        double finalHalfHeight = Math.abs(dimension.y);
        double finalHalfDepth = Math.abs(dimension.z);

        // 判断各维度是否反转
        boolean invertX = dimension.x < 0;
        boolean invertY = dimension.y < 0;
        boolean invertZ = dimension.z < 0;

        // 将检测点平移到以区域中心为原点
        double pX_relative = checkPos.x - center.x;
        double pY_relative = checkPos.y - center.y;
        double pZ_relative = checkPos.z - center.z;

        double finalCheckX;
        double finalCheckZ;

        // 仅在 XZ 平面进行旋转（绕 Y 轴旋转）
        if (Math.abs(rotateDegree) < EPSILON) {
            finalCheckX = pX_relative;
            finalCheckZ = pZ_relative;
        } else {
            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            finalCheckX = pX_relative * cosDegree + pZ_relative * sinDegree;
            finalCheckZ = -pX_relative * sinDegree + pZ_relative * cosDegree;
        }

        // 判断点是否在长方体内部（各轴向的绝对值判断）
        boolean isWithinAbsX = Math.abs(finalCheckX) <= finalHalfWidth;
        boolean isWithinAbsY = Math.abs(pY_relative) <= finalHalfHeight;
        boolean isWithinAbsZ = Math.abs(finalCheckZ) <= finalHalfDepth;

        // 根据维度是否为负来反转区域内外判断
        return (isWithinAbsX != invertX)
                && (isWithinAbsY != invertY)
                && (isWithinAbsZ != invertZ);
    }

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
                        if (standingGamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        if (startEntry.selectStanding) {
                            playerId = standingGamePlayers.get((int) (random.get() * standingGamePlayers.size())).getGameSingleId();
                        } else {
                            List<GamePlayer> gamePlayers = GameManager.get().getGamePlayers(); // 更不可能为空的情况，最好直接在下一行崩掉
                            playerId = gamePlayers.get((int) (random.get() * gamePlayers.size())).getGameSingleId();
                        }
                    }
                    GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
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
            startCenter = randomAdjustXYZ(startCenter, startEntry.startCenterRange, random);
            if (startEntry.playerCenterLerp != 0) {
                startCenter = GameUtils.calculateCenterAndLerp(startCenter, standingGamePlayers, startEntry.playerCenterLerp);
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
            startDimension = randomAdjustXYZ(startDimension, startEntry.startDimensionRange, random);
            startDimension = Vec3Utils.scaleXYZ(startDimension, startEntry.startDimensionScale);
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
                        if (standingGamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        playerId = standingGamePlayers.get((int) (random.get() * standingGamePlayers.size())).getGameSingleId();
                    }
                    GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) { // 非预期，因为GameManager需要保证列表有效
                        BattleRoyale.LOGGER.error("Failed to generate shape rotation: failed to get game player by id: {}", playerId);
                        return;
                    }
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (player == null) {
                        BattleRoyale.LOGGER.info("Failed to generate shape rotation: can't find ServerPlayer {} (UUID:{})", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
                        return;
                    }
                    startRotateDegree = player.getYRot();
                }
            }
            startRotateDegree += random.get() * startEntry.startRotateRange;
            startRotateDegree *= startEntry.startRotateScale;
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
                        if (standingGamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        if (endEntry.selectStanding) {
                            playerId = standingGamePlayers.get((int) (random.get() * standingGamePlayers.size())).getGameSingleId();
                        } else {
                            List<GamePlayer> gamePlayers = GameManager.get().getGamePlayers(); // 更不可能为空的情况，最好直接在下一行崩掉
                            playerId = gamePlayers.get((int) (random.get() * gamePlayers.size())).getGameSingleId();
                        }
                    }
                    GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
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
                Vec3 endRangeVec = Vec3Utils.scaleXYZ(startDimension, endEntry.endCenterRange);
                if (endEntry.useCircleRange) {
                    endCenter = Vec3Utils.randomSphereXYZ(endCenter, endRangeVec, random);
                } else {
                    endCenter = randomAdjustXYZ(endCenter, endRangeVec, random);
                }
            } else {
                endCenter = randomAdjustXYZ(endCenter, endEntry.endCenterRange, random);
            }
            endCenter = GameUtils.calculateCenterAndLerp(endCenter, standingGamePlayers, endEntry.playerCenterLerp);
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
            endDimension = randomAdjustXYZ(endDimension, endEntry.endDimensionRange, random);
            endDimension = Vec3Utils.scaleXYZ(endDimension, endEntry.endDimensionScale);
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
                        if (standingGamePlayers.isEmpty()) {
                            BattleRoyale.LOGGER.error("StandingGamePlayers is empty, but still calculate shape, may should end game instantly");
                            return;
                        }
                        playerId = standingGamePlayers.get((int) (random.get() * standingGamePlayers.size())).getGameSingleId();
                    }
                    GamePlayer gamePlayer = GameManager.get().getGamePlayerBySingleId(playerId);
                    if (gamePlayer == null) { // 非预期，因为GameManager需要保证列表有效
                        BattleRoyale.LOGGER.error("Failed to generate end rotation: failed to get game player by id: {}", playerId);
                        return;
                    }
                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (player == null) {
                        BattleRoyale.LOGGER.info("Failed to generate end rotation: can't find ServerPlayer {} (UUID:{})", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
                        return;
                    }
                    endRotateDegree = player.getYRot();
                }
            }
            endRotateDegree += random.get() * endEntry.endRotateRange;
            endRotateDegree *= endEntry.endRotateScale;
        }
        if (additionalCalculationCheck()
                && startCenter != null&& startDimension != null
                && endCenter != null && endDimension != null) {
            // 预计算
            centerDist = endCenter.subtract(startCenter);
            dimensionDist = endDimension.subtract(endDimension);
            rotateDist = endRotateDegree - startRotateDegree;
            // 缓存，用于加速判断isWithinZone
            cachedCenter = startCenter;
            cachedDimension = startDimension;
            cachedProgress = 0;
            determined = true;
        }
    }

    protected boolean hasEqualXYZAbsDimension() {
        return Vec3Utils.equalXYZAbs(startDimension) && Vec3Utils.equalXYZAbs(endDimension);
    }
}
