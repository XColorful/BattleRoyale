package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.GameType;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.gamerule.IGameruleManager;

import static xiao.battleroyale.command.CommandArg.GET_GAME_MODE;
import static xiao.battleroyale.command.CommandArg.GAMERULE_MANAGER;

public class GameruleManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(GAMERULE_MANAGER)
                // IGameruleManager
                .then(Commands.literal(GET_GAME_MODE).executes(GameruleManagerCommand::getGameMode));
    }

    // --------IGameruleManager--------

    private static int getGameMode(CommandContext<CommandSourceStack> context) {
        IGameruleManager gameruleManager = BattleRoyale.getGameManager().getGameruleManager();
        GameType gameType = gameruleManager.getGameMode();
        return gameType.getId();
    }
}