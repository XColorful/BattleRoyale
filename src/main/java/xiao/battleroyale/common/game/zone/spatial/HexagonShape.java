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

    public HexagonShape(StartEntry startEntry, EndEntry endEntry) {
        super(startEntry, endEntry);
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

        double radius = dimension.x; // 外接圆半径，即边长
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

        // 先内接圆判断是否在内（多数情况用safe安全区，判断在圈内）
        if (distSq < currentApothem * currentApothem) {
            return true;
        }

        // 后外接圆判断是否在外
        if (distSq > radius * radius) {
            return false;
        }

        // 立方体坐标系判定 (Cube Coordinates)
        double q = (x_rotated * 2.0 / 3.0) / radius;
        double r = (-x_rotated / 3.0 + z_rotated * Math.sqrt(3.0) / 3.0) / radius;
        double s = (-x_rotated / 3.0 - z_rotated * Math.sqrt(3.0) / 3.0) / radius;

        int rq = (int) Math.round(q);
        int rr = (int) Math.round(r);
        int rs = (int) Math.round(s);

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

        return (rq == 0 && rr == 0 && rs == 0);
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