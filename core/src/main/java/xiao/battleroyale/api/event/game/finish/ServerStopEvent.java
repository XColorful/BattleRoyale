package xiao.battleroyale.api.event.game.finish;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class ServerStopEvent extends AbstractGameStatsEvent {

    public ServerStopEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.SERVER_STOP_EVENT;
    }

    @Override public String getTextName() {
        return "CBR ServerStopEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
