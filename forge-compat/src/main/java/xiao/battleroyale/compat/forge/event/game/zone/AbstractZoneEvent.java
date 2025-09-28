package xiao.battleroyale.compat.forge.event.game.zone;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public abstract class AbstractZoneEvent extends AbstractGameStatsEvent {

    protected @NotNull final IGameZone gameZone;

    public AbstractZoneEvent(IGameManager gameManager, @NotNull IGameZone gameZone) {
        super(gameManager);
        this.gameZone = gameZone;
    }

    public @NotNull IGameZone getGameZone() {
        return this.gameZone;
    }
}
