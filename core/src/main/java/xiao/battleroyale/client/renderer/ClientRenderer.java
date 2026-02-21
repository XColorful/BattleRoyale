package xiao.battleroyale.client.renderer;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.IClientRenderer;
import xiao.battleroyale.api.client.render.IClientSubRenderer;
import xiao.battleroyale.api.client.render.gui.IClientGameInfoRenderer;
import xiao.battleroyale.api.client.render.gui.IClientTeamInfoRenderer;
import xiao.battleroyale.api.client.render.level.IClientSpectateRenderer;
import xiao.battleroyale.api.client.render.level.IClientTeamRenderer;
import xiao.battleroyale.api.client.render.level.IClientZoneRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.client.renderer.gui.GameInfoRenderer;
import xiao.battleroyale.client.renderer.gui.TeamInfoRenderer;
import xiao.battleroyale.client.renderer.level.SpectatePlayerRenderer;
import xiao.battleroyale.client.renderer.level.TeamMemberRenderer;
import xiao.battleroyale.client.renderer.level.ZoneRenderer;

public class ClientRenderer implements IClientRenderer {

    private static class ClientRendererHolder {
        private static final ClientRenderer INSTANCE = new ClientRenderer();
    }

    public static ClientRenderer get() {
        return ClientRendererHolder.INSTANCE;
    }

    protected ClientRenderer() {
        // ClientLevelRenderer
        this.clientSpectateRenderer = SpectatePlayerRenderer.get(); this.clientSpectateRenderer.registerRenderEventHandler();
        this.clientTeamRenderer = TeamMemberRenderer.get(); this.clientTeamRenderer.registerRenderEventHandler();
        this.clientZoneRenderer = ZoneRenderer.get(); this.clientZoneRenderer.registerRenderEventHandler();
        // ClientGuiRenderer
        this.clientGameInfoRenderer = GameInfoRenderer.get(); this.clientGameInfoRenderer.registerRenderEventHandler();
        this.clientTeamInfoRenderer = TeamInfoRenderer.get(); this.clientTeamInfoRenderer.registerRenderEventHandler();
    }

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            BattleRoyale.LOGGER.debug("ClientRenderer skipped init() at {}", mcSide.toString());
            return;
        }
        // ClientLevelRenderer
        SpectatePlayerRenderer.init(mcSide);
        TeamMemberRenderer.init(mcSide);
        ZoneRenderer.init(mcSide);
        // ClientGuiRenderer
        GameInfoRenderer.init(mcSide);
        TeamInfoRenderer.init(mcSide);
    }

    @Override
    public String getRendererName() {
        return String.format("%s:ClientRenderer", BattleRoyale.MOD_ID);
    }

    // ClientLevelRenderer
    private @NotNull IClientSpectateRenderer clientSpectateRenderer;
    private @NotNull IClientTeamRenderer clientTeamRenderer;
    private @NotNull IClientZoneRenderer clientZoneRenderer;
    // ClientGuiRenderer
    private @NotNull IClientGameInfoRenderer clientGameInfoRenderer;
    private @NotNull IClientTeamInfoRenderer clientTeamInfoRenderer;

    protected void registerNewRenderer(IClientSubRenderer previousManager, IClientSubRenderer newManager) {
        if (previousManager != null) {
            if (previousManager.unregisterRenderEventHandler()) {
                BattleRoyale.LOGGER.debug("Unregistering previous ClientSubRenderer {}", previousManager.getRendererName());
            } else {
                BattleRoyale.LOGGER.debug("Failed to unregister previous ClientSubRenderer {}", previousManager.getRendererName());
            }
        }
        if (newManager.registerRenderEventHandler()) {
            BattleRoyale.LOGGER.debug("Register new ClientSubRenderer {}", newManager.getRendererName());
        } else {
            BattleRoyale.LOGGER.warn("Failed to register new ClientSubRenderer {}", newManager.getRendererName());
        }
    }

    // ClientLevelRenderer
    @Override public void setClientSpectateRenderer(@NotNull IClientSpectateRenderer clientSpectateRenderer) {
        registerNewRenderer(this.clientSpectateRenderer, clientSpectateRenderer);
        this.clientSpectateRenderer = clientSpectateRenderer;
    }
    @Override public void setClientTeamRenderer(@NotNull IClientTeamRenderer clientTeamRenderer) {
        registerNewRenderer(this.clientTeamRenderer, clientTeamRenderer);
        this.clientTeamRenderer = clientTeamRenderer;
    }
    @Override public void setClientZoneRenderer(@NotNull IClientZoneRenderer clientZoneRenderer) {
        registerNewRenderer(this.clientZoneRenderer, clientZoneRenderer);
        this.clientZoneRenderer = clientZoneRenderer;
    }
    // ClientGuiRenderer
    @Override public void setClientGameInfoRenderer(@NotNull IClientGameInfoRenderer clientGameInfoRenderer) {
        registerNewRenderer(this.clientGameInfoRenderer, clientGameInfoRenderer);
        this.clientGameInfoRenderer = clientGameInfoRenderer;
    }
    @Override public void setClientTeamInfoRenderer(@NotNull IClientTeamInfoRenderer clientTeamInfoRenderer) {
        registerNewRenderer(this.clientTeamInfoRenderer, clientTeamInfoRenderer);
        this.clientTeamInfoRenderer = clientTeamInfoRenderer;
    }

    // ClientLevelRenderer
    @Override public @NotNull IClientSpectateRenderer getClientSpectateRenderer() {
        return clientSpectateRenderer;
    }
    @Override public @NotNull IClientTeamRenderer getClientTeamRenderer() {
        return clientTeamRenderer;
    }
    @Override public @NotNull IClientZoneRenderer getClientZoneRenderer() {
        return clientZoneRenderer;
    }
    // ClientGuiRenderer
    @Override public @NotNull IClientGameInfoRenderer getClientGameInfoRenderer() {
        return clientGameInfoRenderer;
    }
    @Override public @NotNull IClientTeamInfoRenderer getClientTeamInfoRenderer() {
        return clientTeamInfoRenderer;
    }

    @Override
    public boolean registerRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        return true;
    }
    @Override
    public boolean unregisterRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        return true;
    }
}