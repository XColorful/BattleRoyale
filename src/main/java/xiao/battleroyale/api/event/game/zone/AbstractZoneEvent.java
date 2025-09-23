package xiao.battleroyale.api.event.game.zone;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public abstract class AbstractZoneEvent extends AbstractGameStatsEvent {

    private @NotNull final IGameZone gameZone;

    public AbstractZoneEvent(IGameManager gameManager, @NotNull IGameZone gameZone) {
        super(gameManager);
        this.gameZone = gameZone;
    }

    public @NotNull IGameZone getGameZone() {
        return this.gameZone;
    }
}
