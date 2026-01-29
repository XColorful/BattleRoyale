package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.BattleRoyale;

import java.util.List;

import static xiao.battleroyale.command.CommandArg.*;
import static xiao.battleroyale.util.StringUtils.buildCommandString;

public class TeamCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEAM)
                // .requires(CommandSourceStack::isPlayer) // 兼容命令方块内执行
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
                        // .requires(CommandSourceStack::isPlayer) // 兼容命令方块内执行
                        .executes(TeamCommand::queryPlayerTeamId)
                )
                .then(Commands.literal(ADD)
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument(PLAYER, EntityArgument.entity())
                                .then(Commands.argument(TEAM_ID, IntegerArgumentType.integer(1))
                                        .executes(TeamCommand::addToTeam)
                                )
                        )
                )
                .then(Commands.literal(BUILD)
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument(PLAYER, EntityArgument.entities())
                                .then(Commands.argument(SIZE, IntegerArgumentType.integer(1))
                                        .executes(context -> buildTeamForAll(context, false))
                                        .then(Commands.argument(FORCE_REBUILD, BoolArgumentType.bool())
                                                .executes(context -> buildTeamForAll(context, BoolArgumentType.getBool(context, FORCE_REBUILD)))
                                        )
                                )
                        )
                );
    }

    private static int joinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        BattleRoyale.getGameManager().getTeamManager().joinTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int joinTeamSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        BattleRoyale.getGameManager().getTeamManager().joinTeamSpecific(player, teamId);
        return Command.SINGLE_SUCCESS;
    }

    public static int leaveTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        BattleRoyale.getGameManager().getTeamManager().leaveTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int kickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER);
        BattleRoyale.getGameManager().getTeamManager().kickPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER);
        BattleRoyale.getGameManager().getTeamManager().invitePlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int acceptInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, SENDER);
        BattleRoyale.getGameManager().getTeamManager().acceptInvite(player, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int declineInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, SENDER);
        BattleRoyale.getGameManager().getTeamManager().declineInvite(player, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int requestPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER);
        BattleRoyale.getGameManager().getTeamManager().requestPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int acceptRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer teamLeader = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, REQUESTER);
        BattleRoyale.getGameManager().getTeamManager().acceptRequest(teamLeader, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int declineRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer teamLeader = context.getSource().getPlayerOrException();
        ServerPlayer senderPlayer = EntityArgument.getPlayer(context, REQUESTER);
        BattleRoyale.getGameManager().getTeamManager().declineRequest(teamLeader, senderPlayer);
        return Command.SINGLE_SUCCESS;
    }

    private static int queryPlayerTeamId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        BattleRoyale.getGameManager().getTeamManager().sendPlayerTeamId(player);
        return Command.SINGLE_SUCCESS;
    }

    public static int addToTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, PLAYER);
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID);
        if (!(target instanceof LivingEntity livingEntity)) { // 不能只是Entity
            source.sendFailure(Component.translatable("battleroyale.message.not_include_living_entity"));
            return 0;
        }

        if (BattleRoyale.getGameManager().getTeamManager().addToTeam(source, livingEntity, teamId)) {
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }

    public static int buildTeamForAll(CommandContext<CommandSourceStack> context, boolean forceRebuild) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        List<LivingEntity> livingEntities = EntityArgument.getEntities(context, PLAYER).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .toList();
        int teamSize = IntegerArgumentType.getInteger(context, SIZE);
        if (livingEntities.isEmpty()) { // 不能只是Entity
            source.sendFailure(Component.translatable("battleroyale.message.not_include_living_entity"));
            return 0;
        }

        int buildMemberCount = BattleRoyale.getGameManager().getTeamManager().buildTeamForAll(source, livingEntities, teamSize, forceRebuild);
        if (buildMemberCount > 0) {
            return Command.SINGLE_SUCCESS;
        } else {
            return 0;
        }
    }

    public static String joinCommand() {
        return buildCommandString(
                MOD_ID,
                TEAM,
                JOIN
        );
    }

    public static String acceptInviteCommand(String senderName) {
        return buildCommandString(
                MOD_ID,
                TEAM,
                ACCEPT,
                INVITE,
                senderName
        );
    }

    public static String declineInviteCommand(String senderName) {
        return buildCommandString(
                MOD_ID,
                TEAM,
                DECLINE,
                INVITE,
                senderName
        );
    }

    public static String acceptRequestCommand(String name) {
        return buildCommandString(
                MOD_ID,
                TEAM,
                ACCEPT,
                REQUEST,
                name
        );
    }

    public static String declineRequestCommand(String name) {
        return buildCommandString(
                MOD_ID,
                TEAM,
                DECLINE,
                REQUEST,
                name
        );
    }

    public static String requestPlayerCommand(String name) {
        return buildCommandString(
                MOD_ID,
                TEAM,
                REQUEST,
                name
        );
    }

    public static String requestTeamCommand(int teamId) {
        return buildCommandString(
                MOD_ID,
                TEAM,
                JOIN,
                Integer.toString(teamId)
        );
    }
}