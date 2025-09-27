package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.ConfigCommand;
import xiao.battleroyale.command.sub.ExampleCommand;
import xiao.battleroyale.command.sub.ReloadCommand;

import static xiao.battleroyale.command.CommandArg.*;

public class ClientCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(get(MOD_ID));
        dispatcher.register(get(MOD_NAME_SHORT));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> get(String rootName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        root.then(ReloadCommand.getClient()
        );
        root.then(ConfigCommand.getClient()
        );
        root.then(ExampleCommand.getClient()
        );
        return root;
    }
}
