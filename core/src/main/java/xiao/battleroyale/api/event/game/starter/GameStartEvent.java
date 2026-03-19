package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

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
}
