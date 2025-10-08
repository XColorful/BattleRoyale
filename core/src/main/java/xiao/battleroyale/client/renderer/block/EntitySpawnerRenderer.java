package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;

public class EntitySpawnerRenderer implements BlockEntityRenderer<EntitySpawnerBlockEntity> {

    public EntitySpawnerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull EntitySpawnerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay, @NotNull Vec3 cameraPos) {

    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}