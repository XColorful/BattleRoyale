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
 * 二维多边形
 */
public class PolygonShape extends AbstractPolyShape {

    protected int segments;
    protected float angle = (float) (Math.PI / 2.0); // 使正上方为一个顶点

    public PolygonShape(StartEntry startEntry, EndEntry endEntry, int segments) {
        super(startEntry, endEntry);
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

        double halfHeight = dimension.y / 2.0;
        double lowerY = center.y - halfHeight;
        double upperY = center.y + halfHeight;
        if (checkPos.y < lowerY || checkPos.y > upperY) {
            return false;
        }

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


        double radius = dimension.x; // 外接圆半径 (circumradius)
        double distSq = pX_rotated * pX_rotated + pZ_rotated * pZ_rotated;

        // 内接圆判断
        if (distSq < currentApothem * currentApothem) {
            return true;
        }

        // 外接圆判断
        if (distSq > radius * radius) {
            return false;
        }

        // 精确判断：点是否在多边形内部
        // 遍历多边形的每条边，检查点是否始终位于所有边的同一侧
        // 如果顶点是逆时针排列，则点应始终在边的左侧（叉积 >= 0）
        // 如果顶点是顺时针排列，则点应始终在边的右侧（叉积 <= 0）
        // 当前的角度计算是逆时针的 (angle1 -> angle2)，所以期望叉积 >= 0
        // 顶点计算需要结合 `angle` 和 `rotateDegree`
        double[] vertexX = new double[segments];
        double[] vertexZ = new double[segments];
        double totalRotationRadians = angle + Math.toRadians(rotateDegree);

        for (int i = 0; i < segments; i++) {
            double currentVertexAngle = totalRotationRadians + (2 * Math.PI * i / segments);
            vertexX[i] = radius * Mth.cos((float) currentVertexAngle);
            vertexZ[i] = radius * Mth.sin((float) currentVertexAngle);
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
            // 如果叉积小于0，说明点在边的右侧，因此不在多边形内部
            if ((edgeVecX * pointVecZ) - (edgeVecZ * pointVecX) < 0) {
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
