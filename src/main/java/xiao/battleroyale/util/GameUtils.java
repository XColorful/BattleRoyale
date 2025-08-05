package xiao.battleroyale.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.LootNBTTag;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.UUID;

public class GameUtils {

    /**
     * 计算给定玩家列表中所有玩家的平均中心点
     * 如果玩家列表为空，则返回 Vec3.ZERO
     * @param gamePlayers 游戏玩家列表
     * @return 所有玩家的平均中心点
     */
    @NotNull
    public static Vec3 calculatePlayerCenter(List<GamePlayer> gamePlayers) {
        if (gamePlayers.isEmpty()) {
            return Vec3.ZERO;
        }

        int n = gamePlayers.size();
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;

        for (GamePlayer gamePlayer : gamePlayers) {
            Vec3 pos = gamePlayer.getLastPos();
            sumX += pos.x;
            sumY += pos.y;
            sumZ += pos.z;
        }

        return new Vec3(sumX / n, sumY / n, sumZ / n);
    }

    /**
     * 计算玩家平均中心点作为终点，线性插值
     * @param startVec 起始点
     * @param gamePlayers 游戏玩家列表
     * @param delta 插值因子
     * @return 插值后的 Vec3
     */
    public static Vec3 calculateCenterAndLerp(Vec3 startVec, List<GamePlayer> gamePlayers, double delta) {
        Vec3 playerCenter = calculatePlayerCenter(gamePlayers);
        return Vec3Utils.lerp(startVec, playerCenter, delta);
    }

    /**
     * 添加游戏UUID
     */
    public static void addGameId(ItemStack itemStack, UUID gameId) {
        itemStack.getOrCreateTag().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
    }
    public static void addGameId(Entity entity, UUID gameId) {
        entity.getPersistentData().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
    }
    /**
     * 添加游戏UUID
     * 此方法不适用于本模组的方块
     */
    public static void addGameId(BlockEntity blockEntity, UUID gameId) {
        blockEntity.getPersistentData().putUUID(LootNBTTag.GAME_ID_TAG, gameId);
        blockEntity.setChanged();
    }

    /**
     * 获取物品掉落物或实体的GameUUID
     */
    public static @Nullable UUID getGameId(Entity entity) {
        UUID entityGameId = null;
        if (entity instanceof ItemEntity itemEntity) { // 物品掉落物，位于{Item:{tag:{GameId:UUID}}}
            ItemStack itemStack = itemEntity.getItem();
            CompoundTag itemTag = itemStack.getOrCreateTag();
            if (itemTag.hasUUID(LootNBTTag.GAME_ID_TAG)) {
                entityGameId = itemTag.getUUID(LootNBTTag.GAME_ID_TAG);
            }
        } else { // 一般实体，位于{ForgeData:{GameId:UUID}}
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
    public static @Nullable UUID getGameId(BlockEntity blockEntity) {
        CompoundTag forgeData = blockEntity.getPersistentData();
        if (forgeData.hasUUID(LootNBTTag.GAME_ID_TAG)) {
            return forgeData.getUUID(LootNBTTag.GAME_ID_TAG);
        }
        return null;
    }

    public record GameTimeFormat(int gameTime, int remainTick, float remainSeconds, int seconds, int minutes, int hours) {
        public GameTimeFormat(int gameTime) {
            this(
                    gameTime, // tickTime
                    gameTime % 20, // remainTick
                    (float) (gameTime % 20) / 20.0f, // remainSeconds

                    // 计算秒、分钟、小时
                    (gameTime / 20) % 60, // seconds
                    ((gameTime / 20) / 60) % 60, // minutes
                    ((gameTime / 20) / 3600) // hours
            );
        }
        public String toTimeString() {
            if (hours > 0) {
                return String.format("%dh %dm %ds", hours, minutes, seconds);
            } else if (minutes > 0) {
                return String.format("%dm %ds", minutes, seconds);
            } else {
                return String.format("%ds", seconds);
            }
        }
        public String toFormattedString(boolean includeRemainder) {
            if (includeRemainder && remainTick > 0) {
                if (hours > 0) {
                    return String.format("%dh %dm %.2fs", hours, minutes, seconds + remainSeconds);
                } else if (minutes > 0) {
                    return String.format("%dm %.2fs", minutes, seconds + remainSeconds);
                } else {
                    return String.format("%.2fs", seconds + remainSeconds);
                }
            } else {
                return toTimeString();
            }
        }
        public String toFullString(boolean includeRemainder) {
            return "GameTime:" + gameTime + "(" + toFormattedString(includeRemainder) + ")";
        }
        public String toSpaceFullString(boolean includeRemainder) {
            return " GameTime:" + gameTime + "(" + toFormattedString(includeRemainder) + ")";
        }
    }

    public static void clearGamePlayerInventory(@NotNull ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
        if (player == null) {
            BattleRoyale.LOGGER.debug("Failed to clear GamePlayer ({}) inventory by UUID: {}", gamePlayer.getPlayerName(), gamePlayer.getPlayerUUID());
            return;
        }
        player.getInventory().clearContent();
    }

    public static void clearGamePlayersInventory(ServerLevel serverLevel, List<GamePlayer> gamePlayers) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("ServerLevel is null, failed to clear GamePlayers Inventory");
            return;
        }
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer == null) {
                continue;
            }
            clearGamePlayerInventory(serverLevel, gamePlayer);
        }
    }
}
