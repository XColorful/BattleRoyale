package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.event.EventDispatcher;

public class InvitePlayerEvent extends AbstractSenderEvent {

    protected @NotNull final GamePlayer senderGamePlayer;

    public InvitePlayerEvent(IGameManager gameManager, @NotNull GamePlayer senderGamePlayer, ServerPlayer sender, ServerPlayer targetPlayer) {
        super(gameManager, sender, targetPlayer);
        this.senderGamePlayer = senderGamePlayer;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.INVITE_PLAYER_EVENT;
    }

    public @NotNull GamePlayer getSenderGamePlayer() {
        return this.senderGamePlayer;
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(InvitePlayerEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
