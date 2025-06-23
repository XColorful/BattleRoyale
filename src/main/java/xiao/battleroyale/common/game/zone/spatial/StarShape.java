package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.zone.GameZone;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.Vec3Utils;

/**
 * 二维⭐
 */
public class StarShape extends AbstractSimpleShape {

    protected int segments;
    protected float angle = (float) (Math.PI / 2.0); // 使正上方成为第一个顶点

    public StarShape(StartEntry startEntry, EndEntry endEntry, boolean allowBadShape, int segments) {
        super(startEntry, endEntry, allowBadShape);
        this.segments = Math.max(segments, 2); // 至少为指南针
    }

    @Override
    public int getSegments() {
        return this.segments;
    }

    @Override
    public boolean isWithinZone(@Nullable Vec3 checkPos, double progress) {
        if (checkPos == null || progress < 0 || !isDetermined()) {
            return false;
        }

        double allowedProgress = GameZone.allowedProgress(progress);
        Vec3 center, dimension; // dimension.x 为原始外接圆半径，dimension.z 为原始内接圆半径
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

        double rawOuterRadius = dimension.x;
        double rawInnerRadius = dimension.z;

        double effectiveOuterRadius = Math.abs(rawOuterRadius);
        double effectiveInnerRadius = Math.abs(rawInnerRadius);

        if (effectiveOuterRadius <= EPSILON) {
            return false;
        }

        boolean isZoneInverted = rawOuterRadius < 0;

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

        // 外接圆判断
        if (distSq > effectiveOuterRadius * effectiveOuterRadius + EPSILON) {
            return isZoneInverted;
        }
        // 内接圆判断
        if (distSq < effectiveInnerRadius * effectiveInnerRadius - EPSILON) {
            return !isZoneInverted;
        }

        // 卷绕数法判断点是否在星形内部
        double totalRotationRadians = angle + Math.toRadians(rotateDegree);

        int numVertices = 2 * segments;
        double[] vertexX = new double[numVertices];
        double[] vertexZ = new double[numVertices];

        for (int i = 0; i < segments; i++) {
            double outerVertexAngle = totalRotationRadians + (2 * Math.PI * i / segments);
            vertexX[2 * i] = effectiveOuterRadius * Mth.cos((float) outerVertexAngle);
            vertexZ[2 * i] = effectiveOuterRadius * Mth.sin((float) outerVertexAngle);

            double innerVertexAngle = totalRotationRadians + (2 * Math.PI * i / segments) + (Math.PI / segments);
            vertexX[2 * i + 1] = effectiveInnerRadius * Mth.cos((float) innerVertexAngle);
            vertexZ[2 * i + 1] = effectiveInnerRadius * Mth.sin((float) innerVertexAngle);
        }

        int windingNumber = 0;
        for (int i = 0; i < numVertices; i++) {
            double v1x = vertexX[i];
            double v1z = vertexZ[i];
            double v2x = vertexX[(i + 1) % numVertices];
            double v2z = vertexZ[(i + 1) % numVertices];

            boolean v1_above = v1z > pZ_rotated;
            boolean v2_above = v2z > pZ_rotated;

            // 向上穿越射线
            double crossProduct = (v2x - v1x) * (pZ_rotated - v1z) - (pX_rotated - v1x) * (v2z - v1z);
            if (!v1_above && v2_above) {
                // 叉积判断点在V1->V2的哪一侧
                if (crossProduct > EPSILON) {
                    windingNumber++;
                }
            }
            // 向下穿越射线
            else if (v1_above && !v2_above) {
                if (crossProduct < -EPSILON) {
                    windingNumber--;
                }
            }
        }
        // 根据卷绕数和区域反转状态返回结果
        return (windingNumber == 0) == isZoneInverted;
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.STAR;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        boolean willProduceBadShape = hasNegativeDimension()
                || startDimension.x < startDimension.z || endDimension.x < endDimension.z;
        checkBadShape = willProduceBadShape && !allowBadShape;
        return true;
    }

    @Override
    public @Nullable Vec3 getStartDimension() {
        if (checkBadShape
                && (Vec3Utils.hasNegative(startDimension) || startDimension.x < startDimension.z)) {
            return Vec3Utils.positive(new Vec3(startDimension.z, startDimension.y, startDimension.x));
        } else {
            return startDimension;
        }
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
        if (checkBadShape
                && (Vec3Utils.hasNegative(baseVec) || baseVec.x < baseVec.z)) {
            return Vec3Utils.positive(new Vec3(baseVec.z, baseVec.y, baseVec.x));
        } else {
            return baseVec;
        }
    }

    @Override
    public @Nullable Vec3 getEndDimension() {
        if (checkBadShape
                && (Vec3Utils.hasNegative(endDimension) || endDimension.x < endDimension.z)) {
            return Vec3Utils.positive(new Vec3(endDimension.z, endDimension.y, endDimension.x));
        } else {
            return endDimension;
        }
    }
}
