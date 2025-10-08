package xiao.battleroyale.compat.neoforge.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
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
        ModelBlockRenderer.renderModel(
                poseStack.last(),
                bufferIn,
                blockStateModel,
                1.0F, 1.0F, 1.0F,
                combinedLightIn,
                combinedOverlayIn,
                EmptyBlockAndTintGetter.INSTANCE,
                BlockPos.ZERO,
                Blocks.AIR.defaultBlockState()
        );
    }
}