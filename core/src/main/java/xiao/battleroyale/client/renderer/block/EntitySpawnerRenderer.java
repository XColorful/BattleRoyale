package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;

public class EntitySpawnerRenderer extends AbstractBlockRenderer<EntitySpawnerBlockEntity, BlockEntityRenderState> {

    protected static double MAX_RENDER_DISTANCE_SQ = 16 * 16;
    public static void setRenderDistance(double distance) {
        MAX_RENDER_DISTANCE_SQ = distance * distance;
    }

    public EntitySpawnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    /**
     * EntitySpawner 的 BlockBench 模型就 13 个方块，不整 {@link LootContainerRenderer} 的那套机制
     */
    @Override
    public void render(@NotNull EntitySpawnerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn,
                       @NotNull BlockEntityRenderState renderState, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 获取摄像机数据
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.position();
        Vector3fc cameraLook = camera.forwardVector();

        // ---1. 距离判断---
        BlockPos pos = blockEntity.getBlockPos();
        double dx = pos.getX() + 0.5 - cameraPos.x;
        double dy = pos.getY() + 0.5 - cameraPos.y;
        double dz = pos.getZ() + 0.5 - cameraPos.z;
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq > MAX_RENDER_DISTANCE_SQ) return;

        // ---2. 夹角判断---
        // shouldRenderOffScreen并没有对方块实体生效
        if (cameraLook.x() * dx + cameraLook.y() * dy + cameraLook.z() * dz < -0.1) return; // 小阈值防止视野边缘闪烁

        // ---3. 渲染执行---
        // 实体刷新方块模型长宽为32x32
        poseStack.pushPose();
        poseStack.translate(0.25F, 0, 0.25F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        renderBlockModel(blockEntity, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}