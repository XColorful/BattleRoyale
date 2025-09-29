package xiao.battleroyale.compat.forge.client.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.api.client.init.IModEntityRender;
import xiao.battleroyale.client.init.ModEntityRender;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeModEntityRender {

    private static final IModEntityRender MOD_ENTITY_RENDER = ModEntityRender.get();

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        MOD_ENTITY_RENDER.onRegisterEntityRenderers();
    }
}