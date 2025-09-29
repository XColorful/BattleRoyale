package xiao.battleroyale.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IBlockModelRenderer;

public class BlockModelRenderer implements IBlockModelRenderer {

    private static BlockModelRenderer INSTANCE;
    private final IBlockModelRenderer blockModelRenderer;

    private BlockModelRenderer(IBlockModelRenderer blockModelRenderer) {
        this.blockModelRenderer = blockModelRenderer;
    }

    public static void initialize(IBlockModelRenderer blockModelRenderer) {
        if (INSTANCE != null) {
            throw new IllegalStateException("BlockModelRenderer already initialized.");
        }
        INSTANCE = new BlockModelRenderer(blockModelRenderer);
    }

    public static BlockModelRenderer get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("BlockModelRenderer not initialized. Call initialize() first.");
        }
        return INSTANCE;
    }

    @Override
    public void renderBlockModel(@NotNull BlockState blockState,
                                 @NotNull BakedModel bakedModel,
                                 @NotNull ModelBlockRenderer modelBlockRenderer,
                                 @NotNull PoseStack poseStack,
                                 @NotNull MultiBufferSource bufferIn,
                                 int combinedLightIn,
                                 int combinedOverlayIn) {
        this.blockModelRenderer.renderBlockModel(blockState,
                bakedModel,
                modelBlockRenderer,
                poseStack,
                bufferIn,
                combinedLightIn,
                combinedOverlayIn);
    }
}
