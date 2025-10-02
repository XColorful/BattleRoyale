package xiao.battleroyale.compat.neoforge.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.team.InvitePlayerData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class InvitePlayerEvent extends AbstractSenderEvent {

    protected @NotNull final GamePlayer senderGamePlayer;

    public InvitePlayerEvent(IGameManager gameManager, @NotNull GamePlayer senderGamePlayer, ServerPlayer sender, ServerPlayer targetPlayer) {
        super(gameManager, sender, targetPlayer);
        this.senderGamePlayer = senderGamePlayer;
    }

    public @NotNull GamePlayer getSenderGamePlayer() {
        return this.senderGamePlayer;
    }

    public static InvitePlayerEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof InvitePlayerData data)) {
            throw new RuntimeException("Expected InvitePlayerData but received: " + customEventData.getClass().getName());
        }
        return new InvitePlayerEvent(data.gameManager, data.senderGamePlayer, data.sender, data.targetPlayer);
    }
}