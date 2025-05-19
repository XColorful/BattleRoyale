package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import xiao.battleroyale.block.LootSpawner;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;

public class LootSpawnerRenderer implements BlockEntityRenderer<LootSpawnerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public LootSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(LootSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof LootSpawner)) {
            return;
        }

        Player cameraPlayer = Minecraft.getInstance().player;
        if (cameraPlayer != null && blockEntity.getBlockPos().distSqr(cameraPlayer.blockPosition()) > 16 * 16) {
            return; // 超过 16 米不渲染
        }

        ItemStack[] items = new ItemStack[18];
        for (int i = 0; i < 18; i++) {
            items[i] = blockEntity.getItem(i);
        }

        Direction facing = blockState.getValue(LootSpawner.FACING);
        Vector3f renderOffset = getRenderOffset(facing);

        poseStack.pushPose();
        poseStack.translate(0.5F + renderOffset.x(), 0.1F + renderOffset.y(), 0.5F + renderOffset.z());

        switch (facing) {
            case NORTH:
                break;
            case SOUTH:
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180F));
                break;
            case WEST:
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90F));
                break;
            case EAST:
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90F));
                break;
        }

        int itemCount = 0;
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                itemCount++;
            }
        }
        int rows = (int) Math.ceil((double) itemCount / 9.0D);
        float itemScale = 0.0F;
        float yOffsetIncrement = 0.0F;

        if (itemCount <= 16) {
            itemScale = 1.0F / 4.0F;
            yOffsetIncrement = 0.25F;
        } else {
            itemScale = 1.0F / 5.0F;
            yOffsetIncrement = 0.2F;
        }

        poseStack.scale(itemScale, itemScale, itemScale);

        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();
                int row = i / 9;
                int col = i % 9;
                float xOffset = (col - 4.0F) * 0.22F;
                float yOffset = row * yOffsetIncrement * (1 / itemScale);
                float zOffset = -0.01F;

                poseStack.translate(xOffset, yOffset, zOffset);
                this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, blockEntity.getLevel(), 0);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private Vector3f getRenderOffset(Direction facing) {
        return switch (facing) {
            case NORTH -> new Vector3f(0, 0, -0.4F);
            case SOUTH -> new Vector3f(0, 0, 0.4F);
            case WEST -> new Vector3f(-0.4F, 0, 0);
            case EAST -> new Vector3f(0.4F, 0, 0);
            default -> new Vector3f(0, 0, 0);
        };
    }

    @Override
    public boolean shouldRenderOffScreen(LootSpawnerBlockEntity blockEntity) {
        return true;
    }
}