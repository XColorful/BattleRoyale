package xiao.battleroyale.api.game.zone;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

import java.util.List;

public interface IGameZoneReadApi {

    List<IGameZone> getGameZones();
    List<IGameZone> getCurrentGameZones();
    List<IGameZone> getCurrentGameZones(int gameTime);
    @Nullable IGameZone getGameZone(int zoneId);
}
