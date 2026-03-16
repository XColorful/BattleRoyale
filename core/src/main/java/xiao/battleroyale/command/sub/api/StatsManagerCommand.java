package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameMainManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import static xiao.battleroyale.command.CommandArg.*;

public class StatsManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(STATS_MANAGER)
                // IStatsManager
                .then(Commands.literal(SHOULD_RECORD_STATS).executes(StatsManagerCommand::shouldRecordStats))
                .then(Commands.literal(IS_IN_RECORD_GAME_PLAYERS)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(StatsManagerCommand::isInRecordGamePlayersByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(StatsManagerCommand::isInRecordGamePlayersByGamePlayerId)
                                )
                        )
                )
                .then(Commands.literal(SAVE_STATS)
                        .executes(StatsManagerCommand::saveStats)
                        .then(Commands.argument(PATH, StringArgumentType.string())
                                .executes(StatsManagerCommand::saveStatsWithPath)
                        )
                );
    }

    // --------IStatsManager--------

    private static int shouldRecordStats(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getStatsManager().shouldRecordStats() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInRecordGamePlayersByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(entity.getUUID());
        return gamePlayer != null && gameManager.getStatsManager().isInRecordGamePlayers(gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInRecordGamePlayersByGamePlayerId(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        int playerId = IntegerArgumentType.getInteger(context, ID);
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        return gamePlayer != null && gameManager.getStatsManager().isInRecordGamePlayers(gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int saveStats(CommandContext<CommandSourceStack> context) {
        try {
            BattleRoyale.getGameManager().getStatsManager().saveStats();
            return Command.SINGLE_SUCCESS;
        } catch (Exception ignored) {
            return 0;
        }
    }
    private static int saveStatsWithPath(CommandContext<CommandSourceStack> context) {
        String path = StringArgumentType.getString(context, PATH);
        try {
            BattleRoyale.getGameManager().getStatsManager().saveStats(path);
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to save stats to {} via api command: {}", path, e.getMessage());
            return 0;
        }
    }
}
