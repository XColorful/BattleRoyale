package xiao.battleroyale.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface IBlockModelRenderer {

    /**
     * 渲染给定的 BlockState 的静态方块模型。
     * 兼容层将负责处理底层的 ModelData 和 RenderType 选择。
     *
     * @param blockState 方块状态
     * @param blockStateModel
     * @param modelBlockRenderer
     * @param poseStack PoseStack
     * @param collector 提交节点收集器
     * @param combinedLightIn 综合光照
     * @param combinedOverlayIn 综合覆盖
     */
    void renderBlockModel(
            @NotNull BlockState blockState,
            @NotNull BlockStateModel blockStateModel,
            @NotNull ModelBlockRenderer modelBlockRenderer,
            @NotNull PoseStack poseStack,
            @NotNull SubmitNodeCollector collector,
            int combinedLightIn,
            int combinedOverlayIn
    );
}
