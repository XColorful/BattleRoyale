package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.developer.debug.DebugManager;
import xiao.battleroyale.developer.debug.LocalDebugManager;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildDebugCommandString;

public class GetMessage {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // 获取所有类别消息状态
        // get messages
        getCommand.then(Commands.literal(useFullName ? MESSAGES : MESSAGES_SHORT)
                .executes(GetMessage::getMessages));

        // 获取区域消息
        // get zonemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGES : ZONE_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getZoneMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getZoneMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get zonemessage [id / name]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGE : ZONE_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::getZoneMessage))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetMessage::getZoneMessageByName)));

        // 获取队伍消息
        // get teammessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? TEAM_MESSAGES : TEAM_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getTeamMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getTeamMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get teammessage [id]
        getCommand.then(Commands.literal(useFullName ? TEAM_MESSAGE : TEAM_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::getTeamMessage)));

        // 获取游戏消息
        // get gamemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGES : GAME_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getGameMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getGameMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamemessage [id]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGE : GAME_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::getGameMessage)));
    }

    public static void addClient(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {
        // 显示消息
        getCommand.then(Commands.literal(useFullName ? MESSAGES : MESSAGES_SHORT)
                .executes(GetMessage::localGetMessages));

        // 显示区域消息
        // get zonemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGES : ZONE_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> localGetZoneMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> localGetZoneMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get zonemessage [id / name]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGE : ZONE_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::localGetZoneMessage))
                .then(Commands.argument(NAME, StringArgumentType.string())
                        .executes(GetMessage::localGetZoneMessageByName)));

        // 显示队伍消息
        // get teammessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? TEAM_MESSAGES : TEAM_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> localGetTeamMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> localGetTeamMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get teammessage [id]
        getCommand.then(Commands.literal(useFullName ? TEAM_MESSAGE : TEAM_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::localGetTeamMessage)));

        // 显示游戏消息
        // get gamemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGES : GAME_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> localGetGameMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> localGetGameMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamemessage [id]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGE : GAME_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::localGetGameMessage)));
    }

    /**
     * 获取全部消息状态
     */
    private static int getMessages(CommandContext<CommandSourceStack> context) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Executing get messages"), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetMessages(CommandContext<CommandSourceStack> context) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) { // 仅在玩家存在时发送
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get messages"));
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取区域消息
     */
    private static int getZoneMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessages (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessages with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int getZoneMessage(CommandContext<CommandSourceStack> context) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessage by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int getZoneMessageByName(CommandContext<CommandSourceStack> context) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        final String name = StringArgumentType.getString(context, NAME);
        context.getSource().sendSuccess(() -> Component.literal("Executing get zonemessage by Name: " + name), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetZoneMessages(CommandContext<CommandSourceStack> context, int min, int max) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get zonemessages (all)"));
            } else {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get zonemessages with min: " + min + ", max: " + max));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetZoneMessage(CommandContext<CommandSourceStack> context) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get zonemessage by ID: " + id));
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetZoneMessageByName(CommandContext<CommandSourceStack> context) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        final String name = StringArgumentType.getString(context, NAME);
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get zonemessage by Name: " + name));
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取队伍消息
     */
    private static int getTeamMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get teammessages (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get teammessages with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int getTeamMessage(CommandContext<CommandSourceStack> context) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get teammessage by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetTeamMessages(CommandContext<CommandSourceStack> context, int min, int max) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get teammessages (all)"));
            } else {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get teammessages with min: " + min + ", max: " + max));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetTeamMessage(CommandContext<CommandSourceStack> context) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get teammessage by ID: " + id));
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取游戏消息
     */
    private static int getGameMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        if (!DebugManager.hasDebugPermission(context.getSource())) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gamemessages (all)"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing get gamemessages with min: " + min + ", max: " + max), false);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int getGameMessage(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing get gamemessage by ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetGameMessages(CommandContext<CommandSourceStack> context, int min, int max) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get gamemessages (all)"));
            } else {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get gamemessages with min: " + min + ", max: " + max));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetGameMessage(CommandContext<CommandSourceStack> context) {CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Executing local get gamemessage by ID: " + id));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static String getZoneMessageCommand(int zoneId) {
        return buildDebugCommandString(
                GET,
                ZONE_MESSAGE,
                Integer.toString(zoneId)
        );
    }
    public static String getTeamMessageCommand(int teamId) {
        return buildDebugCommandString(
                GET,
                TEAM_MESSAGE,
                Integer.toString(teamId)
        );
    }
    public static String getGameMessageCommand(int channel) {
        return buildDebugCommandString(
                GET,
                GAME_MESSAGE,
                Integer.toString(channel)
        );
    }

    public static String getLocalZoneMessageCommand(int zoneId) {
        return buildDebugCommandString(
                GET,
                ZONE_MESSAGE,
                Integer.toString(zoneId)
        );
    }
    public static String getLocalTeamMessageCommand(int teamId) {
        return buildDebugCommandString(
                GET,
                TEAM_MESSAGE,
                Integer.toString(teamId)
        );
    }
    public static String getLocalGameMessageCommand(int channel) {
        return buildDebugCommandString(
                GET,
                GAME_MESSAGE,
                Integer.toString(channel)
        );
    }
}