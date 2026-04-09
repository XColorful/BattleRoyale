package xiao.battleroyale.api.event.game.tick;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

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

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(ZoneTickEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
