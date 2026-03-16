package xiao.battleroyale.command.sub.api;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameMainManager;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static xiao.battleroyale.command.CommandArg.SPAWN_MANAGER;
import static xiao.battleroyale.command.CommandArg.RESPAWN;
import static xiao.battleroyale.command.CommandArg.PLAYER;

public class SpawnManagerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal(SPAWN_MANAGER)
                // ISpawnManager
                .then(Commands.literal(RESPAWN)
                        .then(Commands.argument(PLAYER, EntityArgument.entities())
                                .executes(SpawnManagerCommand::respawn)
                        )
                );
    }

    // --------ISpawnManager--------

    private static int respawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        IGameMainManager gameManager = BattleRoyale.getGameManager();
        IGameTeamReadApi teamManager = gameManager.getTeamManager();
        Collection<? extends Entity> entities = EntityArgument.getEntities(context, PLAYER);
        if (entities.isEmpty()) return 0;
        List<GamePlayer> respawnGamePlayers = new ArrayList<>();
        for (Entity entity : entities) {
            @Nullable GamePlayer gamePlayer = teamManager.getGamePlayerByUUID(entity.getUUID());
            if (gamePlayer != null) {
                respawnGamePlayers.add(gamePlayer);
            }
        }
        if (respawnGamePlayers.isEmpty()) return 0;
        gameManager.getSpawnManager().respawn(respawnGamePlayers);
        return respawnGamePlayers.size();
    }
}
