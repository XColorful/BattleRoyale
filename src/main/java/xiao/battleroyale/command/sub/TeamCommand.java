package xiao.battleroyale.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
    // 指令字符串常量
    private static final String TEAM_NAME = "team";
    private static final String JOIN_NAME = "join";
    private static final String LEAVE_NAME = "leave";
    private static final String KICK_NAME = "kick";
    private static final String INVITE_NAME = "invite";
    private static final String ACCEPT_NAME = "accept";
    private static final String DECLINE_NAME = "decline";
    private static final String PLAYER_ARG_NAME = "player";
    private static final String TEAM_ID_ARG_NAME = "teamId";

    /**
     * 获取队伍相关命令的字面量参数构建器。
     * 包含加入、离开、踢人、邀请、接受和拒绝邀请等子命令。
     * @return 字面量参数构建器。
     */
    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(TEAM_NAME)
                // join 和 leave 命令对所有玩家开放，但必须由玩家执行
                .then(Commands.literal(JOIN_NAME)
                        .requires(CommandSourceStack::isPlayer) // 必须是玩家才能执行加入操作
                        .executes(TeamCommand::joinTeam) // /battleroyale team join
                        .then(Commands.argument(TEAM_ID_ARG_NAME, IntegerArgumentType.integer(1))
                                .executes(TeamCommand::joinTeamSpecific) // /battleroyale team join <teamId>
                        )
                )
                .then(Commands.literal(LEAVE_NAME)
                        .requires(CommandSourceStack::isPlayer) // 必须是玩家才能执行离开操作
                        .executes(TeamCommand::leaveTeam) // /battleroyale team leave
                )
                // kick 和 invite 命令只允许队伍队长执行，且必须由玩家执行
                .then(Commands.literal(KICK_NAME)
                        .requires(source -> {
                            // 必须是玩家才能执行踢人操作
                            if (!(source.getEntity() instanceof ServerPlayer player)) {
                                return false;
                            }
                            // 检查玩家是否是当前队伍的队长
                            return TeamManager.get().isPlayerLeader(player.getUUID());
                        })
                        .then(Commands.argument(PLAYER_ARG_NAME, EntityArgument.player())
                                .executes(TeamCommand::kickPlayer) // /battleroyale team kick <player>
                        )
                )
                .then(Commands.literal(INVITE_NAME)
                        .requires(source -> {
                            // 必须是玩家才能执行邀请操作
                            if (!(source.getEntity() instanceof ServerPlayer player)) {
                                return false;
                            }
                            // 检查玩家是否是当前队伍的队长
                            return TeamManager.get().isPlayerLeader(player.getUUID());
                        })
                        .then(Commands.argument(PLAYER_ARG_NAME, EntityArgument.player())
                                .executes(TeamCommand::invitePlayer) // /battleroyale team invite <player>
                        )
                )
                // accept 和 decline 命令对所有玩家开放，但必须由玩家执行
                .then(Commands.literal(ACCEPT_NAME)
                        .requires(CommandSourceStack::isPlayer) // 必须是玩家才能执行接受邀请操作
                        .then(Commands.argument(TEAM_ID_ARG_NAME, IntegerArgumentType.integer(1))
                                .executes(TeamCommand::acceptInvite) // /battleroyale team accept <teamId> (用于可点击聊天消息)
                        )
                )
                .then(Commands.literal(DECLINE_NAME)
                        .requires(CommandSourceStack::isPlayer) // 必须是玩家才能执行拒绝邀请操作
                        .then(Commands.argument(TEAM_ID_ARG_NAME, IntegerArgumentType.integer(1))
                                .executes(TeamCommand::declineInvite) // /battleroyale team decline <teamId> (用于可点击聊天消息)
                        )
                );
    }

    /**
     * 处理 `/battleroyale team join` 命令，玩家加入一个自动分配的队伍。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int joinTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        // 在游戏进行中不允许通过此命令加入队伍
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }
        TeamManager.get().joinTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 处理 `/battleroyale team join <teamId>` 命令，玩家加入指定的队伍。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int joinTeamSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID_ARG_NAME);
        // 在游戏进行中不允许通过此命令加入队伍
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }
        TeamManager.get().addPlayerToTeam(player, teamId); // TeamManager 中的此方法已处理了所有检查
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 处理 `/battleroyale team leave` 命令，玩家离开当前队伍。
     * 注意：在游戏进行中离开会被视为淘汰。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int leaveTeam(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        // leaveTeam 方法已处理游戏进行中的特殊逻辑，这里不需要额外的 inGame 检查
        TeamManager.get().leaveTeam(player);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 处理 `/battleroyale team kick <player>` 命令，队长踢出队伍成员。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int kickPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER_ARG_NAME);

        // 在游戏进行中不允许通过此命令踢人
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        TeamManager.get().kickPlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 处理 `/battleroyale team invite <player>` 命令，队长邀请玩家加入队伍。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int invitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, PLAYER_ARG_NAME);

        // 在游戏进行中不允许通过此命令邀请
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(sender, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        TeamManager.get().invitePlayer(sender, targetPlayer);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 处理 `/battleroyale team accept <teamId>` 命令，玩家接受队伍邀请。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int acceptInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID_ARG_NAME);

        // 在游戏进行中不允许通过此命令接受邀请
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        TeamManager.get().acceptInvite(player, teamId);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 处理 `/battleroyale team decline <teamId>` 命令，玩家拒绝队伍邀请。
     * @param context 命令上下文。
     * @return 命令执行结果（1表示成功，0表示失败）。
     * @throws CommandSyntaxException 如果命令语法错误。
     */
    private static int declineInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int teamId = IntegerArgumentType.getInteger(context, TEAM_ID_ARG_NAME);

        // 在游戏进行中不允许通过此命令拒绝邀请
        if (GameManager.get().isInGame()) {
            ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.game_in_progress").withStyle(ChatFormatting.RED));
            return 0;
        }

        TeamManager.get().declineInvite(player, teamId);
        return Command.SINGLE_SUCCESS;
    }
}