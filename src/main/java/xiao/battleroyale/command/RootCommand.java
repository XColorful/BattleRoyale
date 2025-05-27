package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.ConfigCommand;
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.command.sub.LootCommand;
import xiao.battleroyale.command.sub.ReloadCommand;
import xiao.battleroyale.command.sub.TeamCommand;

public class RootCommand {
    private static final String ROOT_NAME = "battleroyale";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT_NAME);
        root.then(LootCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(ReloadCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(ConfigCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(TeamCommand.get());
        root.then(GameCommand.get()
                .requires(source -> source.hasPermission(2)));

        dispatcher.register(root);
    }
}