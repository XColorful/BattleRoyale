package xiao.battleroyale.compat.neoforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import net.neoforged.neoforge.client.model.data.ModelData;

import xiao.battleroyale.api.client.render.IBlockModelRenderer;

public class NeoBlockModelRenderer implements IBlockModelRenderer {

    @Override
    public void renderBlockModel(@NotNull BlockState blockState,
                                 @NotNull BakedModel bakedModel,
                                 @NotNull ModelBlockRenderer modelBlockRenderer,
                                 @NotNull PoseStack poseStack,
                                 @NotNull MultiBufferSource bufferIn,
                                 int combinedLightIn,
                                 int combinedOverlayIn) {
        for (RenderType renderType : bakedModel.getRenderTypes(blockState, RandomSource.create(), ModelData.EMPTY)) {
            modelBlockRenderer.renderModel(
                    poseStack.last(),
                    bufferIn.getBuffer(renderType),
                    blockState,
                    bakedModel,
                    1.0F, 1.0F, 1.0F, // 表示不着色
                    combinedLightIn,
                    combinedOverlayIn,
                    ModelData.EMPTY,
                    renderType
            );
        }
    }
}