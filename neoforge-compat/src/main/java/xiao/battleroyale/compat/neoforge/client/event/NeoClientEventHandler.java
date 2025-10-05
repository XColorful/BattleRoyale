package xiao.battleroyale.compat.neoforge.client.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.client.event.ClientGameEventHandler;
import xiao.battleroyale.client.event.ClientRenderEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoClientTickEvent;

public class NeoClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientGameEventHandler.onClientTick(new NeoClientTickEvent(event));
    }

//    @SubscribeEvent
//    public static void onRenderLevelStage(RenderLevelStageEvent event) {
//        ClientRenderEventHandler.onRenderLevelStage(new NeoRenderLevelStageEvent(event));
//    }
    // 与onRenderLevelStage等价
    @SubscribeEvent
    public static void onAfterTranslucentBlocks(RenderLevelStageEvent event) {
        if (NeoRenderLevelStage.fromStage(event.getStage()) != RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        ClientRenderEventHandler.onAfterTranslucentBlocks(new NeoRenderLevelStageEvent(event));
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(RenderGuiEvent.Post event) {
        ClientRenderEventHandler.onRenderGuiEvent(new NeoRenderGuiEventPost(event));
    }
}