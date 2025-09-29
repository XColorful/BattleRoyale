package xiao.battleroyale.client.init;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import xiao.battleroyale.api.client.init.IModEntityRender;
import xiao.battleroyale.init.registry.ModBlocks;
import xiao.battleroyale.client.renderer.block.LootSpawnerRenderer;
import xiao.battleroyale.client.renderer.block.EntitySpawnerRenderer;

public class ModEntityRender implements IModEntityRender {

    private static final ModEntityRender INSTANCE = new ModEntityRender();

    public static ModEntityRender get() {
        return INSTANCE;
    }

    private ModEntityRender() {}

    @Override
    public void onRegisterEntityRenderers() {
        BlockEntityRenderers.register(ModBlocks.LOOT_SPAWNER_BE.get(), LootSpawnerRenderer::new);
        BlockEntityRenderers.register(ModBlocks.ENTITY_SPAWNER_BE.get(), EntitySpawnerRenderer::new);
    }
}