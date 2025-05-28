package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class LootContainerRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected final ItemRenderer itemRenderer;

    public LootContainerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(@NotNull T blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        // TODO 暂时超过 16 米不渲染，以后再加配置
        if (Minecraft.getInstance().player != null && blockEntity.getBlockPos().distSqr(Minecraft.getInstance().player.blockPosition()) > 16 * 16) {
            return;
        }

        ItemStack[] items = getItems(blockEntity);
        if (items == null || items.length == 0) {
            return;
        }

        Vector3f renderOffset = getRenderOffset(blockEntity);
        poseStack.pushPose();
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

        poseStack.popPose();
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