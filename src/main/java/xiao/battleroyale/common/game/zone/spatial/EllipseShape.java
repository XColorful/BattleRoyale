package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.client.renderer.game.ZoneRenderer;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import javax.annotation.Nullable;

/**
 * 二维 椭圆
 */
public class EllipseShape extends AbstractSimpleShape {

    public EllipseShape(StartEntry startEntry, EndEntry endEntry) {
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

        double x_translated = checkPos.x - center.x;
        double z_translated = checkPos.z - center.z;

        double distSq = x_translated * x_translated + z_translated * z_translated;

        double a = dimension.x; // 半长轴
        double b = dimension.z; // 半短轴

        // 先内接圆判断是否在内（多数情况用safe安全区，判断在圈内）
        if (distSq <= b * b) {
            return true;
        }

        // 后外接圆判断是否在外
        if (distSq > a * a) {
            return false;
        }

        // 详细的椭圆方程计算
        double x_rotated;
        double z_rotated;

        if (Math.abs(rotateDegree) < EPSILON) {
            x_rotated = x_translated;
            z_rotated = z_translated;
        } else {
            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            x_rotated = x_translated * cosDegree + z_translated * sinDegree;
            z_rotated = -x_translated * sinDegree + z_translated * cosDegree;
        }

        // 椭圆方程: (x_rotated^2 / a^2) + (z_rotated^2 / b^2) <= 1
        return (x_rotated * x_rotated) / (a * a) + (z_rotated * z_rotated) / (b * b) <= 1.0;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.ELLIPSE;
    }

    @Override
    public int getSegments() {
        return ZoneRenderer.ELLIPSE_SEGMENTS;
    }
}
