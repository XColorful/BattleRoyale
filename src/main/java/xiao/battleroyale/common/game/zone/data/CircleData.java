package xiao.battleroyale.common.game.zone.data;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.zone.IGameZone;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeData;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import java.util.Map;

public class CircleData implements IZoneShapeData {
    @Override
    public ZoneShapeType getShapeType() {
        return ZoneShapeType.CIRCLE;
    }

    @Override
    public StartCenterType getStartCenterType() {
        return null;
    }

    @Override
    public Vec3 getStartCenterPos(Map<Integer, IGameZone> gameZones) {
        return null;
    }

    @Override
    public StartDimensionType getStartDimensionType() {
        return null;
    }

    @Override
    public Vec3 getStartDimensions(Map<Integer, IGameZone> gameZones) {
        return null;
    }

    @Override
    public EndCenterType getEndCenterType() {
        return null;
    }

    @Override
    public Vec3 getEndCenterPos(Map<Integer, IGameZone> gameZones) {
        return null;
    }

    @Override
    public EndDimensionType getEndDimensionType() {
        return null;
    }

    @Override
    public Vec3 getEndDimensions(Map<Integer, IGameZone> gameZones) {
        return null;
    }
}
