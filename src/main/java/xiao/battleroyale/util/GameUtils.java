package xiao.battleroyale.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
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

import java.util.List;
import java.util.UUID;

import static xiao.battleroyale.util.CommandUtils.buildIntBracketWithColor;
import static xiao.battleroyale.util.CommandUtils.buildIntBracketWithFullColor;

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

    public static MutableComponent buildGamePlayerText(@NotNull GamePlayer gamePlayer, ChatFormatting nameColor) {
        TextColor color = TextColor.fromRgb(ColorUtils.parseColorToInt(gamePlayer.getGameTeamColor()) & 0xFFFFFF);

        return Component.empty()
                .append(buildIntBracketWithColor(gamePlayer.getGameTeamId(), color))
                .append(buildIntBracketWithFullColor(gamePlayer.getGameSingleId(), color))
                .append(Component.literal(gamePlayer.getPlayerName()).withStyle(nameColor));
    }
}
