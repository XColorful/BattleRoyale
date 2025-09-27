package xiao.battleroyale.developer.gm.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class MessageManager {
    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 删除所有消息
        // /battleroyale gamemaster delete messages
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? MESSAGES : MESSAGES_SHORT)
                        .executes(MessageManager::deleteMessages)));
        // 删除区域消息
        // /battleroyale gamemaster delete zonemessage [min max] / [all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? ZONE_MESSAGE : ZONE_MESSAGE_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> deleteZoneMessage(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                        .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                        .executes(context -> deleteZoneMessage(context,
                                                IntegerArgumentType.getInteger(context, ID_MIN),
                                                IntegerArgumentType.getInteger(context, ID_MAX)))))));
        // 删除队伍消息
        // /battleroyale gamemaster delete teammessage [min max] / [all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? TEAM_MESSAGE : TEAM_MESSAGE_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> deleteTeamMessage(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                        .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                        .executes(context -> deleteTeamMessage(context,
                                                IntegerArgumentType.getInteger(context, ID_MIN),
                                                IntegerArgumentType.getInteger(context, ID_MAX)))))));
        // 删除游戏消息
        // /battleroyale gamemaster delete gamemessage [min max] / [all]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_MESSAGE : GAME_MESSAGE_SHORT)
                        .then(Commands.literal(ALL)
                                .executes(context -> deleteGameMessage(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                        .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                                .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                        .executes(context -> deleteGameMessage(context,
                                                IntegerArgumentType.getInteger(context, ID_MIN),
                                                IntegerArgumentType.getInteger(context, ID_MAX)))))));
    }

    /**
     * 删除所有消息
     */
    private static int deleteMessages(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing delete all messages"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 删除区域消息
     */
    private static int deleteZoneMessage(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all zone messages"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete zone messages from " + min + " to " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 删除队伍消息
     */
    private static int deleteTeamMessage(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all team messages"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete team messages from " + min + " to " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 删除游戏消息
     */
    private static int deleteGameMessage(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete all game messages"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing delete game messages from " + min + " to " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}