package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.block.entity.AbstractLootContainerBlockEntity;

public abstract class LootContainerRenderer<T extends AbstractLootContainerBlockEntity> implements BlockEntityRenderer<T> {

    protected final int MAX_RENDER_DISTANCE_SQ = 16 * 16;

    protected final ItemRenderer itemRenderer;
    protected final BlockRenderDispatcher blockRenderDispatcher;

    public LootContainerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();

        // TODO 暂时超过 16 米改渲染方块，以后再加配置
        boolean renderItem = (Minecraft.getInstance().player != null && blockEntity.getBlockPos().distSqr(Minecraft.getInstance().player.blockPosition()) <= MAX_RENDER_DISTANCE_SQ)
                && !blockEntity.isEmpty(); // 玩家在范围内 且 容器不为空
        if (!renderItem) { // 渲染方块模型
            renderBlockModel(blockEntity, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        } else { // 渲染容器内物品
            ItemStack[] items = getItems(blockEntity);
            if (items == null || items.length == 0) { // 照理不应该发生，除非hasItem有问题
                renderBlockModel(blockEntity, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
                return;
            }

            Vector3f renderOffset = getRenderOffset(blockEntity);

            poseStack.translate(0.5F + renderOffset.x(), renderOffset.y(), 0.5F + renderOffset.z()); // 初始平移到方块中心，并考虑整体偏移
            applyRotation(blockEntity, poseStack);

            float itemScale = 1 / 2F;

            poseStack.scale(itemScale, itemScale, itemScale);

            for (int i = 0; i < items.length; i++) {
                ItemStack itemStack = items[i];
                if (itemStack.isEmpty()) {
                    continue;
                }
                poseStack.pushPose();

                int row = (i % 16) / 4;
                int col = i % 4;
                int layer = i / 16;
                float xOffset = -0.75F + col * 0.5F;
                float zOffset = -0.75F + row * 0.5F;
                float yOffset = layer * itemScale;
                poseStack.translate(xOffset, yOffset, zOffset);

                this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, blockEntity.getLevel(), 0);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private void renderBlockModel(@NotNull T blockEntity, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockState = blockEntity.getBlockState();
        BakedModel bakedModel = this.blockRenderDispatcher.getBlockModel(blockState);
        ModelBlockRenderer modelBlockRenderer = this.blockRenderDispatcher.getModelRenderer();

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

    /**
     * 获取要渲染的物品数组。
     * 子类需要实现此方法以提供具体的物品。
     * @param blockEntity 方块实体
     * @return 物品数组
     */
    protected abstract ItemStack[] getItems(T blockEntity);

    /**
     * 获取整体渲染偏移量。
     * 子类可以根据方块朝向等因素提供偏移。
     * @param blockEntity 方块实体
     * @return 偏移量
     */
    protected abstract Vector3f getRenderOffset(T blockEntity);

    /**
     * 应用基于方块的旋转。
     * 子类需要实现此方法以根据方块的朝向进行旋转。
     * @param blockEntity 方块实体
     * @param poseStack PoseStack
     */
    protected abstract void applyRotation(T blockEntity, PoseStack poseStack);

    @Override
    public boolean shouldRenderOffScreen(@NotNull T blockEntity) {
        return true;
    }
}