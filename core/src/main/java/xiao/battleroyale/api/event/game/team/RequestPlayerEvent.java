package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class RequestPlayerEvent extends AbstractSenderEvent {

    protected @NotNull final GamePlayer targetGamePlayer;

    public RequestPlayerEvent(IGameManager gameManager, ServerPlayer sender, @NotNull GamePlayer targetGamePlayer, ServerPlayer targetPlayer) {
        super(gameManager, sender, targetPlayer);
        this.targetGamePlayer = targetGamePlayer;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.REQUEST_PLAYER_EVENT;
    }
}
