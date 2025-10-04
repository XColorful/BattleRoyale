package xiao.battleroyale.compat.forge.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.client.event.ClientGameEventHandler;
import xiao.battleroyale.client.event.ClientRenderEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeClientTickEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ForgeClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientGameEventHandler.onClientTick(new ForgeClientTickEvent(event));
        }
    }

//    @SubscribeEvent
//    public static void onRenderLevelStage(RenderLevelStageEvent event) {
//        ClientRenderEventHandler.onRenderLevelStage(new ForgeRenderLevelStageEvent(event));
//    }
    // 与onRenderLevelStage等价
    @SubscribeEvent
    public static void onAfterTranslucentBlocks(RenderLevelStageEvent event) {
        if (ForgeRenderLevelStage.fromStage(event.getStage()) != RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        ClientRenderEventHandler.onAfterTranslucentBlocks(new ForgeRenderLevelStageEvent(event));
    }

    @SubscribeEvent
    public static void onRenderGuiEvent(CustomizeGuiOverlayEvent event) {
        ClientRenderEventHandler.onRenderGuiEvent(new ForgeRenderGuiEventPost(event));
    }
}
