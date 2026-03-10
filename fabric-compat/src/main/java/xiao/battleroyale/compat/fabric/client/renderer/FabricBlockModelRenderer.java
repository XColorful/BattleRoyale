package xiao.battleroyale.compat.fabric.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class FabricBlockModelRenderer implements IBlockModelRenderer {

    @Override
    public void renderBlockModel(@NotNull BlockState blockState,
                                 @NotNull BakedModel bakedModel,
                                 @NotNull ModelBlockRenderer modelBlockRenderer,
                                 @NotNull PoseStack poseStack,
                                 @NotNull MultiBufferSource bufferIn,
                                 int combinedLightIn,
                                 int combinedOverlayIn) {
        RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(blockState);

        // 直接调用原版渲染方法
        modelBlockRenderer.renderModel(
                poseStack.last(),
                bufferIn.getBuffer(renderType),
                blockState,
                bakedModel,
                1.0F, 1.0F, 1.0F,
                combinedLightIn,
                combinedOverlayIn
        );
    }
}