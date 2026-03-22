package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

import java.util.List;

public class _GameZoneManager {

    public static List<IGameZone> getGameZones() { return BattleRoyale.getGameManager().getZoneManager().getGameZones(); }
    public static List<IGameZone> getCurrentGameZones() { return BattleRoyale.getGameManager().getZoneManager().getCurrentGameZones(); }
    public static List<IGameZone> getCurrentGameZones(int gameTime) { return BattleRoyale.getGameManager().getZoneManager().getCurrentGameZones(gameTime); }
    public static IGameZone getGameZone(int zoneId) { return BattleRoyale.getGameManager().getZoneManager().getGameZone(zoneId); }

    public static IGameZoneReadApi getApi() {
        return BattleRoyale.getGameManager().getZoneManager();
    }
}
