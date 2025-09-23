package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.game.IGameManager;

public class InvitePlayerCompleteEvent extends AbstractSenderEvent {

    private final boolean accept;

    public InvitePlayerCompleteEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }

    public boolean isAccept() {
        return accept;
    }
}
