package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

/**
 * 二维平顶正六边形
 */
public class HexagonShape extends AbstractPolyShape {

    public HexagonShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape) {
        super(startEntry, endEntry, allowBadShape);
    }

    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0) {
            return false;
        }
        if (!isDetermined()) {
            return false;
        }
        double allowedProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension;
        double rotateDegree;
        double currentApothem; // 当前内接圆半径

        if (Math.abs(allowedProgress - cachedProgress) < EPSILON) {
            center = cachedCenter;
            dimension = cachedDimension;
            rotateDegree = cachedRotateDegree;
            currentApothem = getApothem(allowedProgress);
        } else {
            center = getCenterPos(allowedProgress);
            dimension = getDimension(allowedProgress);
            rotateDegree = getRotateDegree(allowedProgress);
            currentApothem = getApothem(allowedProgress);
            cachedCenter = center;
            cachedDimension = dimension;
            cachedRotateDegree = rotateDegree;
            cachedProgress = allowedProgress;
        }

        double rawRadius = dimension.x;
        double effectiveRadius = Math.abs(rawRadius);
        double effectiveApothem = Math.abs(currentApothem);

        if (effectiveRadius <= EPSILON) {
            return false;
        }

        boolean isZoneInverted = rawRadius < 0;

        double x_rotated;
        double z_rotated;

        if (Math.abs(rotateDegree) < EPSILON) {
            x_rotated = checkPos.x - center.x;
            z_rotated = checkPos.z - center.z;
        } else {
            double dx = checkPos.x - center.x;
            double dz = checkPos.z - center.z;

            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            x_rotated = dx * cosDegree + dz * sinDegree;
            z_rotated = -dx * sinDegree + dz * cosDegree;
        }

        double distSq = x_rotated * x_rotated + z_rotated * z_rotated;

        // 内接圆判断
        if (distSq < effectiveApothem * effectiveApothem) {
            return !isZoneInverted;
        }

        // 外接圆判断
        if (distSq > effectiveRadius * effectiveRadius) {
            return isZoneInverted;
        }

        /*
         * 立方体坐标系判定 (Cube Coordinates) - 恢复到之前正确的精确判定逻辑
         * 将旋转后的点坐标归一化到单位六边形空间，再进行判断
         */
        double q = (x_rotated * 2.0 / 3.0) / effectiveRadius;
        double r = (-x_rotated / 3.0 + z_rotated * Math.sqrt(3.0) / 3.0) / effectiveRadius;
        double s = (-x_rotated / 3.0 - z_rotated * Math.sqrt(3.0) / 3.0) / effectiveRadius;

        // 对轴向坐标进行四舍五入
        int rq = (int) Math.round(q);
        int rr = (int) Math.round(r);
        int rs = (int) Math.round(s);

        // 调整舍入误差，确保 q+r+s = 0
        double q_diff = Math.abs(rq - q);
        double r_diff = Math.abs(rr - r);
        double s_diff = Math.abs(rs - s);

        if (q_diff > r_diff && q_diff > s_diff) {
            rq = -rr - rs;
        } else if (r_diff > s_diff) {
            rr = -rq - rs;
        } else {
            rs = -rq - rr;
        }

        return (rq == 0 && rr == 0 && rs == 0) != isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.HEXAGON;
    }

    @Override
    public int getSegments() {
        return 6;
    }
}