package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;

public class EntitySpawnerRenderer implements BlockEntityRenderer<EntitySpawnerBlockEntity> {

    public EntitySpawnerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(EntitySpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // 目前什么都不渲染
    }

    @Override
    public boolean shouldRenderOffScreen(EntitySpawnerBlockEntity blockEntity) {
        return true;
    }
}