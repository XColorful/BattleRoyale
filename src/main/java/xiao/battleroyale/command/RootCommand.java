package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.*;

import static xiao.battleroyale.command.CommandArg.ROOT;

public class RootCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT);
        root.then(LootCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(ReloadCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(ConfigCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(TeamCommand.get()
                ); // 队伍管理不需要权限
        root.then(GameCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(FireworkCommand.get()
                .requires(source -> source.hasPermission(2)));

        dispatcher.register(root);
    }
}