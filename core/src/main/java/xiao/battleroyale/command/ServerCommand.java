package xiao.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.command.sub.*;

import static xiao.battleroyale.command.CommandArg.MOD_ID;
import static xiao.battleroyale.command.CommandArg.MOD_NAME_SHORT;

public class ServerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(get(MOD_ID));
        dispatcher.register(get(MOD_NAME_SHORT));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> get(String rootName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        root.then(LootCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(ReloadCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(ConfigCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(SaveCommand.get()
                .requires(CommandLevel.hasPermission(3)));
        root.then(BackupCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(TeamCommand.get()
        ); // 个别指令需要权限
        root.then(GameCommand.get()
        ); // 部分指令不需要权限
        root.then(RegisterCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(TriggerCommand.get()
        ); // 不需要权限
        root.then(FireworkCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(MutekiCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(ParticleCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(BoostCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(ExampleCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(UtilityCommand.get()
        ); // 部分指令不需要权限
        root.then(TempCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        root.then(ApiCommand.get()
                .requires(CommandLevel.hasPermission(2)));
        return root;
    }
}