package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class GameTickFinishEvent extends AbstractGameTickFinishEvent {

    public GameTickFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_TICK_FINISH_EVENT;
    }

    @Override public String getTextName() {
        return "GameTickFinishEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameTickFinishEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
