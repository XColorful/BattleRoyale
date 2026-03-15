package xiao.battleroyale.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.command.sub.api.*;

import static xiao.battleroyale.command.CommandArg.*;

public class ApiCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> apiCommand = Commands.literal(API);

        // GameManager
        apiCommand.then(GameManagerCommand.get());
        apiCommand.then(GameruleManagerCommand.get());
        apiCommand.then(GameLobbyManagerCommand.get());
        apiCommand.then(GameLootManagerCommand.get());
        apiCommand.then(GameProcessManagerCommand.get());
        apiCommand.then(TeamManagerCommand.get());

        return apiCommand;
    }
}
