package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.Set;

public class GameCompleteFinishData extends AbstractGameStatsEventData {

    public final boolean hasWinner;
    public final Set<GamePlayer> winnerGamePlayers;
    public final Set<GameTeam> winnerGameTeams;

    public GameCompleteFinishData(IGameManager gameManager, boolean hasWinner,
                                  Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams) {
        super(gameManager);
        this.hasWinner = hasWinner;
        this.winnerGamePlayers = winnerGamePlayers;
        this.winnerGameTeams = winnerGameTeams;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_COMPLETE_FINISH_EVENT;
    }
}
