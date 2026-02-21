package xiao.battleroyale.api.client.render;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.gui.IClientGameInfoRenderer;
import xiao.battleroyale.api.client.render.gui.IClientTeamInfoRenderer;

public interface IClientGuiRenderer extends IClientSubRenderer {

    @NotNull IClientGameInfoRenderer getClientGameInfoRenderer();

    void setClientGameInfoRenderer(@NotNull IClientGameInfoRenderer clientGameInfoRenderer);

    @NotNull IClientTeamInfoRenderer getClientTeamInfoRenderer();

    void setClientTeamInfoRenderer(@NotNull IClientTeamInfoRenderer clientTeamInfoRenderer);
}
