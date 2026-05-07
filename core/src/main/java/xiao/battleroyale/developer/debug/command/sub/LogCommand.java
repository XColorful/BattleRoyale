package xiao.battleroyale.developer.debug.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.developer.debug.DebugLog;
import xiao.battleroyale.developer.debug.DebugManager;
import xiao.battleroyale.developer.debug.LocalDebugManager;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class LogCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> getServer(boolean useFullName) {
        return Commands.literal(LOG)
                .then(Commands.literal(useFullName ? LOG_ERROR : LOG_ERROR_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::logError)
                        )
                )
                .then(Commands.literal(useFullName ? LOG_WARN : LOG_WARN_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::logWarn)
                        )
                )
                .then(Commands.literal(useFullName ? LOG_INFO : LOG_INFO_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::logInfo)
                        )
                )
                .then(Commands.literal(useFullName ? LOG_DEBUG : LOG_DEBUG_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::logDebug)
                        )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient(boolean useFullName) {
        return Commands.literal(LOG)
                .then(Commands.literal(useFullName ? LOG_ERROR : LOG_ERROR_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::localLogError)
                        )
                )
                .then(Commands.literal(useFullName ? LOG_WARN : LOG_WARN_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::localLogWarn)
                        )
                )
                .then(Commands.literal(useFullName ? LOG_INFO : LOG_INFO_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::localLogInfo)
                        )
                )
                .then(Commands.literal(useFullName ? LOG_DEBUG : LOG_DEBUG_SHORT)
                        .then(Commands.argument(MSG, StringArgumentType.string())
                                .executes(LogCommand::localLogDebug)
                        )
                );
    }

    private static int logError(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugLog.get().logError(StringArgumentType.getString(context, MSG));
        return Command.SINGLE_SUCCESS;
    }
    private static int localLogError(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugLog.get().logErrorLocal(StringArgumentType.getString(context, MSG));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int logWarn(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugLog.get().logWarn(StringArgumentType.getString(context, MSG));
        return Command.SINGLE_SUCCESS;
    }
    private static int localLogWarn(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugLog.get().logWarnLocal(StringArgumentType.getString(context, MSG));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int logInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugLog.get().logInfo(StringArgumentType.getString(context, MSG));
        return Command.SINGLE_SUCCESS;
    }
    private static int localLogInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugLog.get().logInfoLocal(StringArgumentType.getString(context, MSG));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int logDebug(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugLog.get().logDebug(StringArgumentType.getString(context, MSG));
        return Command.SINGLE_SUCCESS;
    }
    private static int localLogDebug(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!LocalDebugManager.enableLocalDebug(source)) {
            source.sendFailure(Component.translatable("battleroyale.message.local_debug_not_enabled"));
            return 0;
        }

        if (Minecraft.getInstance().player != null) {
            DebugLog.get().logDebugLocal(StringArgumentType.getString(context, MSG));
        }
        return Command.SINGLE_SUCCESS;
    }
}
