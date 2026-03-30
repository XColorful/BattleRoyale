```java
package xiao.battleroyale.api.utility;

public interface ILobbyReadApi {
    @Nullable ResourceKey<Level> lobbyLevelKey();
    Vec3 lobbyPos();
    Vec3 lobbyDimension();
    
    boolean isInLobbyRange(Vec3 pos, @Nullable ServerLevel serverLevel);
    boolean canMuteki(@NotNull LivingEntity livingEntity);
}
```