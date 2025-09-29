package xiao.battleroyale.api.event.game.team;

import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractSenderEventData extends AbstractGameEventData {

    public final ServerPlayer sender;
    public final ServerPlayer targetPlayer;

    public AbstractSenderEventData(IGameManager gameManager, ServerPlayer sender, ServerPlayer targetPlayer) {
        super(gameManager);
        this.sender = sender;
        this.targetPlayer = targetPlayer;
    }
}
