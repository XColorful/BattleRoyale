package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootEvent extends AbstractGameTickEvent {

    public GameLootEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_EVENT;
    }

    @Override public String getTextName() {
        return "GameLootEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
