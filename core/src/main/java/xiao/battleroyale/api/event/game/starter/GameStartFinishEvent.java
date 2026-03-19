package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameStartFinishEvent extends AbstractGameStatsEvent {

    public GameStartFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_START_FINISH_EVENT;
    }

    @Override public String getTextName() {
        return "CBR GameStartFinishEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
