package xiao.battleroyale.api.game;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public interface IGameInfoGetter {

    int getGameTime();
    UUID getGameId();
    boolean isInGame();
    Vec3 getGlobalCenterOffset();
    int getMaxGameTime();
    int getWinnerTeamTotal();
    int getRequiredGameTeam();
    ServerLevel getServerLevel();
    ResourceKey<Level> getGameLevelKey();
    Supplier<Float> getRandom();

    boolean hasWinner();
    Set<GamePlayer> getWinnerGamePlayers();
    Set<GameTeam> getWinnerGameTeams();
    int getRemainRestartTime();
}
