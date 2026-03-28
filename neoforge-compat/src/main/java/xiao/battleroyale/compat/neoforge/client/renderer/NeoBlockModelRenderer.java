package xiao.battleroyale.compat.neoforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
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
                                 @NotNull MultiBufferSource bufferIn,
                                 int combinedLightIn,
                                 int combinedOverlayIn) {
        VertexConsumer consumer = bufferIn.getBuffer(Sheets.cutoutBlockSheet());
        BlockQuadOutput output = (x, y, z, quad, instance) -> {
            consumer.putBakedQuad(
                    poseStack.last(),
                    quad,
                    instance
            );
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
    }
}