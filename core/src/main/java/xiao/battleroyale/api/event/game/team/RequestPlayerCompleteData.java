package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class RequestPlayerCompleteData extends AbstractSenderEventData {

    public final boolean accept;

    public RequestPlayerCompleteData(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.REQUEST_PLAYER_COMPLETE_EVENT;
    }
}
