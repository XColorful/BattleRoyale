package xiao.battleroyale.client.init;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.client.renderer.block.LootSpawnerRenderer;
import xiao.battleroyale.client.renderer.block.EntitySpawnerRenderer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntitiesRender {
    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        BlockEntityRenderers.register(LootSpawnerBlockEntity.TYPE, LootSpawnerRenderer::new);
        BlockEntityRenderers.register(EntitySpawnerBlockEntity.TYPE, EntitySpawnerRenderer::new);
    }
}