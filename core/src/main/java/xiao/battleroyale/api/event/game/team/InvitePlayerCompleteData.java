package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class InvitePlayerCompleteData extends AbstractSenderEventData {

    public final boolean accept;

    public InvitePlayerCompleteData(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.INVITE_PLAYER_COMPLETE_EVENT;
    }
}
