package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.client.renderer.BlockModelRenderer;

public abstract class AbstractBlockRenderer<T extends AbstractLootBlockEntity> implements BlockEntityRenderer<T> {

    protected final ItemRenderer itemRenderer;
    protected final BlockRenderDispatcher blockRenderDispatcher;

    public AbstractBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    protected void renderBlockModel(@NotNull BlockEntity blockEntity, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockState = blockEntity.getBlockState();
        BakedModel bakedModel = this.blockRenderDispatcher.getBlockModel(blockState);
        ModelBlockRenderer modelBlockRenderer = this.blockRenderDispatcher.getModelRenderer();

        BlockModelRenderer.get().renderBlockModel(blockState,
                bakedModel,
                modelBlockRenderer,
                poseStack,
                bufferIn,
                combinedLightIn,
                combinedOverlayIn);
    }

    /**
     * 通用射线检测逻辑
     * @param blockEntity 目标方块实体
     * @param cameraPos 摄像机实时坐标 (解决 F5 穿帮)
     * @param yOffset 目标点 Y 轴偏移（用于微调检测高度）
     */
    protected boolean isVisible(T blockEntity, Vec3 cameraPos, double yOffset) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return false;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 targetPos = new Vec3(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);

        BlockHitResult result = mc.level.clip(new ClipContext(
                cameraPos,
                targetPos,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                mc.cameraEntity
        ));

        return result.getType() == HitResult.Type.MISS || result.getBlockPos().equals(pos);
    }
}
