package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

/**
 * 二维多边形
 */
public class PolygonShape extends AbstractSimpleShape {

    protected int segments;
    protected float angle = (float) (Math.PI / 2.0); // 使正上方为一个顶点

    public PolygonShape(StartEntry startEntry, EndEntry endEntry, int segments) {
        super(startEntry, endEntry);
        this.segments = Math.max(segments, 3);
        // 基于segments计算angle，使正上方为一个顶点
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
        double allowProgress = Math.min(progress, 1);
        Vec3 center = getCenterPos(allowProgress);
        Vec3 dimension = getDimension(allowProgress);

        double halfHeight = dimension.y / 2.0;
        double lowerY = center.y - halfHeight;
        double upperY = center.y + halfHeight;
        if (checkPos.y < lowerY || checkPos.y > upperY) {
            return false;
        }

        double pX = checkPos.x - center.x;
        double pZ = checkPos.z - center.z;

        double radius = dimension.x; // 外接圆半径 (circumradius)
        double distSq = pX * pX + pZ * pZ;

        // 外接圆判断
        if (distSq > radius * radius) {
            return false;
        }

        // 内切圆判断
        double apothem = radius * Mth.cos((float) (Math.PI / segments));
        if (distSq < apothem * apothem) {
            return true;
        }

        // 精确判断：点是否在多边形内部
        // 遍历多边形的每条边，检查点是否始终位于所有边的同一侧
        // 如果顶点是逆时针排列，则点应始终在边的左侧（叉积 >= 0）
        // 如果顶点是顺时针排列，则点应始终在边的右侧（叉积 <= 0）
        // 当前的角度计算是逆时针的 (angle1 -> angle2)，所以期望叉积 >= 0

        double[] vertexX = new double[segments];
        double[] vertexZ = new double[segments];
        for (int i = 0; i < segments; i++) {
            double currentAngle = angle + (2 * Math.PI * i / segments);
            vertexX[i] = radius * Mth.cos((float) currentAngle);
            vertexZ[i] = radius * Mth.sin((float) currentAngle);
        }

        for (int i = 0; i < segments; i++) {
            double v1x = vertexX[i];
            double v1z = vertexZ[i];
            double v2x = vertexX[(i + 1) % segments];
            double v2z = vertexZ[(i + 1) % segments];

            double edgeVecX = v2x - v1x;
            double edgeVecZ = v2z - v1z;

            double pointVecX = pX - v1x;
            double pointVecZ = pZ - v1z;

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

    @Override
    protected boolean additionalCalculationCheck() {
        if (startDimension.x != startDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal polygon shape start dimension (x: {}, z:{}), defaulting to x", startDimension.x, startDimension.z);
            startDimension = new Vec3(startDimension.x, startDimension.y, startDimension.x);
        }
        if (endDimension.x != endDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal polygon shape end dimension (x: {}, z:{}), defaulting to x", endDimension.x, endDimension.z);
            endDimension = new Vec3(endDimension.x, endDimension.y, endDimension.x);
        }
        return true;
    }
}
