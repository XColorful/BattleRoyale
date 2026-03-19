package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

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
}
