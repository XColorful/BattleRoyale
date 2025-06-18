package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

/**
 * 二维平顶正六边形
 */
public class HexagonShape extends AbstractSimpleShape {

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
        double allowProgress = Math.min(progress, 1);
        Vec3 center = getCenterPos(allowProgress);
        Vec3 dimension = getDimension(allowProgress);

        double radius = dimension.x; // 外接圆半径，即边长
        double x = checkPos.x - center.x;
        double z = checkPos.z - center.z;

        // 先进行外接圆判断(忽略y轴)
        double distSq = x * x + z * z;
        if (distSq > radius * radius) { // 如果距离平方大于半径平方，则肯定在外面
            return false;
        }

        // 立方体坐标系判定 (Cube Coordinates)
        double q = (x * 2.0 / 3.0) / radius;
        double r = (-x / 3.0 + z * Math.sqrt(3.0) / 3.0) / radius;
        double s = (-x / 3.0 - z * Math.sqrt(3.0) / 3.0) / radius;

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
    protected boolean additionalCalculationCheck() {
        if (startDimension.x != startDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal hexagon shape start dimension (x: {}, z:{}), defaulting to x", startDimension.x, startDimension.z);
            startDimension = new Vec3(startDimension.x, startDimension.y, startDimension.x);
        }
        if (endDimension.x != endDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal hexagon shape end dimension (x: {}, z:{}), defaulting to x", endDimension.x, endDimension.z);
            endDimension = new Vec3(endDimension.x, endDimension.y, endDimension.x);
        }
        return true;
    }

    @Override
    public int getSegments() {
        return 6;
    }
}