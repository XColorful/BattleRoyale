package xiao.battleroyale.client.event;

import xiao.battleroyale.api.client.event.IRenderGuiEventPost;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.client.renderer.game.*;

public class ClientRenderEventHandler {

    private static final ZoneRenderer zoneRenderer = ZoneRenderer.get();
    private static final TeamMemberRenderer teamMemberRenderer = TeamMemberRenderer.get();
    private static final SpectatePlayerRenderer SPECTATE_PLAYER_RENDERER = SpectatePlayerRenderer.get();
    private static final GameInfoRenderer gameInfoRenderer = GameInfoRenderer.get();
    private static final TeamInfoRenderer teamInfoRenderer = TeamInfoRenderer.get();

    public static void onRenderLevelStage(IRenderLevelStageEvent event) {
        zoneRenderer.onRenderLevelStage(event);
        teamMemberRenderer.onRenderLevelStage(event);
        SPECTATE_PLAYER_RENDERER.onRenderLevelStage(event);
    }
    // 与onRenderLevelStage等价
    public static void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {
        zoneRenderer.onAfterTranslucentBlocks(event);
        teamMemberRenderer.onAfterTranslucentBlocks(event); // 后绘制
        SPECTATE_PLAYER_RENDERER.onAfterTranslucentBlocks(event);
    }

    public static void onRenderGuiEvent(IRenderGuiEventPost event) {
        gameInfoRenderer.onRenderGuiEvent(event);
        teamInfoRenderer.onRenderGuiEvent(event);
    }
}
