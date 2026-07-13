package xiao.battleroyale.compat.neoforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;

public class NeoBlockModelRenderer implements IBlockModelRenderer {

    @Override
    public void renderBlockModel(@NotNull BlockState blockState,
                                 @NotNull BlockStateModel blockStateModel,
                                 @NotNull ModelBlockRenderer modelBlockRenderer,
                                 @NotNull PoseStack poseStack,
                                 @NotNull SubmitNodeCollector collector,
                                 int combinedLightIn,
                                 int combinedOverlayIn) {
        RenderType renderType = RenderTypes.translucentMovingBlock();

        // 26.2+ 使用 submitCustomGeometry 提交方块模型顶点
        collector.submitCustomGeometry(poseStack, renderType, (pose, vertexBuffer) -> {
        BlockQuadOutput output = (x, y, z, quad, instance) -> {
            instance.setLightCoords(combinedLightIn);
            instance.setOverlayCoords(combinedOverlayIn);
            vertexBuffer.putBakedQuad(pose, quad, instance);
        };

        modelBlockRenderer.tesselateBlock(
                output,
                0.0F, 0.0F, 0.0F,
                BlockAndTintGetter.EMPTY,
                BlockPos.ZERO,
                blockState,
                blockStateModel,
                42L
        );
        });
    }
}