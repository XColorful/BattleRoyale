package xiao.battleroyale.developer.debug.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.developer.debug.LocalDebugManager;
import xiao.battleroyale.developer.debug.command.sub.GetCommand;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class LocalDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LocalDebugManager.init();
        dispatcher.register(get(DEBUG_MOD, true));
        dispatcher.register(get(DEBUG_MOD_SHORT, false));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> get(String rootName, boolean useFullName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        LiteralArgumentBuilder<CommandSourceStack> debugCommand = Commands.literal(useFullName ? DEBUG_LOCAL : DEBUG_LOCAL_SHORT);
        debugCommand.then(GetCommand.getClient(useFullName));
        root.then(debugCommand);
        return root;
    }
}
