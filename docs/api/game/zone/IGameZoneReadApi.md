```java
package xiao.battleroyale.api.game.zone;

public interface IGameZoneReadApi {
    List<IGameZone> getGameZones();
    List<IGameZone> getCurrentGameZones();
    List<IGameZone> getCurrentGameZones(int gameTime);
    @Nullable IGameZone getGameZone(int zoneId);
}
```