package xiao.battleroyale.compat.forge.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.team.InvitePlayerCompleteData;
import xiao.battleroyale.api.game.IGameManager;

public class InvitePlayerCompleteEvent extends AbstractSenderEvent {

    protected final boolean accept;

    public InvitePlayerCompleteEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }

    public boolean isAccept() {
        return accept;
    }

    public static InvitePlayerCompleteEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof InvitePlayerCompleteData data)) {
            throw new RuntimeException("Expected InvitePlayerCompleteData but received: " + customEventData.getClass().getName());
        }
        return new InvitePlayerCompleteEvent(data.gameManager, data.sender, data.targetPlayer, data.accept);
    }
}
