package xiao.battleroyale.common.game;

import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.zone.ZoneManager;

import java.util.List;

public class GameZoneManager {

    private static final ZoneManager zoneManagerInstance = ZoneManager.get();

    public static List<IGameZone> getGameZones() { return zoneManagerInstance.getGameZones(); }
    public static List<IGameZone> getCurrentGameZones() { return zoneManagerInstance.getCurrentTickGameZones(GameManager.get().getGameTime()); }
    public static List<IGameZone> getCurrentGameZones(int gameTime) { return zoneManagerInstance.getCurrentTickGameZones(gameTime); }
    public static IGameZone getGameZone(int zoneId) { return zoneManagerInstance.getZoneById(zoneId); }

}
