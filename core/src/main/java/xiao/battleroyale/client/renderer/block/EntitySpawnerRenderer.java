package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xiao.battleroyale.block.entity.EntitySpawnerBlockEntity;

public class EntitySpawnerRenderer extends AbstractBlockRenderer<EntitySpawnerBlockEntity> implements BlockEntityRenderer<EntitySpawnerBlockEntity> {

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
    public void render(@NotNull EntitySpawnerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 获取摄像机数据
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vector3f cameraLook = camera.getLookVector();

        // --- 1. 距离判断 (优化：手动计算，避免 BlockPos.distSqr 内部对象调用) ---
        BlockPos pos = blockEntity.getBlockPos();
        double dx = pos.getX() + 0.5 - cameraPos.x;
        double dy = pos.getY() + 0.5 - cameraPos.y;
        double dz = pos.getZ() + 0.5 - cameraPos.z;
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq > MAX_RENDER_DISTANCE_SQ) return;

        // --- 2. 夹角判断 (极简数学过滤，砍掉身后 180 度的方块) ---
        // 这里的开销仅仅是 3 次乘法，比任何渲染调用都轻量
        if (cameraLook.x * dx + cameraLook.y * dy + cameraLook.z * dz < 0) return;

        // --- 3. 渲染执行 ---
        // 实体刷新方块模型长宽为32x32
        poseStack.pushPose();
        poseStack.translate(0.25F, 0, 0.25F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        renderBlockModel(blockEntity, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull EntitySpawnerBlockEntity blockEntity) {
        return false;
    }
}