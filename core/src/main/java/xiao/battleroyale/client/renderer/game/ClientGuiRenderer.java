package xiao.battleroyale.client.renderer.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.game.IClientGuiRenderer;
import xiao.battleroyale.api.client.render.game.gui.IClientGameInfoRenderer;
import xiao.battleroyale.api.client.render.game.gui.IClientTeamInfoRenderer;
import xiao.battleroyale.client.renderer.game.gui.GameInfoRenderer;
import xiao.battleroyale.client.renderer.game.gui.TeamInfoRenderer;

public class ClientGuiRenderer implements IClientGuiRenderer {

    private static class ClientGuiRendererHolder {
        private static final ClientGuiRenderer INSTANCE = new ClientGuiRenderer();
    }

    protected ClientGuiRenderer() {}

    public static ClientGuiRenderer get() {
        return ClientGuiRendererHolder.INSTANCE;
    }

    private @NotNull IClientGameInfoRenderer clientGameInfoRenderer = GameInfoRenderer.get();
    private @NotNull IClientTeamInfoRenderer clientTeamInfoRenderer = TeamInfoRenderer.get();

    @Override public @NotNull IClientGameInfoRenderer getClientGameInfoRenderer() {
        return clientGameInfoRenderer;
    }
    @Override public @NotNull IClientTeamInfoRenderer getClientTeamInfoRenderer() {
        return clientTeamInfoRenderer;
    }

    @Override
    public void setClientGameInfoRenderer(@NotNull IClientGameInfoRenderer clientGameInfoRenderer) {
        BattleRoyale.LOGGER.debug("Replace ClientGameInfoRenderer {} with {}", this.clientGameInfoRenderer.getRendererName(), clientGameInfoRenderer.getRendererName());
        this.clientGameInfoRenderer = clientGameInfoRenderer;
    }
    @Override
    public void setClientTeamInfoRenderer(@NotNull IClientTeamInfoRenderer clientTeamInfoRenderer) {
        BattleRoyale.LOGGER.debug("Replace ClientTeamInfoRenderer {} with {}", this.clientTeamInfoRenderer.getRendererName(), clientTeamInfoRenderer);
        this.clientTeamInfoRenderer = clientTeamInfoRenderer;
    }
}
