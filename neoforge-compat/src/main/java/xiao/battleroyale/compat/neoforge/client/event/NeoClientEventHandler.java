package xiao.battleroyale.compat.neoforge.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.client.event.ClientGameEventHandler;
import xiao.battleroyale.client.event.ClientRenderEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoClientTickEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID) // bus从 FORGE 变更为 NEOFORGE
public class NeoClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientGameEventHandler.onClientTick(new NeoClientTickEvent(event));
        }
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