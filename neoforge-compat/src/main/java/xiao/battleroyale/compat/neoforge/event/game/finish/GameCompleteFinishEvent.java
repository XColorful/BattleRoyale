package xiao.battleroyale.compat.neoforge.event.game.finish;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameStatsEvent;

import java.util.Set;

public class GameCompleteFinishEvent extends AbstractGameStatsEvent {

    protected final boolean hasWinner;
    protected final Set<GamePlayer> winnerGamePlayers;
    protected final Set<GameTeam> winnerGameTeams;

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

    public static GameCompleteFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameCompleteFinishData data)) {
            throw new RuntimeException("Expected GameCompleteFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameCompleteFinishEvent(data.gameManager, data.hasWinner, data.winnerGamePlayers, data.winnerGameTeams);
    }
}