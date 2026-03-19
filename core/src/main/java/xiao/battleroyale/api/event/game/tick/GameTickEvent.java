package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameTickEvent extends AbstractGameTickEvent {

    public GameTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_TICK_EVENT;
    }

    @Override public String getTextName() {
        return "GameTickEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
