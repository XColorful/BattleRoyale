package xiao.battleroyale.api.event.game.zone;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public abstract class AbstractZoneEventData extends AbstractGameStatsEventData {

    public @NotNull final IGameZone gameZone;

    public AbstractZoneEventData(IGameManager gameManager, @NotNull IGameZone gameZone) {
        super(gameManager);
        this.gameZone = gameZone;
    }
}
