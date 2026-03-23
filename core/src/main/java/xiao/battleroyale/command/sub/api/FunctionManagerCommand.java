package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.server.function.IFunctionRegisterApi;

import static xiao.battleroyale.command.CommandArg.*;

public class FunctionManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(FUNCTION_MANAGER)
                // IFunctionManager
                .then(Commands.literal(CLEAR_CONFIG_FUNCTION).executes(FunctionManagerCommand::clearConfigFunction))
                .then(Commands.literal(CLEAR_API_FUNCTION).executes(FunctionManagerCommand::clearApiFunction))
                // IFunctionRegisterApi
                .then(Commands.literal(REGISTER_EVENT)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(IS_TAG, BoolArgumentType.bool())
                                        .then(Commands.literal(EVENT_TYPE)
                                                .then(Commands.argument(EVENT_NAME, StringArgumentType.string())
                                                        .suggests(EventType.EVENT_TYPE_SUGGESTS)
                                                        .then(Commands.argument(EVENT_PRIORITY, StringArgumentType.string())
                                                                .suggests(EventPriority.EVENT_PRIORITY_SUGGESTS)
                                                                .then(Commands.argument(RECEIVE_CANCELED, BoolArgumentType.bool())
                                                                        .executes(FunctionManagerCommand::registerFunctionToEvent)
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal(CUSTOM_EVENT_TYPE)
                                                .then(Commands.argument(EVENT_NAME, StringArgumentType.string())
                                                        .suggests(CustomEventType.CUSTOM_EVENT_TYPE_SUGGESTS)
                                                        .then(Commands.argument(EVENT_PRIORITY, StringArgumentType.string())
                                                                .suggests(EventPriority.EVENT_PRIORITY_SUGGESTS)
                                                                .then(Commands.argument(RECEIVE_CANCELED, BoolArgumentType.bool())
                                                                        .executes(FunctionManagerCommand::registerFunctionToCustomEvent)
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.literal(EVENT_CLASS)
                                                .then(Commands.argument(EVENT_NAME, StringArgumentType.string())
                                                        .then(Commands.argument(EVENT_PRIORITY, StringArgumentType.string())
                                                                .suggests(EventPriority.EVENT_PRIORITY_SUGGESTS)
                                                                .then(Commands.argument(RECEIVE_CANCELED, BoolArgumentType.bool())
                                                                        .executes(FunctionManagerCommand::registerFunctionToEventClass)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal(UNREGISTER_EVENT)
                        .then(Commands.argument(RESOURCE_LOCATION, IdentifierArgument.id())
                                .then(Commands.argument(IS_TAG, BoolArgumentType.bool())
                                        .then(Commands.literal(EVENT_TYPE)
                                                .then(Commands.argument(EVENT_NAME, StringArgumentType.string())
                                                        .suggests(EventType.EVENT_TYPE_SUGGESTS)
                                                        .executes(FunctionManagerCommand::unregisterFunctionToEvent)
                                                )
                                        )
                                        .then(Commands.literal(CUSTOM_EVENT_TYPE)
                                                .then(Commands.argument(EVENT_NAME, StringArgumentType.string())
                                                        .suggests(CustomEventType.CUSTOM_EVENT_TYPE_SUGGESTS)
                                                        .executes(FunctionManagerCommand::unregisterFunctionToCustomEvent)
                                                )
                                        )
                                        .then(Commands.literal(EVENT_CLASS)
                                                .then(Commands.argument(EVENT_NAME, StringArgumentType.string())
                                                        .executes(FunctionManagerCommand::unregisterFunctionToEventClass)
                                                )
                                        )
                                )
                        )
                );
    }

    // --------IFunctionManager--------

    private static int clearConfigFunction(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getServerManager().getFunctionManager().clearConfigFunction();
        return Command.SINGLE_SUCCESS;
    }
    private static int clearApiFunction(CommandContext<CommandSourceStack> context) {
        BattleRoyale.getServerManager().getFunctionManager().clearApiFunction();
        return Command.SINGLE_SUCCESS;
    }

    // --------IFunctionRegisterApi--------

    private static int registerFunctionToEvent(CommandContext<CommandSourceStack> context) {
        IFunctionRegisterApi functionManager = BattleRoyale.getServerManager().getFunctionManager();
        return functionManager.registerFunctionToEvent(IdentifierArgument.getId(context, RESOURCE_LOCATION),
                BoolArgumentType.getBool(context, IS_TAG),
                StringArgumentType.getString(context, EVENT_NAME),
                StringArgumentType.getString(context, EVENT_PRIORITY),
                BoolArgumentType.getBool(context, RECEIVE_CANCELED)
        ) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int registerFunctionToCustomEvent(CommandContext<CommandSourceStack> context) {
        IFunctionRegisterApi functionManager = BattleRoyale.getServerManager().getFunctionManager();
        return functionManager.registerFunctionToCustomEvent(IdentifierArgument.getId(context, RESOURCE_LOCATION),
                BoolArgumentType.getBool(context, IS_TAG),
                StringArgumentType.getString(context, EVENT_NAME),
                StringArgumentType.getString(context, EVENT_PRIORITY),
                BoolArgumentType.getBool(context, RECEIVE_CANCELED)
        ) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int registerFunctionToEventClass(CommandContext<CommandSourceStack> context) {
        IFunctionRegisterApi functionManager = BattleRoyale.getServerManager().getFunctionManager();
        return functionManager.registerFunctionToEventClass(IdentifierArgument.getId(context, RESOURCE_LOCATION),
                BoolArgumentType.getBool(context, IS_TAG),
                StringArgumentType.getString(context, EVENT_NAME),
                StringArgumentType.getString(context, EVENT_PRIORITY),
                BoolArgumentType.getBool(context, RECEIVE_CANCELED)
        ) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int unregisterFunctionToEvent(CommandContext<CommandSourceStack> context) {
        IFunctionRegisterApi functionManager = BattleRoyale.getServerManager().getFunctionManager();
        return functionManager.unregisterFunctionToEvent(IdentifierArgument.getId(context, RESOURCE_LOCATION),
                BoolArgumentType.getBool(context, IS_TAG),
                StringArgumentType.getString(context, EVENT_NAME)
        ) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int unregisterFunctionToCustomEvent(CommandContext<CommandSourceStack> context) {
        IFunctionRegisterApi functionManager = BattleRoyale.getServerManager().getFunctionManager();
        return functionManager.unregisterFunctionToCustomEvent(IdentifierArgument.getId(context, RESOURCE_LOCATION),
                BoolArgumentType.getBool(context, IS_TAG),
                StringArgumentType.getString(context, EVENT_NAME)
        ) ? Command.SINGLE_SUCCESS : 0;
    }
    private static int unregisterFunctionToEventClass(CommandContext<CommandSourceStack> context) {
        IFunctionRegisterApi functionManager = BattleRoyale.getServerManager().getFunctionManager();
        return functionManager.unregisterFunctionToEventClass(IdentifierArgument.getId(context, RESOURCE_LOCATION),
                BoolArgumentType.getBool(context, IS_TAG),
                StringArgumentType.getString(context, EVENT_NAME)
        ) ? Command.SINGLE_SUCCESS : 0;
    }
}
