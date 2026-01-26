```java
package xiao.battleroyale.api.utility;

public interface ILobbyReadApi {
    ResourceKey<Level> lobbyLevelKey();
    Vec3 lobbyPos();
    Vec3 lobbyDimension();
    
    boolean isInLobbyRange(Vec3 pos);
    boolean canMuteki(@NotNull LivingEntity livingEntity);
}
```