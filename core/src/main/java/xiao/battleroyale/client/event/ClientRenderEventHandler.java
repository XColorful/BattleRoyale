package xiao.battleroyale.client.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.IRenderGuiEventPost;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.render.IClientGuiRenderer;
import xiao.battleroyale.api.client.render.IClientLevelRenderer;

public class ClientRenderEventHandler {

    @Deprecated
    public static void onRenderLevelStage(IRenderLevelStageEvent event) {
    }
    // 与onRenderLevelStage等价
    public static void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {
        IClientLevelRenderer clientLevelRenderer = BattleRoyale.getClientLevelRenderer();
        clientLevelRenderer.getClientZoneRenderer().onAfterTranslucentBlocks(event);
        clientLevelRenderer.getClientTeamRenderer().onAfterTranslucentBlocks(event); // 后绘制
        clientLevelRenderer.getClientSpectateRenderer().onAfterTranslucentBlocks(event);
    }

    public static void onRenderGuiEvent(IRenderGuiEventPost event) {
        IClientGuiRenderer clientGuiRenderer = BattleRoyale.getClientGuiRenderer();
        clientGuiRenderer.getClientGameInfoRenderer().onRenderGuiEvent(event);
        clientGuiRenderer.getClientTeamInfoRenderer().onRenderGuiEvent(event);
    }
}
