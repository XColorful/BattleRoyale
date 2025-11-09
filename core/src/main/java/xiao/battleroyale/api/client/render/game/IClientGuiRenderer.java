package xiao.battleroyale.api.client.render.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.game.gui.IClientGameInfoRenderer;
import xiao.battleroyale.api.client.render.game.gui.IClientTeamInfoRenderer;

public interface IClientGuiRenderer {

    @NotNull IClientGameInfoRenderer getClientGameInfoRenderer();

    void setClientGameInfoRenderer(@NotNull IClientGameInfoRenderer clientGameInfoRenderer);

    @NotNull IClientTeamInfoRenderer getClientTeamInfoRenderer();

    void setClientTeamInfoRenderer(@NotNull IClientTeamInfoRenderer clientTeamInfoRenderer);
}
