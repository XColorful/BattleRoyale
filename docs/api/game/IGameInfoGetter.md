```java
package xiao.battleroyale.api.game;

public interface IGameInfoGetter {
    int getGameTime();  
    UUID getGameId();
    Vec3 getGlobalCenterOffset();
    int getWinnerTeamTotal();
    ServerLevel getServerLevel();
}
```