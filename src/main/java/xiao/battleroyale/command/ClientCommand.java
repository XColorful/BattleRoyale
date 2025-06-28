package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.ConfigCommand;
import xiao.battleroyale.command.sub.ReloadCommand;

import static xiao.battleroyale.command.CommandArg.*;

public class ClientCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(MOD_ID);
        root.then(ReloadCommand.getClient()
                );
        root.then(ConfigCommand.getClient()
                );
        dispatcher.register(root);
    }
}
