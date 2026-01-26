```java
package xiao.battleroyale.api.game;

public interface IGameIdReadApi {
	@Nullable UUID getGameId(Entity entity);
	@Nullable UUID getGameId(BlockEntity blockEntity);
	@Nullable UUID getGameId(ItemStack itemStack);
}
```