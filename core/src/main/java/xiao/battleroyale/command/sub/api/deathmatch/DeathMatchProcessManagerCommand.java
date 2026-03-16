package xiao.battleroyale.command.sub.api.deathmatch;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import xiao.battleroyale.api.game.process.deathmatch.IDeathMatchProcessManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import static xiao.battleroyale.command.CommandArg.*;

public class DeathMatchProcessManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(DEATH_MATCH)
                // IDeathMatchInfoGetter
                .then(Commands.literal(GET_CURRENT_MAX_KILL).executes(DeathMatchProcessManagerCommand::getCurrentMaxKill))
                // IDeathMatchDataManagement
                .then(Commands.literal(ADD_GAME_PLAYER_KILL)
                        .then(Commands.argument(ADD_KILL, IntegerArgumentType.integer(0))
                                .then(Commands.literal(BY_PLAYER)
                                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                                .executes(DeathMatchProcessManagerCommand::addGamePlayerKillByPlayer)
                                        )
                                )
                                .then(Commands.literal(BY_ID)
                                        .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                                .executes(DeathMatchProcessManagerCommand::addGamePlayerKillByGamePlayerId)
                                        )
                                )
                        )
                )
                .then(Commands.literal(ADD_GAME_TEAM_KILL)
                        .then(Commands.argument(ADD_KILL, IntegerArgumentType.integer(0))
                                .then(Commands.literal(BY_PLAYER)
                                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                                .executes(DeathMatchProcessManagerCommand::addGameTeamKillByPlayer)
                                        )
                                )
                                .then(Commands.literal(BY_ID)
                                        .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                                .executes(DeathMatchProcessManagerCommand::addGameTeamKillByGameTeamId)
                                        )
                                )
                        )
                )
                .then(Commands.literal(ADD_AND_TRACK_RESTANDING_GAME_PLAYER)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(DeathMatchProcessManagerCommand::addAndTrackRestandingGamePlayerByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(DeathMatchProcessManagerCommand::addAndTrackRestandingGamePlayerByGamePlayerId)
                                )
                        )
                )
                // IDeathMatchGameManagement
                .then(Commands.literal(RESPAWN_GAME_PLAYER)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(DeathMatchProcessManagerCommand::respawnGamePlayerByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(DeathMatchProcessManagerCommand::respawnGamePlayerByGamePlayerId)
                                )
                        )
                );
    }

    private static @Nullable IDeathMatchProcessManager getDMProcessManager(IGameMainManager gameManager) {
        return gameManager.getGameProcessManager() instanceof IDeathMatchProcessManager dmProcessManager ? dmProcessManager : null;
    }

    // --------IDeathMatchInfoGetter--------

    private static int getCurrentMaxKill(CommandContext<CommandSourceStack> context) {
        @Nullable IDeathMatchProcessManager dmProcessManager = getDMProcessManager(BattleRoyale.getGameManager());
        return dmProcessManager != null ? dmProcessManager.getCurrentMaxKill() : 0;
    }

    // --------IDeathMatchDataManagement--------

    private static int addGamePlayerKillByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        @Nullable IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(entity.getUUID());
        return gamePlayer != null && dmProcessManager.addGamePlayerKill(gamePlayer, IntegerArgumentType.getInteger(context, ADD_KILL)) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int addGamePlayerKillByGamePlayerId(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        @Nullable IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        int playerId = IntegerArgumentType.getInteger(context, ID);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        return gamePlayer != null && dmProcessManager.addGamePlayerKill(gamePlayer, IntegerArgumentType.getInteger(context, ADD_KILL)) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int addGameTeamKillByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        @Nullable IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(entity.getUUID());
        return gamePlayer != null && dmProcessManager.addGameTeamKill(gamePlayer.getTeam(), IntegerArgumentType.getInteger(context, ADD_KILL)) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int addGameTeamKillByGameTeamId(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        int teamId = IntegerArgumentType.getInteger(context, ID);
        GameTeam gameTeam = gameManager.getTeamManager().getGameTeamById(teamId);
        return gameTeam != null && dmProcessManager.addGameTeamKill(gameTeam, IntegerArgumentType.getInteger(context, ADD_KILL)) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int addAndTrackRestandingGamePlayerByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException, CommandSyntaxException {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(entity.getUUID());
        return gamePlayer != null && dmProcessManager.addAndTrackRestandingGamePlayer(gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int addAndTrackRestandingGamePlayerByGamePlayerId(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        int playerId = IntegerArgumentType.getInteger(context, ID);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        return gamePlayer != null && dmProcessManager.addAndTrackRestandingGamePlayer(gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IDeathMatchGameManagement--------

    private static int respawnGamePlayerByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(entity.getUUID());
        return gamePlayer != null && dmProcessManager.respawnGamePlayer(context.getSource().getLevel(), gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int respawnGamePlayerByGamePlayerId(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        IDeathMatchProcessManager dmProcessManager = getDMProcessManager(gameManager);
        if (dmProcessManager == null) return 0;
        int playerId = IntegerArgumentType.getInteger(context, ID);
        GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        return gamePlayer != null && dmProcessManager.respawnGamePlayer(context.getSource().getLevel(), gamePlayer) ? Command.SINGLE_SUCCESS : 0;
    }
}
