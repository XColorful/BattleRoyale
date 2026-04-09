package xiao.battleroyale.api.event.game.zone;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.event.EventDispatcher;

public class ZoneCompleteEvent extends AbstractZoneEvent {

    public ZoneCompleteEvent(IGameManager gameManager, IGameZone gameZone) {
        super(gameManager, gameZone);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ZONE_COMPLETE_EVENT;
    }

    @Override public String getTextName() {
        return String.format("CBR Zone %s Complete Event", gameZone.getZoneId());
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(ZoneCompleteEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
