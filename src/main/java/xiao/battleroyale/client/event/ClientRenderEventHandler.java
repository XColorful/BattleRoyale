package xiao.battleroyale.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.renderer.game.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ClientRenderEventHandler {

    private static final ZoneRenderer zoneRenderer = ZoneRenderer.get();
    private static final TeamMemberRenderer teamMemberRenderer = TeamMemberRenderer.get();
    private static final SpectatePlayerRenderer SPECTATE_PLAYER_RENDERER = SpectatePlayerRenderer.get();
    private static final GameInfoRenderer gameInfoRenderer = GameInfoRenderer.get();
    private static final TeamInfoRenderer teamInfoRenderer = TeamInfoRenderer.get();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        zoneRenderer.onRenderLevelStage(event);
        teamMemberRenderer.onRenderLevelStage(event); // 后绘制
        SPECTATE_PLAYER_RENDERER.onRenderLevelStage(event);
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(RenderGuiEvent.Post event) {
        gameInfoRenderer.onRenderGuiEvent(event);
        teamInfoRenderer.onRenderGuiEvent(event);
    }
}
