package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLoadEvent extends AbstractGameEvent {

    public GameLoadEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOAD_EVENT;
    }

    @Override public String getTextName() {
        return "CBR GameLoadEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
