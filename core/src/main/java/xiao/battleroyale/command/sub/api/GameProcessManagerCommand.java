package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameMainManager;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import static xiao.battleroyale.command.CommandArg.*;

public class GameProcessManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAME_PROCESS_MANAGER)
                // IGameProcessManager
                .then(Commands.literal(CHECK_IF_GAME_SHOULD_END_AND_FINISH).executes(GameProcessManagerCommand::checkIfGameShouldEndAndFinish))
                .then(Commands.literal(FINISH_GAME_IF_SHOULD_END).executes(GameProcessManagerCommand::finishGameIfShouldEnd))
                // IGameManagement
                .then(Commands.literal(CHECK_AND_UPDATE_INVALID_GAME_PLAYER).executes(GameProcessManagerCommand::checkAndUpdateInvalidGamePlayer))
                .then(Commands.literal(TELEPORT_TO_LOBBY_IN_GAME)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(GameProcessManagerCommand::teleportToLobbyInGame)))
                .then(Commands.literal(TELEPORT_AFTER_GAME)
                        .then(Commands.argument(TELEPORT_WINNER, BoolArgumentType.bool())
                                .then(Commands.argument(TELEPORT_NON_WINNER, BoolArgumentType.bool())
                                        .executes(GameProcessManagerCommand::teleportAfterGame))))
                .then(Commands.literal(SPECTATE_GAME)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(GameProcessManagerCommand::spectateGame)))
                .then(Commands.literal(HEAL_GAME_PLAYERS).executes(GameProcessManagerCommand::healGamePlayers))
                .then(Commands.literal(FINISH_GAME_ADD_WINNER)
                        .then(Commands.argument(HAS_WINNER, BoolArgumentType.bool())
                                .executes(GameProcessManagerCommand::finishGameAddWinner)))
                // IGameNotification
                .then(Commands.literal(SEND_WINNER_RESULT).executes(GameProcessManagerCommand::sendWinnerResult))
                .then(Commands.literal(NOTIFY_WINNER)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(GameProcessManagerCommand::notifyWinner)
                                )
                        )
                )
                .then(Commands.literal(SEND_GAME_SPECTATE_MESSAGE)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .then(Commands.argument(ALLOW_SPECTATE, BoolArgumentType.bool())
                                        .executes(GameProcessManagerCommand::sendGameSpectateMessage)
                                )
                        )
                )
                .then(Commands.literal(SEND_DOWN_MESSAGE)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(GameProcessManagerCommand::sendDownMessageByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(GameProcessManagerCommand::sendDownMessageByGamePlayerId)
                                )
                        )
                )
                .then(Commands.literal(SEND_REVIVE_MESSAGE)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(GameProcessManagerCommand::sendReviveMessageByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(GameProcessManagerCommand::sendReviveMessageByGamePlayerId)
                                )
                        )
                )
                .then(Commands.literal(SEND_ELIMINATE_MESSAGE)
                        .then(Commands.literal(BY_PLAYER)
                                .then(Commands.argument(PLAYER, EntityArgument.entity())
                                        .executes(GameProcessManagerCommand::sendEliminateMessageByPlayer)
                                )
                        )
                        .then(Commands.literal(BY_ID)
                                .then(Commands.argument(ID, IntegerArgumentType.integer(0))
                                        .executes(GameProcessManagerCommand::sendEliminateMessageByGamePlayerId)
                                )
                        )
                );
    }

    // --------IGameProcessManager--------

    private static int checkIfGameShouldEndAndFinish(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        if (!gameManager.isInGame()) return 0;
        gameManager.getGameProcessManager().checkIfGameShouldEndAndFinish();
        return !gameManager.isInGame() // 之前在游戏，执行后结束了游戏
                ? Command.SINGLE_SUCCESS : 0;
    }
    private static int finishGameIfShouldEnd(CommandContext<CommandSourceStack> context) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (!gameManager.isInGame()) return 0;
        gameManager.getGameProcessManager().finishGameIfShouldEnd(gameManager);
        return !gameManager.isInGame() ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IGameManagement--------

    private static int checkAndUpdateInvalidGamePlayer(CommandContext<CommandSourceStack> context) {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        gameManager.getGameProcessManager().checkAndUpdateInvalidGamePlayer(gameManager.getServerLevel());
        return Command.SINGLE_SUCCESS;
    }
    private static int teleportToLobbyInGame(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity player)) return 0;
        BattleRoyale.getGameManager().getGameProcessManager().teleportToLobbyInGame(player);
        return Command.SINGLE_SUCCESS;
    }
    private static int teleportAfterGame(CommandContext<CommandSourceStack> context) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) return 0;
        boolean teleportWinner = BoolArgumentType.getBool(context, TELEPORT_WINNER);
        boolean teleportNonWinner = BoolArgumentType.getBool(context, TELEPORT_NON_WINNER);
        gameManager.getGameProcessManager().teleportAfterGame(serverLevel, gameManager.getWinnerGamePlayers(), gameManager.getWinnerGameTeams(),
                teleportWinner, teleportNonWinner);
        return Command.SINGLE_SUCCESS;
    }
    private static int spectateGame(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof ServerPlayer player)) return 0;
        BattleRoyale.getGameManager().getGameProcessManager().spectateGame(player);
        return Command.SINGLE_SUCCESS;
    }
    private static int healGamePlayers(CommandContext<CommandSourceStack> context) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) return 0;
        gameManager.getGameProcessManager().healGamePlayers(serverLevel, gameManager.getTeamManager().getGamePlayers());
        return Command.SINGLE_SUCCESS;
    }
    private static int finishGameAddWinner(CommandContext<CommandSourceStack> context) {
        boolean hasWinner = BoolArgumentType.getBool(context, HAS_WINNER);
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (!gameManager.isInGame()) return 0;
        gameManager.getGameProcessManager().finishGameAddWinner(hasWinner);
        return !gameManager.isInGame() ? Command.SINGLE_SUCCESS : 0;
    }

    // --------IGameNotification--------

    private static int sendWinnerResult(CommandContext<CommandSourceStack> context) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) return 0;
        gameManager.getGameProcessManager().sendWinnerResult(serverLevel, gameManager.getWinnerGamePlayers(), gameManager.getWinnerGameTeams(), gameManager.getGameTime());
        return Command.SINGLE_SUCCESS;
    }
    private static int notifyWinner(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) return 0;
        Entity player = EntityArgument.getEntity(context, PLAYER);
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().notifyWinner(serverLevel, gamePlayer, IntegerArgumentType.getInteger(context, ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int sendGameSpectateMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        if (!(player instanceof ServerPlayer serverPlayer)) return 0;
        boolean allowSpectate = BoolArgumentType.getBool(context, ALLOW_SPECTATE);
        BattleRoyale.getGameManager().getGameProcessManager().sendGameSpectateMessage(serverPlayer, allowSpectate);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendDownMessageByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().sendDownMessage(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendDownMessageByGamePlayerId(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().sendDownMessage(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendReviveMessageByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().sendReviveMessage(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendReviveMessageByGamePlayerId(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().sendReviveMessage(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendEliminateMessageByPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(context, PLAYER);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().sendEliminateMessage(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendEliminateMessageByGamePlayerId(CommandContext<CommandSourceStack> context) {
        int playerId = IntegerArgumentType.getInteger(context, ID);
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerBySingleId(playerId);
        if (gamePlayer == null) return 0;
        gameManager.getGameProcessManager().sendEliminateMessage(gameManager.getServerLevel(), gamePlayer);
        return Command.SINGLE_SUCCESS;
    }
}
