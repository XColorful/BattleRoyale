package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.block.entity.AbstractLootBlockEntity;
import xiao.battleroyale.client.renderer.BlockModelRenderer;

public abstract class AbstractBlockRenderer<T extends AbstractLootBlockEntity, S extends BlockEntityRenderState> implements BlockEntityRenderer<T, S> {

    protected final ItemRenderer itemRenderer;
    protected final BlockRenderDispatcher blockRenderDispatcher;

    public AbstractBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.itemRenderer();
        this.blockRenderDispatcher = context.blockRenderDispatcher();
    }

    protected void renderBlockModel(@NotNull BlockEntity blockEntity, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockState = blockEntity.getBlockState();
        BlockStateModel bakedModel = this.blockRenderDispatcher.getBlockModel(blockState);
        ModelBlockRenderer modelBlockRenderer = this.blockRenderDispatcher.getModelRenderer();

        BlockModelRenderer.get().renderBlockModel(blockState,
                bakedModel,
                modelBlockRenderer,
                poseStack,
                bufferIn,
                combinedLightIn,
                combinedOverlayIn);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull S createRenderState() {
        return (S) new BlockEntityRenderState();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void submit(@NotNull S renderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        T blockEntity = (T) minecraft.level.getBlockEntity(renderState.blockPos);
        if (blockEntity == null) return;

        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        @NotNull MultiBufferSource bufferIn = minecraft.renderBuffers().bufferSource();
        int combinedLightIn = renderState.lightCoords;

        int progress = renderState.breakProgress != null ? renderState.breakProgress.progress() : -1;
        int combinedOverlayIn = progress != -1 ? getDestroyProgressOverlay(progress) : 0;

        this.render(blockEntity, partialTick, poseStack, bufferIn, combinedLightIn, combinedOverlayIn, renderState, collector, cameraState);
    }

    protected abstract void render(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn,
                                   @NotNull S renderState, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraState);

    public static int getDestroyProgressOverlay(int progress) {
        if (progress < 0 || progress > 9) {
            return 0; // -1 或其他无效进度返回 0
        }
        // 破坏进度 progress (0-9) 被编码到 combinedOverlayIn 的 S 坐标 (U)
        // 公式是 (progress * 20 + 10) / 256.0 * 65536
        // 实际上就是 (progress * 20 + 10) << 16，因为 65536 = 256 << 8 * 256 << 8
        // 简化后：
        return (progress * 20 + 10) << 16;
    }
}
