package xiao.battleroyale.compat.forge.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.team.RequestPlayerCompleteData;
import xiao.battleroyale.api.game.IGameManager;

public class RequestPlayerCompleteEvent extends AbstractSenderEvent {

    protected final boolean accept;

    public RequestPlayerCompleteEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }

    public boolean isAccept() {
        return accept;
    }

    public static RequestPlayerCompleteEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof RequestPlayerCompleteData data)) {
            throw new RuntimeException("Expected RequestPlayerCompleteData but received: " + customEventData.getClass().getName());
        }
        return new RequestPlayerCompleteEvent(data.gameManager, data.sender, data.targetPlayer, data.accept);
    }
}
