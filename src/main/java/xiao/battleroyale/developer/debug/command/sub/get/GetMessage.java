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
import xiao.battleroyale.common.message.game.SpectateMessage;
import xiao.battleroyale.developer.debug.DebugManager;
import xiao.battleroyale.developer.debug.DebugMessage;
import xiao.battleroyale.developer.debug.LocalDebugManager;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildDebugCommandString;
import static xiao.battleroyale.developer.debug.command.sub.GetCommand.buildLocalDebugCommandString;

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
                        .executes(context -> getZoneMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
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
                        .executes(context -> getTeamMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
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
                        .executes(context -> getGameMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getGameMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamemessage [id]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGE : GAME_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::getGameMessage)));

        // 获取观战消息
        // get spectatemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? SPECTATE_MESSAGES : SPECTATE_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> getSpectateMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> getSpectateMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get spectatemessage [id]
        getCommand.then(Commands.literal(useFullName ? SPECTATE_MESSAGE : SPECTATE_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::getSpectateMessage)));
    }

    public static void addClient(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {
        // 显示消息
        getCommand.then(Commands.literal(useFullName ? MESSAGES : MESSAGES_SHORT)
                .executes(GetMessage::localGetMessages));

        // 显示区域消息
        // get zonemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? ZONE_MESSAGES : ZONE_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> localGetZoneMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
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
                        .executes(context -> localGetTeamMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
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
                        .executes(context -> localGetGameMessages(context, Integer.MIN_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> localGetGameMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get gamemessage [id]
        getCommand.then(Commands.literal(useFullName ? GAME_MESSAGE : GAME_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::localGetGameMessage)));

        // 显示观战消息
        // get spectatemessages [min max / all]
        getCommand.then(Commands.literal(useFullName ? SPECTATE_MESSAGES : SPECTATE_MESSAGES_SHORT)
                .then(Commands.literal(ALL)
                        .executes(context -> localGetSpectateMessages(context, Integer.MAX_VALUE, Integer.MAX_VALUE - 1)))
                .then(Commands.argument(ID_MIN, IntegerArgumentType.integer())
                        .then(Commands.argument(ID_MAX, IntegerArgumentType.integer())
                                .executes(context -> localGetSpectateMessages(context,
                                        IntegerArgumentType.getInteger(context, ID_MIN),
                                        IntegerArgumentType.getInteger(context, ID_MAX))))));
        // get spectatemessage [id]
        getCommand.then(Commands.literal(useFullName ? SPECTATE_MESSAGE : SPECTATE_MESSAGE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GetMessage::localGetSpectateMessage)));
    }

    /**
     * 获取全部消息状态
     */
    private static int getMessages(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getMessages(source);
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetMessages(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getMessagesLocal(source);
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取区域消息
     */
    private static int getZoneMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getZoneMessages(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getZoneMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getZoneMessage(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int getZoneMessageByName(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getZoneMessage(source, StringArgumentType.getString(context, NAME));
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetZoneMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getZoneMessagesLocal(source, min, max);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetZoneMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getZoneMessageLocal(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetZoneMessageByName(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getZoneMessageLocal(source, StringArgumentType.getString(context, NAME));
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取队伍消息
     */
    private static int getTeamMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getTeamMessages(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getTeamMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getTeamMessage(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetTeamMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getTeamMessagesLocal(source, min, max);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetTeamMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getTeamMessageLocal(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取游戏消息
     */
    private static int getGameMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getGameMessages(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getGameMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getGameMessage(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetGameMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getGameMessagesLocal(source, min, max);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetGameMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getGameMessageLocal(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 获取观战消息
     */
    private static int getSpectateMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getSpectateMessages(source, min, max);
        return Command.SINGLE_SUCCESS;
    }
    private static int getSpectateMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugMessage.get().getSpectateMessage(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetSpectateMessages(CommandContext<CommandSourceStack> context, int min, int max) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getSpectateMessagesLocal(source, min, max);
        }
        return Command.SINGLE_SUCCESS;
    }
    private static int localGetSpectateMessage(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugMessage.get().getSpectateMessageLocal(source, IntegerArgumentType.getInteger(context, SINGLE_ID));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static String getZoneMessagesCommand(int min, int max) {
        return buildDebugCommandString(
                GET,
                ZONE_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getZoneMessageCommand(int zoneId) {
        return buildDebugCommandString(
                GET,
                ZONE_MESSAGE,
                Integer.toString(zoneId)
        );
    }
    public static String getTeamMessagesCommand(int min, int max) {
        return buildDebugCommandString(
                GET,
                TEAM_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getTeamMessageCommand(int teamId) {
        return buildDebugCommandString(
                GET,
                TEAM_MESSAGE,
                Integer.toString(teamId)
        );
    }
    public static String getGameMessagesCommand(int min, int max) {
        return buildDebugCommandString(
                GET,
                GAME_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getGameMessageCommand(int channel) {
        return buildDebugCommandString(
                GET,
                GAME_MESSAGE,
                Integer.toString(channel)
        );
    }
    public static String getSpectateMessagesCommand(int min, int max) {
        return buildDebugCommandString(
                GET,
                SPECTATE_MESSAGE,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getSpectateMessageCommand(int singleId) {
        return buildDebugCommandString(
                GET,
                SPECTATE_MESSAGE,
                Integer.toString(singleId)
        );
    }

    public static String getLocalZoneMessagesCommand(int min, int max) {
        return buildLocalDebugCommandString(
                GET,
                ZONE_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getLocalZoneMessageCommand(int zoneId) {
        return buildLocalDebugCommandString(
                GET,
                ZONE_MESSAGE,
                Integer.toString(zoneId)
        );
    }
    public static String getLocalTeamMessagesCommand(int min, int max) { // 队友singleId范围
        return buildLocalDebugCommandString(
                GET,
                TEAM_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getLocalTeamMessageCommand(int singleId) { // 队友singleId
        return buildLocalDebugCommandString(
                GET,
                TEAM_MESSAGE,
                Integer.toString(singleId)
        );
    }
    public static String getLocalGameMessagesCommand(int min, int max) {
        return buildLocalDebugCommandString(
                GET,
                GAME_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getLocalGameMessageCommand(int channel) {
        return buildLocalDebugCommandString(
                GET,
                GAME_MESSAGE,
                Integer.toString(channel)
        );
    }
    public static String getLocalSpectateMessagesCommand(int min, int max) {
        return buildLocalDebugCommandString(
                GET,
                SPECTATE_MESSAGES,
                Integer.toString(min),
                Integer.toString(max)
        );
    }
    public static String getLocalSpectateMessageCommand(int channel) {
        return buildLocalDebugCommandString(
                GET,
                SPECTATE_MESSAGE,
                Integer.toString(channel)
        );
    }
}