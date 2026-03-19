package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

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
}
