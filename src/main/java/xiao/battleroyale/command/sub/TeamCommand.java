package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.common.game.team.TeamManager;

import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.util.StringUtils.buildCommandString;

public class TeamCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEAM)
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.literal(JOIN)
                        .executes(TeamCommand::joinTeam)
                        .then(Commands.argument(TEAM_ID, IntegerArgumentType.integer(1))
                                .executes(TeamCommand::joinTeamSpecific)
                        )
                )
                .then(Commands.literal(LEAVE)
                        .executes(TeamCommand::leaveTeam)
                )
                .then(Commands.literal(KICK)
                        .then(Commands.argument(PLAYER, EntityArgument.player())
                                .executes(TeamCommand::kickPlayer)
                        )
                )
                .then(Commands.literal(INVITE)
                        .then(Commands.argument(PLAYER, EntityArgument.player())
                                .executes(TeamCommand::invitePlayer)
                        )
                )
                .then(Commands.literal(ACCEPT)
                        .then(Commands.literal(INVITE)
                                .then(Commands.argument(SENDER, EntityArgument.player())
                                        .executes(TeamCommand::acceptInvite)
                                )
                        )
                        .then(Commands.literal(REQUEST)
                                .then(Commands.argument(REQUESTER, EntityArgument.player())
                                        .executes(TeamCommand::acceptRequest)
                                )
                        )
                )
                .then(Commands.literal(DECLINE)
                        .then(Commands.literal(INVITE)
                                .then(Commands.argument(SENDER, EntityArgument.player())
                                        .executes(TeamCommand::declineInvite)
                                )
                        )
                        .then(Commands.literal(REQUEST)
                                .then(Commands.argument(REQUESTER, EntityArgument.player())
                                        .executes(TeamCommand::declineRequest)
                                )
                        )
                )
                .then(Commands.literal(REQUEST)
                        .then(Commands.argument(PLAYER, EntityArgument.player())
                                .executes(TeamCommand::requestPlayer)
                        )
                )
                .then(Commands.literal(ID)
                        .requires(CommandSourceStack::isPlayer)
                        .executes(TeamCommand::queryPlayerTeamId)
                );
    }

    private static int joinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeamManager.get().joinTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int joinTeamSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        TeamManager.get().joinTeamSpecific(player, teamId);
        return Command.SINGLE_SUCCESS;
    }

    private static int leaveTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeamManager.get().leaveTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int kickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER);
        TeamManager.get().kickPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER);
        TeamManager.get().invitePlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int acceptInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, SENDER);
        TeamManager.get().acceptInvite(player, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int declineInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, SENDER);
        TeamManager.get().declineInvite(player, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int requestPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER);
        TeamManager.get().RequestPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int acceptRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer teamLeader = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, REQUESTER);
        TeamManager.get().acceptRequest(teamLeader, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int declineRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer teamLeader = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, REQUESTER);
        TeamManager.get().declineRequest(teamLeader, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int queryPlayerTeamId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeamManager.get().sendPlayerTeamId(player);
        return Command.SINGLE_SUCCESS;
    }

    public static String acceptInviteCommandString(String senderName) {
        return buildCommandString(
                CommandArg.ROOT,
                TEAM,
                ACCEPT,
                INVITE,
                senderName
        );
    }

    public static String declineInviteCommandString(String senderName) {
        return buildCommandString(
                xiao.battleroyale.command.CommandArg.ROOT,
                TEAM,
                DECLINE,
                INVITE,
                senderName
        );
    }

    public static String acceptRequestCommandString(String name) {
        return buildCommandString(
                xiao.battleroyale.command.CommandArg.ROOT,
                TEAM,
                ACCEPT,
                REQUEST,
                name
        );
    }

    public static String declineRequestCommandString(String name) {
        return buildCommandString(
                xiao.battleroyale.command.CommandArg.ROOT,
                TEAM,
                DECLINE,
                REQUEST,
                name
        );
    }
}