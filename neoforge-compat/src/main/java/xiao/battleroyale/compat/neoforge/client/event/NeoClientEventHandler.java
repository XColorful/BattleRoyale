package xiao.battleroyale.compat.neoforge.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.client.event.ClientGameEventHandler;
import xiao.battleroyale.client.event.ClientRenderEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoClientTickEvent;

import static xiao.battleroyale.client.renderer.CustomRenderType.SOLID_TRANSLUCENT_COLOR_PIPELINE;

@EventBusSubscriber(value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
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

    @SubscribeEvent
    public static void onRegisterPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(SOLID_TRANSLUCENT_COLOR_PIPELINE);
    }
}