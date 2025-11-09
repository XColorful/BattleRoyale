package xiao.battleroyale.client.renderer.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.game.IClientLevelRenderer;
import xiao.battleroyale.api.client.render.game.level.IClientSpectateRenderer;
import xiao.battleroyale.api.client.render.game.level.IClientTeamRenderer;
import xiao.battleroyale.api.client.render.game.level.IClientZoneRenderer;
import xiao.battleroyale.client.renderer.game.level.SpectatePlayerRenderer;
import xiao.battleroyale.client.renderer.game.level.TeamMemberRenderer;
import xiao.battleroyale.client.renderer.game.level.ZoneRenderer;

public class ClientLevelRenderer implements IClientLevelRenderer {

    private static class ClientLevelRendererHolder {
        private static final ClientLevelRenderer INSTANCE = new ClientLevelRenderer();
    }

    protected ClientLevelRenderer() {}

    public static ClientLevelRenderer get() {
        return ClientLevelRendererHolder.INSTANCE;
    }

    private @NotNull IClientSpectateRenderer clientSpectateRenderer = SpectatePlayerRenderer.get();
    private @NotNull IClientTeamRenderer clientTeamRenderer = TeamMemberRenderer.get();
    private @NotNull IClientZoneRenderer clientZoneRenderer = ZoneRenderer.get();

    @Override public @NotNull IClientSpectateRenderer getClientSpectateRenderer() {
        return clientSpectateRenderer;
    }
    @Override public @NotNull IClientTeamRenderer getClientTeamRenderer() {
        return clientTeamRenderer;
    }
    @Override public @NotNull IClientZoneRenderer getClientZoneRenderer() {
        return clientZoneRenderer;
    }

    @Override public void setClientSpectateRenderer(@NotNull IClientSpectateRenderer clientSpectateRenderer) {
        BattleRoyale.LOGGER.debug("Replace ClientSpectateRenderer {} with {}", this.clientSpectateRenderer.getRendererName(), clientSpectateRenderer.getRendererName());
        this.clientSpectateRenderer = clientSpectateRenderer;
    }
    @Override public void setClientTeamRenderer(@NotNull IClientTeamRenderer clientTeamRenderer) {
        BattleRoyale.LOGGER.debug("Replace ClientTeamRenderer {} with {}", this.clientTeamRenderer.getRendererName(), clientTeamRenderer.getRendererName());
        this.clientTeamRenderer = clientTeamRenderer;
    }
    @Override public void setClientZoneRenderer(@NotNull IClientZoneRenderer clientZoneRenderer) {
        BattleRoyale.LOGGER.debug("Replace ClientZoneRenderer {} with {}", this.clientZoneRenderer.getRendererName(), clientZoneRenderer.getRendererName());
        this.clientZoneRenderer = clientZoneRenderer;
    }
}
