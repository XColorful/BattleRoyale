package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

/**
 * 二维尖顶正多边形
 */
public class PolygonShape extends AbstractPolyShape {

    protected int segments;
    protected float angle = (float) (Math.PI / 2.0); // 使正上方为一个顶点

    public PolygonShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape, int segments) {
        super(startEntry, endEntry, allowBadShape);
        this.segments = Math.max(segments, 3);
    }

    @Override
    public int getSegments() {
        return this.segments;
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
        double currentApothem;

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
        int expectedCrossProductSign = isZoneInverted ? -1 : 1;


        double pX_relative = checkPos.x - center.x;
        double pZ_relative = checkPos.z - center.z;

        double pX_rotated;
        double pZ_rotated;

        if (Math.abs(rotateDegree) < EPSILON) {
            pX_rotated = pX_relative;
            pZ_rotated = pZ_relative;
        } else {
            double radians = Math.toRadians(rotateDegree);
            double cosDegree = Math.cos(radians);
            double sinDegree = Math.sin(radians);

            pX_rotated = pX_relative * cosDegree + pZ_relative * sinDegree;
            pZ_rotated = -pX_relative * sinDegree + pZ_relative * cosDegree;
        }

        double distSq = pX_rotated * pX_rotated + pZ_rotated * pZ_rotated;

        // 内接圆判断
        if (distSq < effectiveApothem * effectiveApothem) {
            return !isZoneInverted;
        }

        // 外接圆判断
        if (distSq > effectiveRadius * effectiveRadius) {
            return isZoneInverted;
        }

        /*
         * 精确判断：点是否在多边形内部
         * 遍历多边形的每条边，检查点是否始终位于所有边的同一侧
         * 如果顶点是逆时针排列，点应始终位于所有边的“左侧”（期望叉积 >= 0）
         * 如果是反转区域，点应始终位于所有边的“右侧”（期望叉积 <= 0）
         */
        double[] vertexX = new double[segments];
        double[] vertexZ = new double[segments];
        double totalRotationRadians = angle + Math.toRadians(rotateDegree);

        for (int i = 0; i < segments; i++) {
            double currentVertexAngle = totalRotationRadians + (2 * Math.PI * i / segments);
            vertexX[i] = effectiveRadius * Mth.cos((float) currentVertexAngle);
            vertexZ[i] = effectiveRadius * Mth.sin((float) currentVertexAngle);
        }

        for (int i = 0; i < segments; i++) {
            double v1x = vertexX[i];
            double v1z = vertexZ[i];
            double v2x = vertexX[(i + 1) % segments];
            double v2z = vertexZ[(i + 1) % segments];

            double edgeVecX = v2x - v1x;
            double edgeVecZ = v2z - v1z;

            double pointVecX = pX_rotated - v1x;
            double pointVecZ = pZ_rotated - v1z;

            // 2D 叉积: (edgeVecX * pointVecZ) - (edgeVecZ * pointVecX)
            // 如果 (实际叉积 * 期望符号) 小于 -EPSILON，说明点不在区域内部
            double crossProduct = (edgeVecX * pointVecZ) - (edgeVecZ * pointVecX);
            if (crossProduct * expectedCrossProductSign < -EPSILON) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.POLYGON;
    }
}
