package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
}
