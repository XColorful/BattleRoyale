package xiao.battleroyale.common.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.IGameIdReadApi;
import xiao.battleroyale.api.game.IGameIdWriteApi;
import xiao.battleroyale.api.loot.LootNBTTag;

import java.util.UUID;

public class GameIdHelper implements IGameIdReadApi, IGameIdWriteApi {

    private static class GameIdHelperHolder {
        private static GameIdHelper INSTANCE = new GameIdHelper();
    }

    public static GameIdHelper getApi() {
        return GameIdHelperHolder.INSTANCE;
    }

    /**
     * 获取物品掉落物或实体的GameUUID
     */
    @Override public @Nullable UUID getGameId(Entity entity) {
        UUID entityGameId = null;
        if (entity instanceof ItemEntity itemEntity) { // 物品掉落物，位于{Item:{jsonTag:{GameId:UUID}}}
            ItemStack itemStack = itemEntity.getItem();
            CompoundTag itemTag = itemStack.getOrCreateTag();
            if (itemTag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                entityGameId = itemTag.getUUID(LootNBTTag.GAME_ID_TAG);
            }
        } else { // 一般实体，位于{ForgeData:{GameId:UUID}}
//            CompoundTag persistentData = entity.getPersistentData();
//            if (persistentData.hasUUID(LootNBTTag.GAME_ID_TAG)) {
//                entityGameId = persistentData.getUUID(LootNBTTag.GAME_ID_TAG);
//            }
            for (String tag : entity.getTags()) {
                if (tag.length() == TARGET_LENGTH && tag.startsWith(ID_PREFIX)) {
                    try {
                        entityGameId = UUID.fromString(tag.substring(ID_PREFIX.length()));
                        break;
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
        return entityGameId;
    }
    /**
     * 获取原版BlockEntity的GameUUID
     * 此方法不适用于本模组的方块
     */
    @Override public @Nullable UUID getGameId(BlockEntity blockEntity) {
//        CompoundTag forgeData = blockEntity.getPersistentData();
//        if (forgeData.hasUUID(LootNBTTag.GAME_ID_TAG)) {
//            return forgeData.getUUID(LootNBTTag.GAME_ID_TAG);
//        }
        return null;
    }
    /**
     * 获取 ItemStack 的GameUUID
     */
    @Override public @Nullable UUID getGameId(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            CompoundTag tag = itemStack.getTag();
            if (tag != null && tag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                return tag.getUUID(LootNBTTag.GAME_ID_TAG);
            }
        }
        return null;
    }

    /**
     * 添加游戏UUID
     */
    @Override public void addGameId(ItemStack itemStack, UUID gameId) {
        itemStack.getOrCreateTag().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
    }
    @Override public void addGameId(Entity entity, UUID gameId) {
//        entity.getPersistentData().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
        this.removeGameId(entity);
        entity.addTag(ID_PREFIX + gameId.toString());
    }
    /**
     * 添加游戏UUID
     * 此方法不适用于本模组的方块
     */
    @Override public void addGameId(BlockEntity blockEntity, UUID gameId) {
//        blockEntity.getPersistentData().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
        blockEntity.setChanged();
    }

    /**
     * 移除游戏UUID
     */
    @Override public void removeGameId(ItemStack itemStack) {
        itemStack.getOrCreateTag().remove(LootNBTTag.GAME_ID_TAG);
    }
    @Override public void removeGameId(Entity entity) {
//        entity.getPersistentData().remove(LootNBTTag.GAME_ID_TAG);
        entity.getTags().removeIf(tag -> tag.length() == TARGET_LENGTH && tag.startsWith(ID_PREFIX));
    }
    /**
     * 移除游戏UUID
     * 此方法不适用于本模组的方块
     */
    @Override public void removeGameId(BlockEntity blockEntity) {
//        blockEntity.getPersistentData().remove(LootNBTTag.GAME_ID_TAG);
        blockEntity.setChanged();
    }

    // fabric only
    private static final String ID_PREFIX = LootNBTTag.GAME_ID_TAG + ":";
    private static final int TARGET_LENGTH = ID_PREFIX.length() + 36;
}
