package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;

public class EntitySpawnerRenderer extends AbstractBlockRenderer<EntitySpawnerBlockEntity> implements BlockEntityRenderer<EntitySpawnerBlockEntity> {

    protected static double MAX_RENDER_DISTANCE_SQ = 16 * 16;
    public static void setRenderDistance(double distance) {
        MAX_RENDER_DISTANCE_SQ = distance * distance;
    }

    public EntitySpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntitySpawnerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();

        boolean renderBlock = (Minecraft.getInstance().player != null
                && blockEntity.getBlockPos().distSqr(Minecraft.getInstance().player.blockPosition()) <= MAX_RENDER_DISTANCE_SQ);
        if (renderBlock) {
            // 实体刷新方块模型长宽为32x32
            poseStack.translate(0.25F, 0, 0.25F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            renderBlockModel(blockEntity, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull EntitySpawnerBlockEntity blockEntity) {
        return true;
    }
}