package xiao.battleroyale.common.game;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
        if (entity instanceof ItemEntity itemEntity) { // 物品掉落物，位于{Item:{components:{minecraft:custom_data:{GameId:[I; int, int, int, int]}}}}
            ItemStack itemStack = itemEntity.getItem();
            CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag itemTag = customData.copyTag();
                if (itemTag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                    entityGameId = itemTag.getUUID(LootNBTTag.GAME_ID_TAG);
                }
            }
        } else { // 一般实体，位于{NeoForgeData:{GameId:[I; int, int, int, int]}}
            CompoundTag persistentData = entity.getPersistentData();
            if (persistentData.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                entityGameId = persistentData.getUUID(LootNBTTag.GAME_ID_TAG);
            }
        }
        return entityGameId;
    }
    /**
     * 获取原版BlockEntity的GameUUID
     * 此方法不适用于本模组的方块
     */
    @Override public @Nullable UUID getGameId(BlockEntity blockEntity) {
        CustomData customData = blockEntity.components().get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                return tag.getUUID(LootNBTTag.GAME_ID_TAG);
            }
        }
        return null;
    }
    /**
     * 获取 ItemStack 的GameUUID
     */
    @Override public @Nullable UUID getGameId(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA); // 物品，位于{components:{"minecraft:custom_data":{GameId:[I; int, int, int, int]}}}
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                return tag.getUUID(LootNBTTag.GAME_ID_TAG);
            }
        }
        return null;
    }

    /**
     * 添加游戏UUID
     */
    @Override public void addGameId(ItemStack itemStack, UUID gameId) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putUUID(LootNBTTag.GAME_ID_TAG, gameId);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    @Override public void addGameId(Entity entity, UUID gameId) {
        entity.getPersistentData().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
    }
    /**
     * 添加游戏UUID
     * 此方法不适用于本模组的方块
     */
    @Override public void addGameId(BlockEntity blockEntity, UUID gameId) {
        CustomData customData = blockEntity.components().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putUUID(LootNBTTag.GAME_ID_TAG, gameId);
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                .build();
        PatchedDataComponentMap newComponents = PatchedDataComponentMap.fromPatch(
                blockEntity.components(),
                patch
        );
        blockEntity.setComponents(newComponents);

        blockEntity.setChanged();
    }
}
