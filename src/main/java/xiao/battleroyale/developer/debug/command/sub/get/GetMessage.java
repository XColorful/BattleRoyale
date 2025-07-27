package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildDebugCommandString;

public class GetMessage {

    public static void addMessage(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // 获取所有类别消息状态
        // get messages
        getCommand.then(Commands.literal(useFullName ? MESSAGES : MESSAGES_SHORT)
                .executes(GetMessage::executeGetMessages));

        // 获取区域消息
        // get zonemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGES : ZONE_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetZoneMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetZoneMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get zonemessage [id / name]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGE : ZONE_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::executeGetZoneMessage))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetMessage::executeGetZoneMessageByName)));

        // 获取队伍消息
        // get teammessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? TEAM_MESSAGES : TEAM_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetTeamMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetTeamMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get teammessage [id]
        getCommand.then(Commands.literal(useFullName ? TEAM_MESSAGE : TEAM_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::executeGetTeamMessage)));

        // 获取游戏消息
        // get gamemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGES : GAME_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> executeGetGameMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> executeGetGameMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamemessage [id]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGE : GAME_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::executeGetGameMessage)));
    }

    /**
     * 获取全部消息状态
     */
    private static int executeGetMessages(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get messages"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取区域消息
     */
    private static int executeGetZoneMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessages (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessages with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetZoneMessage(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessage by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetZoneMessageByName(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessage by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取队伍消息
     */
    private static int executeGetTeamMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get teammessages (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get teammessages with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetTeamMessage(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get teammessage by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取游戏消息
     */
    private static int executeGetGameMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gamemessages (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gamemessages with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int executeGetGameMessage(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gamemessage by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    public static String getZoneMessageCommandString(int zoneId) {
        return buildDebugCommandString(
                GET,
                ZONE_MESSAGE,
                Integer.toString(zoneId)
        );
    }
    public static String getTeamMessageCommandString(int teamId) {
        return buildDebugCommandString(
                GET,
                TEAM_MESSAGE,
                Integer.toString(teamId)
        );
    }
    public static String getGameMessageCommandString(int id) {
        return buildDebugCommandString(
                GET,
                GAME_MESSAGE,
                Integer.toString(id)
        );
    }
}