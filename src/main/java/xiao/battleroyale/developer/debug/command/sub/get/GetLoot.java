package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class GetLoot {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // get commonloot
        getCommand.then(Commands.literal(useFullName ? COMMON_LOOT : COMMON_LOOT_SHORT)
                .executes(GetLoot::executeGetCommonLootManager));

        // get gameloot
        getCommand.then(Commands.literal(useFullName ? GAME_LOOT : GAME_LOOT_SHORT)
                .executes(GetLoot::executeGetGameLootManager));
    }

    private static int executeGetCommonLootManager(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get commonloot"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeGetGameLootManager(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing get gameloot"), false);
        return Command.SINGLE_SUCCESS;
    }
}
