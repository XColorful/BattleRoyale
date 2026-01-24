package xiao.battleroyale.api.client.render;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.level.IClientSpectateRenderer;
import xiao.battleroyale.api.client.render.level.IClientTeamRenderer;
import xiao.battleroyale.api.client.render.level.IClientZoneRenderer;

public interface IClientLevelRenderer {

    @NotNull IClientSpectateRenderer getClientSpectateRenderer();

    void setClientSpectateRenderer(@NotNull IClientSpectateRenderer clientSpectateRenderer);

    @NotNull IClientTeamRenderer getClientTeamRenderer();

    void setClientTeamRenderer(@NotNull IClientTeamRenderer clientTeamRenderer);

    @NotNull IClientZoneRenderer getClientZoneRenderer();

    void setClientZoneRenderer(@NotNull IClientZoneRenderer clientZoneRenderer);
}
