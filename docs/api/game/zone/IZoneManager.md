```java
package xiao.battleroyale.api.game.zone;

public interface IZoneManager extends IGameSubManager, IGameZoneReadApi {
    void setStackZoneConfig(boolean turn);
    void randomizeZoneTickOffset();
    ZoneManager.ZoneContext getCommonZoneContext();
    ZoneManager.ZoneContext getZoneContextInGame();
}
```