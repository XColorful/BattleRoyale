package xiao.battleroyale.compat.neoforge.client.init;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import xiao.battleroyale.api.client.init.IModEntityRender;
import xiao.battleroyale.client.init.ModEntityRender;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NeoModEntityRender {

    private static final IModEntityRender MOD_ENTITY_RENDER = ModEntityRender.get();

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        MOD_ENTITY_RENDER.onRegisterEntityRenderers();
    }
}