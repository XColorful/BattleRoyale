package xiao.battleroyale.api.event.game.finish;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class ServerStopFinishEvent extends AbstractGameStatsEvent {

    public ServerStopFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.SERVER_STOP_FINISH_EVENT;
    }

    @Override public String getTextName() {
        return "CBR ServerStopFinishEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(ServerStopFinishEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
