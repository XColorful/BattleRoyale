package xiao.battleroyale.api.game.zone.func;

import xiao.battleroyale.api.game.zone.IZoneData;
import xiao.battleroyale.api.game.zone.ZoneDataType;

public interface IZoneFuncData extends IZoneData {

    @Override
    default ZoneDataType getDataType() {
        return ZoneDataType.FUNC;
    }

    FuncType getFuncType();

    int getMoveDelay();

    int getMoveTime();
}
