package xiao.battleroyale.api.event.game.finish;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.event.EventDispatcher;

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

    @Override public String getTextName() {
        return "CBR GameCompleteEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameCompleteEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
