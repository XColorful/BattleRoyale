package xiao.battleroyale.common.game.battleroyale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.Set;

import static xiao.battleroyale.util.CommandUtils.*;
import static xiao.battleroyale.util.GameUtils.buildGamePlayerText;

public class BRGameNotification {

    protected static void notifyGamePlayerIsInactive(ServerLevel serverLevel, GamePlayer gamePlayer) {
        if (serverLevel != null) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_leaved_from_level", gamePlayer.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null in notifyGamePlayerIsInactive(GamePlayer {})", gamePlayer.getPlayerName());
        }
    }
    protected static void notifyGamePlayerIsActive(ServerLevel serverLevel, GamePlayer gamePlayer) {
        if (serverLevel != null) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_backed_to_level", gamePlayer.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null in notifyGamePlayerIsActive(GamePlayer {})", gamePlayer.getPlayerName());
        }
    }

    // 发送胜利队伍消息
    protected static void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams, int gameTime) {
        MutableComponent winnerComponent = Component.empty()
                .append(Component.translatable("battleroyale.message.game_time", gameTime, new GameUtils.GameTimeFormat(gameTime).toFormattedString(true)));
        for (GameTeam team : winnerGameTeams) {
            // 队伍ID
            TextColor color = TextColor.fromRgb(ColorUtils.parseColorToInt(team.getGameTeamColor()));
            MutableComponent teamComponent = Component.empty()
                    .append(buildSuggestableIntBracketWithColor(team.getGameTeamId(), TeamCommand.requestTeamCommand(team.getGameTeamId()), color));
            // 队长
            GamePlayer leader = team.getLeader();
            teamComponent.append(Component.literal(" "))
                    .append(buildSuggestableIntBracketWithFullColor(leader.getGameSingleId(), TeamCommand.requestPlayerCommand(leader.getPlayerName()), color))
                    .append(Component.literal(leader.getPlayerName()).withStyle(leader.isEliminated() ? ChatFormatting.GRAY : ChatFormatting.GOLD));
            // 队员
            for (GamePlayer member : team.getTeamMembers()) {
                if (member.getGameSingleId() == leader.getGameSingleId()) {
                    continue;
                }
                teamComponent.append(Component.literal(" "))
                        .append(buildSuggestableIntBracketWithFullColor(member.getGameSingleId(), TeamCommand.requestPlayerCommand(member.getPlayerName()), color))
                        .append(Component.literal(member.getPlayerName()).withStyle(member.isEliminated() ? ChatFormatting.GRAY : ChatFormatting.GOLD));
            }
            // 添加到消息
            winnerComponent.append(Component.literal("\n")
                    .append(teamComponent));
        }
        if (serverLevel != null) {
            ChatUtils.sendMessageToAllPlayers(serverLevel, winnerComponent);
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, winner result: {}", winnerComponent);
        }
    }

    /**
     * 大吉大利！今晚吃鸡！
     * 附加烟花，粒子效果（人机不触发）
     */
    protected static void notifyWinner(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, int winnerParticleId) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("Failed to notify winner {}", gamePlayer.getNameWithId());
            return;
        }
        @Nullable ServerPlayer notifiedPlayer = serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        if (notifiedPlayer == null) {
            BattleRoyale.LOGGER.info("Skipped to notify winner game player {}", gamePlayer.getNameWithId());
            return;
        }

        int teamId = gamePlayer.getGameTeamId();
        int colorRGB = ColorUtils.parseColorToInt(gamePlayer.getGameTeamColor()) & 0xFFFFFF;
        TextColor textColor = TextColor.fromRgb(colorRGB);

        Component winnerTitle = Component.translatable("battleroyale.message.winner_message")
                .withStyle(ChatFormatting.GOLD);

        Component teamWinMessage = Component.translatable("battleroyale.message.team", teamId)
                .withStyle(Style.EMPTY.withColor(textColor))
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.has_won_the_game")
                        .withStyle(ChatFormatting.WHITE));

        ChatUtils.sendTitlesToPlayer(notifiedPlayer, winnerTitle, teamWinMessage, 10, 80, 20);

        // 暂时硬编码
        EffectManager.get().spawnPlayerFirework(notifiedPlayer, 16, 4, 1.0F, 16.0F);
        EffectManager.get().addGameParticle(serverLevel, notifiedPlayer.position(), winnerParticleId, 0);
    }

    public static void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate) {
        String spectateCommand = GameCommand.spectateCommand();

        IGameManager gameManager = BattleRoyale.getGameManager();
        int gameTime = gameManager.getGameTime();
        Component fullMessage = Component.translatable("battleroyale.message.has_game_in_progress")
                .append(Component.literal("\n"))
                // 游戏时长：int(time)
                .append(Component.translatable("battleroyale.message.game_time", gameTime, new GameUtils.GameTimeFormat(gameTime).toFormattedString(true)))
                .append(Component.literal(" "))
                // 生存 int/int
                .append(Component.translatable("battleroyale.label.alive"))
                .append(Component.literal(" "))
                .append(Component.literal(String.valueOf(GameTeamManager.getStandingGamePlayers().size())).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("/" + GameTeamManager.getGamePlayers().size()))
                .append(Component.literal(" "))
                // [观战]
                .append(buildRunnableText(Component.translatable("battleroyale.message.spectate"), spectateCommand, allowSpectate ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));

        ChatUtils.sendComponentMessageToPlayer(player, fullMessage);
    }

    public static void sendDownMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("ServerLevel is null, failed to send GamePlayer {} down", gamePlayer.getPlayerName());
            return;
        }
        MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.GRAY)
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.is_downed"));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
    }
    public static void sendReviveMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("ServerLevel is null, failed to send GamePlayer {} revive", gamePlayer.getPlayerName());
            return;
        }
        MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.GREEN)
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.is_revived"));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
    }
    public static void sendEliminateMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("ServerLevel is null, failed to send GamePlayer {} eliminate", gamePlayer.getPlayerName());
            return;
        }
        MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.RED)
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.is_eliminated"));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
    }
}
