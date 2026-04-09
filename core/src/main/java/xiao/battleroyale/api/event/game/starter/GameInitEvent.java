package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class GameInitEvent extends AbstractGameEvent {

    public GameInitEvent(IGameManager gameManager) {
        super(gameManager);
    }

    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_INIT_EVENT;
    }

    @Override public String getTextName() {
        return "CBR GameInitEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameInitEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
