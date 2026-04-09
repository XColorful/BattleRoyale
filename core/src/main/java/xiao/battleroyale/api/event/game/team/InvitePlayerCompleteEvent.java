package xiao.battleroyale.api.event.game.team;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class InvitePlayerCompleteEvent extends AbstractSenderEvent {

    protected final boolean accept;

    public InvitePlayerCompleteEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer, boolean accept) {
        super(gameManager, sender, targetPlayer);
        this.accept = accept;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.INVITE_PLAYER_COMPLETE_EVENT;
    }

    public boolean isAccept() {
        return accept;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return super.createCommandSourceStack(source)
                .withRotation(targetPlayer.getRotationVector())
                .withEntity(targetPlayer);
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(InvitePlayerCompleteEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
