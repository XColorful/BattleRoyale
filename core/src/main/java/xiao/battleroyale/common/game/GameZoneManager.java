package xiao.battleroyale.common.game;

import xiao.battleroyale.api.game.zone.IGameZoneReadApi;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.zone.ZoneManager;

import java.util.List;

public class GameZoneManager {

    private static final IGameZoneReadApi zoneManagerInstance = ZoneManager.get();

    public static List<IGameZone> getGameZones() { return zoneManagerInstance.getGameZones(); }
    public static List<IGameZone> getCurrentGameZones() { return zoneManagerInstance.getCurrentGameZones(); }
    public static List<IGameZone> getCurrentGameZones(int gameTime) { return zoneManagerInstance.getCurrentGameZones(gameTime); }
    public static IGameZone getGameZone(int zoneId) { return zoneManagerInstance.getGameZone(zoneId); }

    public static IGameZoneReadApi getApi() {
        return zoneManagerInstance;
    }
}
