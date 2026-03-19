package xiao.battleroyale.api.event.game.starter;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameInitFinishEvent extends AbstractGameStatsEvent {

    public GameInitFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_INIT_FINISH_EVENT;
    }

    @Override public String getTextName() {
        return "CBR GameInitFinishEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
