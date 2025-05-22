package xiao.battleroyale.client.init;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.init.ModBlocks;
import xiao.battleroyale.client.renderer.block.LootSpawnerRenderer;
import xiao.battleroyale.client.renderer.block.EntitySpawnerRenderer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitiesRender {
    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        BlockEntityRenderers.register(ModBlocks.LOOT_SPAWNER_BE.get(), LootSpawnerRenderer::new);
        BlockEntityRenderers.register(ModBlocks.ENTITY_SPAWNER_BE.get(), EntitySpawnerRenderer::new);
    }
}