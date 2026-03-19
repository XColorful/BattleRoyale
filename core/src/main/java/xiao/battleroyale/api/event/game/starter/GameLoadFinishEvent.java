package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLoadFinishEvent extends AbstractGameStatsEvent {

    public GameLoadFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOAD_FINISH_EVENT;
    }

    @Override public String getTextName() {
        return "CBR GameLoadFinishEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
