package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import static xiao.battleroyale.command.CommandArg.*;

public class GameLobbyManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAME_LOBBY_MANAGER)
                // IGameLobbyReadApi
                .then(Commands.literal(SEND_LOBBY_TELEPORT_MESSAGE)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(GameLobbyManagerCommand::sendLobbyTeleportMessageCheckWinner)
                                .then(Commands.argument(IS_WINNER, BoolArgumentType.bool())
                                        .executes(GameLobbyManagerCommand::sendLobbyTeleportMessage)
                                )
                        )
                )
                .then(Commands.literal(IS_LOBBY_CREATED).executes(GameLobbyManagerCommand::isLobbyCreated))
                .then(Commands.literal(LOBBY_MUTEKI).executes(GameLobbyManagerCommand::lobbyMuteki))
                .then(Commands.literal(LOBBY_HEAL).executes(GameLobbyManagerCommand::lobbyHeal))
                .then(Commands.literal(LOBBY_CHANGE_GAMEMODE).executes(GameLobbyManagerCommand::lobbyChangeGameMode))
                .then(Commands.literal(TELEPORT_DROP_INVENTORY).executes(GameLobbyManagerCommand::teleportDropInventory))
                .then(Commands.literal(TELEPORT_CLEAR_INVENTORY).executes(GameLobbyManagerCommand::teleportClearInventory))
                .then(Commands.literal(IS_IN_LOBBY_RANGE)
                        .then(Commands.argument(POS, Vec3Argument.vec3())
                                .executes(GameLobbyManagerCommand::isInLobbyRange)
                        )
                )
                .then(Commands.literal(CAN_MUTEKI)
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .executes(GameLobbyManagerCommand::canMuteki)
                        )
                );
    }

    // --------IGameLobbyReadApi--------

    private static int sendLobbyTeleportMessageCheckWinner(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof ServerPlayer player)) return 0;
        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable GamePlayer gamePlayer = gameManager.getTeamManager().getGamePlayerByUUID(player.getUUID());
        boolean isWinner = gamePlayer != null && gameManager.getWinnerGamePlayers().contains(gamePlayer);
        gameManager.getGameLobbyManager().sendLobbyTeleportMessage(player, isWinner);
        return Command.SINGLE_SUCCESS;
    }
    private static int sendLobbyTeleportMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof ServerPlayer player)) return 0;
        BattleRoyale.getGameManager().getGameLobbyManager().sendLobbyTeleportMessage(player, BoolArgumentType.getBool(context, IS_WINNER));
        return Command.SINGLE_SUCCESS;
    }
    private static int isLobbyCreated(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLobbyManager().isLobbyCreated() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int lobbyMuteki(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLobbyManager().lobbyMuteki() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int lobbyHeal(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLobbyManager().lobbyHeal() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int lobbyChangeGameMode(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLobbyManager().lobbyChangeGamemode() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int teleportDropInventory(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLobbyManager().teleportDropInventory() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int teleportClearInventory(CommandContext<CommandSourceStack> context) {
        return BattleRoyale.getGameManager().getGameLobbyManager().teleportClearInventory() ? Command.SINGLE_SUCCESS : 0;
    }
    private static int isInLobbyRange(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Vec3 pos = Vec3Argument.getVec3(context, POS);
        return BattleRoyale.getGameManager().getGameLobbyManager().isInLobbyRange(pos) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int canMuteki(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, PLAYER);
        if (!(entity instanceof LivingEntity livingEntity)) return 0;
        return BattleRoyale.getGameManager().getGameLobbyManager().canMuteki(livingEntity) ? Command.SINGLE_SUCCESS : 0;
    }
}
