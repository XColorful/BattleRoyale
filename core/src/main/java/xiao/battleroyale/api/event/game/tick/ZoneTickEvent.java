package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class ZoneTickEvent extends AbstractGameTickEvent {

    public ZoneTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ZONE_TICK_EVENT;
    }

    @Override public String getTextName() {
        return "ZoneTickEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
