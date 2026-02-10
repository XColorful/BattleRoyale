package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.Collections;
import java.util.List;

public class GameCompleteEvent extends AbstractGameEvent {

    protected final boolean hasWinner;
    protected final List<GamePlayer> gamePlayers;

    public GameCompleteEvent(IGameManager gameManager, boolean hasWinner, List<GamePlayer> gamePlayers) {
        super(gameManager);
        this.hasWinner = hasWinner;
        this.gamePlayers = gamePlayers;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_COMPLETE_EVENT;
    }

    public boolean hasWinner() {
        return this.hasWinner;
    }

    public List<GamePlayer> getGamePlayers() {
        return Collections.unmodifiableList(gamePlayers);
    }
}
