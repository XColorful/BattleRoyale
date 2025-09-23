package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.Set;

public class GameCompleteFinishEvent extends AbstractGameStatsEvent {

    private final boolean hasWinner;
    private final Set<GamePlayer> winnerGamePlayers;
    private final Set<GameTeam> winnerGameTeams;

    public GameCompleteFinishEvent(IGameManager gameManager, boolean hasWinner,
                                   Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams) {
        super(gameManager);
        this.hasWinner = hasWinner;
        this.winnerGamePlayers = winnerGamePlayers;
        this.winnerGameTeams = winnerGameTeams;
    }

    public boolean hasWinner() {
        return this.hasWinner;
    }

    public Set<GamePlayer> getWinnerGamePlayers() {
        return this.winnerGamePlayers;
    }

    public Set<GameTeam> getWinnerGameTeams() {
        return this.winnerGameTeams;
    }
}
