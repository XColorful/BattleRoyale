package xiao.battleroyale.common.game;

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
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.Set;

import static xiao.battleroyale.util.CommandUtils.*;
import static xiao.battleroyale.util.GameUtils.buildGamePlayerText;

public class GameNotification {

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
    protected static void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GameTeam> winnerGameTeams, int gameTime, boolean initGameAfterGame) {
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
            // 游戏正常结束后自动初始化游戏
            if (initGameAfterGame) {
                GameManager.get().initGame(serverLevel);
            }
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, winner result: {}", winnerComponent);
        }
    }

    /**
     * 大吉大利！今晚吃鸡！
     * 附加烟花，粒子效果（人机不触发）
     */
    protected static void notifyWinner(@NotNull ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, @NotNull ServerPlayer notifiedPlayer, int winnerParticleId) {
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

        GameManager gameManager = GameManager.get();
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

    /**
     * 向玩家发送消息，传送回大厅
     */
    public static void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner) {
        String toLobbyCommand = GameCommand.toLobbyCommand();

        Component fullMessage = Component.translatable("battleroyale.message.back_to_lobby").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" "))
                .append(buildRunnableText(Component.translatable("battleroyale.message.teleport"),
                        toLobbyCommand,
                        isWinner ? ChatFormatting.GOLD :  ChatFormatting.GREEN));

        ChatUtils.sendComponentMessageToPlayer(player, fullMessage);
    }

    public static void sendDownMessage(@NotNull ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.GRAY)
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.is_downed"));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
    }
    public static void sendReviveMessage(@NotNull ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.GREEN)
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.is_revived"));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
    }
    public static void sendEliminateMessage(@NotNull ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.RED)
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.is_eliminated"));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
    }

    public static void sendSelectedConfigsInfo(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        GameManager gameManager = GameManager.get();
        int botConfigId = gameManager.getBotConfigId();
        int gameruleConfigId = gameManager.getGameruleConfigId();
        int spawnConfigId = gameManager.getSpawnConfigId();
        String zoneConfigFileName = gameManager.getZoneConfigFileName();
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_bot_config", botConfigId, gameManager.getBotConfigName(botConfigId)));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_gamerule_config", gameruleConfigId, gameManager.getGameruleConfigName(gameruleConfigId)));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_spawn_config", spawnConfigId, gameManager.getSpawnConfigName(spawnConfigId)));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_zone_config", zoneConfigFileName, GameConfigManager.get().getZoneConfigList().size()));
    }

    public static void sendSelectedConfigsInfo(ServerPlayer player) {
        if (player == null) {
            return;
        }

        GameManager gameManager = GameManager.get();
        int botConfigId = gameManager.getBotConfigId();
        int gameruleConfigId = gameManager.getGameruleConfigId();
        int spawnConfigId = gameManager.getSpawnConfigId();
        String zoneConfigFileName = gameManager.getZoneConfigFileName();
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_bot_config", botConfigId, gameManager.getBotConfigName(botConfigId)));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_gamerule_config", gameruleConfigId, gameManager.getGameruleConfigName(gameruleConfigId)));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_spawn_config", spawnConfigId, gameManager.getSpawnConfigName(spawnConfigId)));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_zone_config", zoneConfigFileName, GameConfigManager.get().getZoneConfigList().size()));
    }
}
