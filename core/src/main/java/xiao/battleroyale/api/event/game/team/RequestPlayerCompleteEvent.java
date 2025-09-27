package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.game.IGameManager;

public class RequestPlayerCompleteEvent extends AbstractSenderEvent {

    private final boolean accept;

    public RequestPlayerCompleteEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }

    public boolean isAccept() {
        return accept;
    }
}
