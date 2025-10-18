package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractSenderEvent extends AbstractGameEvent {

    protected final ServerPlayer sender;
    protected final ServerPlayer targetPlayer;

    public AbstractSenderEvent(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer) {
        super(gameManager);
        this.sender = sender;
        this.targetPlayer = targetPlayer;
    }

    public ServerPlayer getSender() {
        return this.sender;
    }

    public ServerPlayer getTargetPlayer() {
        return this.targetPlayer;
    }
}
