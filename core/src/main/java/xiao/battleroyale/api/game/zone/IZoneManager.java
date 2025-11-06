package xiao.battleroyale.api.game.zone;

import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.common.game.zone.ZoneManager;

public interface IZoneManager extends IGameSubManager, IGameZoneReadApi {

    void setStackZoneConfig(boolean turn);

    ZoneManager.ZoneContext getCommonZoneContext();

    ZoneManager.ZoneContext getZoneContextInGame();
}
