```java
package xiao.battleroyale.api.game;

public interface IGameIdWriteApi {
    void addGameId(ItemStack itemStack, UUID gameId);
    void addGameId(Entity entity, UUID gameId);
    void addGameId(BlockEntity blockEntity, UUID gameId);
}
```