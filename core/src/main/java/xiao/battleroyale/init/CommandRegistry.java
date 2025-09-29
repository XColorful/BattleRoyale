package xiao.battleroyale.init;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import xiao.battleroyale.api.init.ICommandRegistry;
import xiao.battleroyale.command.ClientCommand;
import xiao.battleroyale.command.ServerCommand;
import xiao.battleroyale.compat.pubgmc.PubgmcCommand;
import xiao.battleroyale.developer.debug.command.DebugCommand;
import xiao.battleroyale.developer.debug.command.LocalDebugCommand;
import xiao.battleroyale.developer.gm.command.GameMasterCommand;

public class CommandRegistry implements ICommandRegistry {

    private static final CommandRegistry INSTANCE = new CommandRegistry();

    public static CommandRegistry get() {
        return INSTANCE;
    }

    private CommandRegistry() {}

    @Override
    public void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        ServerCommand.register(dispatcher);
        DebugCommand.register(dispatcher);
        GameMasterCommand.register(dispatcher);
        PubgmcCommand.register(dispatcher);
    }

    @Override
    public void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        ClientCommand.register(dispatcher);
        LocalDebugCommand.register(dispatcher);
    }
}
