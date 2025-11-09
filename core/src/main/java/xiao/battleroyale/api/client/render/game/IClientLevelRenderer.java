package xiao.battleroyale.api.client.render.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.game.level.IClientSpectateRenderer;
import xiao.battleroyale.api.client.render.game.level.IClientTeamRenderer;
import xiao.battleroyale.api.client.render.game.level.IClientZoneRenderer;

public interface IClientLevelRenderer {

    @NotNull IClientSpectateRenderer getClientSpectateRenderer();

    void setClientSpectateRenderer(@NotNull IClientSpectateRenderer clientSpectateRenderer);

    @NotNull IClientTeamRenderer getClientTeamRenderer();

    void setClientTeamRenderer(@NotNull IClientTeamRenderer clientTeamRenderer);

    @NotNull IClientZoneRenderer getClientZoneRenderer();

    void setClientZoneRenderer(@NotNull IClientZoneRenderer clientZoneRenderer);
}
