package xiao.battleroyale.api.game.zone.shape;

import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.zone.IGameZone;
import xiao.battleroyale.api.game.zone.IZoneData;
import xiao.battleroyale.api.game.zone.ZoneDataType;
import xiao.battleroyale.api.game.zone.shape.end.EndCenterType;
import xiao.battleroyale.api.game.zone.shape.end.EndDimensionType;
import xiao.battleroyale.api.game.zone.shape.start.StartCenterType;
import xiao.battleroyale.api.game.zone.shape.start.StartDimensionType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import java.util.Map;

public interface IZoneShapeData extends IZoneData {

    @Override
    default ZoneDataType getDataType() {
        return ZoneDataType.SHAPE;
    }

    ZoneShapeType getShapeType();

    StartCenterType getStartCenterType();

    /**
     * @param gameZones 传入游戏进行时已经生成的区域以供动态计算
     * @return 中心点位置
     */
    Vec3 getStartCenterPos(Map<Integer, IGameZone> gameZones);

    StartDimensionType getStartDimensionType();
    Vec3 getStartDimensions(Map<Integer, IGameZone> gameZones);

    EndCenterType getEndCenterType();
    Vec3 getEndCenterPos(Map<Integer, IGameZone> gameZones);

    EndDimensionType getEndDimensionType();
    Vec3 getEndDimensions(Map<Integer, IGameZone> gameZones);
}
