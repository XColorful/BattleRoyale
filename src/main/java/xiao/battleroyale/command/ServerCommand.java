package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.*;

import static xiao.battleroyale.command.CommandArg.MOD_ID;

public class ServerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(MOD_ID);
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
        root.then(MutekiCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(ParticleCommand.get()
                .requires(source -> source.hasPermission(2)));
        root.then(BoostCommand.get()
                .requires(source -> source.hasPermission(2)));

        dispatcher.register(root);
    }
}