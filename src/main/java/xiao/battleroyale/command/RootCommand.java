package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.ConfigCommand;
import xiao.battleroyale.command.sub.LootCommand;
import xiao.battleroyale.command.sub.ReloadCommand;

public class RootCommand {
    private static final String ROOT_NAME = "battleroyale";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT_NAME)
                .requires((source -> source.hasPermission(2)));
        root.then(LootCommand.get());
        root.then(ReloadCommand.get());
        root.then(ConfigCommand.get());
        dispatcher.register(root);
    }
}
