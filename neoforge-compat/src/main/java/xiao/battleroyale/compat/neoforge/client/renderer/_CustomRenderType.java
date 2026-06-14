package xiao.battleroyale.compat.neoforge.client.renderer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.renderer.CustomRenderType;

@EventBusSubscriber(modid = BattleRoyale.MOD_ID)
public class _CustomRenderType {

    @SubscribeEvent
    public static void onRegisterRenderPipelines(RegisterRenderPipelinesEvent event) {
        CustomRenderType.onRegisterRenderPipelines(event::registerPipeline);
    }
}