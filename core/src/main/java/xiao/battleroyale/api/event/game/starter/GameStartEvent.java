package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class GameStartEvent extends AbstractGameEvent {

    public GameStartEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_START_EVENT;
    }

    @Override public String getTextName() {
        return "CBR GameStartEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameStartEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
