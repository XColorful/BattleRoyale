package xiao.battleroyale.api.item.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlockItemDataAccessor {
    String BLOCK_GAME_ID = "BlockGameId"; // 修改 NBT 标签名称

    @Nonnull
    default UUID getBlockGameId(ItemStack block) {
        CompoundTag nbt = block.getOrCreateTag();
        if (nbt.hasUUID(BLOCK_GAME_ID)) {
            return nbt.getUUID(BLOCK_GAME_ID);
        }
        // 返回一个默认的 UUID，或者 null，取决于你的逻辑
        return new UUID(0, 0); // 示例默认 UUID
    }

    default void setBlockGameId(ItemStack block, @Nullable UUID gameId) {
        CompoundTag nbt = block.getOrCreateTag();
        if (gameId != null) {
            nbt.putUUID(BLOCK_GAME_ID, gameId);
            return;
        }
        nbt.remove(BLOCK_GAME_ID); // 如果 gameId 为 null，则移除 NBT 标签
    }
}