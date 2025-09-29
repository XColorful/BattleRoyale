package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class InvitePlayerData extends AbstractSenderEventData {

    public @NotNull final GamePlayer senderGamePlayer;

    public InvitePlayerData(IGameManager gameManager, @NotNull GamePlayer senderGamePlayer, ServerPlayer sender, ServerPlayer targetPlayer) {
        super(gameManager, sender, targetPlayer);
        this.senderGamePlayer = senderGamePlayer;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.INVITE_PLAYER_EVENT;
    }
}
