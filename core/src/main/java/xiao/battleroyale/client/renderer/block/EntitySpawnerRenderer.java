package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;

public class EntitySpawnerRenderer implements BlockEntityRenderer<EntitySpawnerBlockEntity> {

    public EntitySpawnerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull EntitySpawnerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull EntitySpawnerBlockEntity blockEntity) {
        return true;
    }
}