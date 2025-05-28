package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.util.ChatUtils;
import net.minecraft.ChatFormatting;

public class TeamCommand {

    private static final String TEAM_NAME = "team";

    private static final String JOIN_NAME = "join";
    private static final String LEAVE_NAME = "leave";
    private static final String KICK_NAME = "kick";
    private static final String INVITE_NAME = "invite";
    private static final String REQUEST_NAME = "request";
    private static final String ACCEPT_NAME = "accept";
    private static final String DECLINE_NAME = "decline";
    private static final String PLAYER_ARG_NAME = "player";
    private static final String SENDER_NAME_ARG_NAME = "senderName";
    private static final String REQUESTER_NAME_ARG_NAME = "requesterName";
    private static final String TEAM_ID_ARG_NAME = "teamId";
    private static final String TEAM_ID_QUERY_NAME = "id";


    /**
     * 获取队伍相关命令的字面量参数构建器。
     * 包含加入、离开、踢人、邀请、接受和拒绝邀请等子命令。
     * @return 字面量参数构建器。
     */
    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEAM_NAME)
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.literal(JOIN_NAME)
                        .executes(TeamCommand::joinTeam) // /battleroyale team join
                        .then(Commands.argument(TEAM_ID_ARG_NAME, IntegerArgumentType.integer(1))
                                .executes(TeamCommand::joinTeamSpecific) // /battleroyale team join <teamId>
                        )
                )
                .then(Commands.literal(LEAVE_NAME)
                        .executes(TeamCommand::leaveTeam) // /battleroyale team leave
                )
                .then(Commands.literal(KICK_NAME)
                        .then(Commands.argument(PLAYER_ARG_NAME, EntityArgument.player()) // 玩家对象
                                .executes(TeamCommand::kickPlayer) // /battleroyale team kick <player>
                        )
                )
                .then(Commands.literal(INVITE_NAME)
                        .then(Commands.argument(PLAYER_ARG_NAME, EntityArgument.player()) // 玩家对象
                                .executes(TeamCommand::invitePlayer) // /battleroyale team invite <player>
                        )
                )
                .then(Commands.literal(ACCEPT_NAME)
                        .then(Commands.literal(INVITE_NAME) // /battleroyale team accept invite <senderName>
                                .then(Commands.argument(SENDER_NAME_ARG_NAME, EntityArgument.player()) // 邀请者名称
                                        .executes(TeamCommand::acceptInvite)
                                )
                        )
                        .then(Commands.literal(REQUEST_NAME) // /battleroyale team accept request <requesterName>
                                .then(Commands.argument(REQUESTER_NAME_ARG_NAME, EntityArgument.player()) // 申请者名称
                                        .executes(TeamCommand::acceptRequest)
                                )
                        )
                )
                .then(Commands.literal(DECLINE_NAME)
                        .then(Commands.literal(INVITE_NAME) // /battleroyale team decline invite <senderName>
                                .then(Commands.argument(SENDER_NAME_ARG_NAME, EntityArgument.player()) // 邀请者名称
                                        .executes(TeamCommand::declineInvite)
                                )
                        )
                        .then(Commands.literal(REQUEST_NAME) // /battleroyale team decline request <requesterName>
                                .then(Commands.argument(REQUESTER_NAME_ARG_NAME, EntityArgument.player()) // 申请者名称
                                        .executes(TeamCommand::declineRequest)
                                )
                        )
                )
                // 仅用于发送请求的命令 (request <targetPlayer>)
                .then(Commands.literal(REQUEST_NAME)
                        .then(Commands.argument(PLAYER_ARG_NAME, EntityArgument.player()) // 目标玩家的 ServerPlayer 对象 (请求目标队长)
                                .executes(TeamCommand::requestPlayer) // /battleroyale team request <player>
                        )
                )
                .then(Commands.literal(TEAM_ID_QUERY_NAME)
                        .requires(CommandSourceStack::isPlayer)
                        .executes(TeamCommand::queryPlayerTeamId) // /battleroyale team id
                );
    }

    private static int joinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeamManager.get().joinTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int joinTeamSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID_ARG_NAME);
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
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER_ARG_NAME);
        TeamManager.get().kickPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER_ARG_NAME);
        TeamManager.get().invitePlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int acceptInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, SENDER_NAME_ARG_NAME);
        TeamManager.get().acceptInvite(player, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int declineInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, SENDER_NAME_ARG_NAME);
        TeamManager.get().declineInvite(player, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int requestPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER_ARG_NAME);
        TeamManager.get().RequestPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int acceptRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer teamLeader = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, REQUESTER_NAME_ARG_NAME);
        TeamManager.get().acceptRequest(teamLeader, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int declineRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer teamLeader = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, REQUESTER_NAME_ARG_NAME);
        TeamManager.get().declineRequest(teamLeader, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int queryPlayerTeamId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeamManager.get().sendPlayerTeamId(player);
        return Command.SINGLE_SUCCESS;
    }
}