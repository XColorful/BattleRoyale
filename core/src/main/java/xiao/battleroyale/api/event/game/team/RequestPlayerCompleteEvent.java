package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class RequestPlayerCompleteEvent extends AbstractSenderEvent {

    protected final boolean accept;

    public RequestPlayerCompleteEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.REQUEST_PLAYER_COMPLETE_EVENT;
    }

    public boolean isAccept() {
        return accept;
    }
}
