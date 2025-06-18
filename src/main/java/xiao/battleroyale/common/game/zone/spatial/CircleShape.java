package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.renderer.game.ZoneRenderer;
import xiao.battleroyale.config.common.game.zone.zoneshape.EndEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.StartEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

/**
 * 二维 Circle
 */
public class CircleShape extends AbstractSimpleShape {

    public CircleShape(StartEntry startEntry, EndEntry endEntry) {
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
        // 忽略y方向
        double xDist = center.x - checkPos.x;
        double zDist = center.z - checkPos.z;
        return (xDist * xDist + zDist * zDist) <= (dimension.x * dimension.z);
    }

    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CIRCLE;
    }

    @Override
    protected boolean additionalCalculationCheck() {
        if (startDimension.x != startDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal circle shape start dimension (x: {}, z:{}), defaulting to x", startDimension.x, startDimension.z);
            startDimension = new Vec3(startDimension.x, startDimension.y, startDimension.x);
        }
        if (endDimension.x != endDimension.z) {
            BattleRoyale.LOGGER.warn("Unequal circle shape end dimension (x: {}, z:{}), defaulting to x", endDimension.x, endDimension.z);
            endDimension = new Vec3(endDimension.x, endDimension.y, endDimension.x);
        }

        return true;
    }

    @Override
    public int getSegments() {
        return ZoneRenderer.CIRCLE_SEGMENTS;
    }
}
