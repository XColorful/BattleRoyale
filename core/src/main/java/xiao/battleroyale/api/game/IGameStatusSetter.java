package xiao.battleroyale.api.game;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.UUID;

public interface IGameStatusSetter {

    boolean setGameStep(int step);
    boolean setGlobalCenterOffset(Vec3 offset);
    void setDefaultLevel(String defaultLevelKey);

    @Deprecated
    void setGameTime(int gameTime);
    @ApiStatus.Internal
    void setGameId(UUID gameId);

    boolean clearWinnerGamePlayers();
    boolean clearWinnerGameTeams();
    boolean addWinnerGamePlayer(GamePlayer gamePlayer);
    boolean addWinnerGameTeam(GameTeam gameTeam);
}
