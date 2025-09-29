package xiao.battleroyale.compat.forge.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.team.RequestPlayerData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class RequestPlayerEvent extends AbstractSenderEvent {

    protected @NotNull final GamePlayer targetGamePlayer;

    public RequestPlayerEvent(IGameManager gameManager, ServerPlayer sender, @NotNull GamePlayer targetGamePlayer, ServerPlayer targetPlayer) {
        super(gameManager, sender, targetPlayer);
        this.targetGamePlayer = targetGamePlayer;
    }

    public @NotNull GamePlayer getTargetGamePlayer() {
        return this.targetGamePlayer;
    }

    public static RequestPlayerEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof RequestPlayerData data)) {
            throw new RuntimeException("Expected RequestPlayerData but received: " + customEventData.getClass().getName());
        }
        return new RequestPlayerEvent(data.gameManager, data.sender, data.targetGamePlayer, data.targetPlayer);
    }
}
