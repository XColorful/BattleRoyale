package xiao.battleroyale.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.block.EntitySpawner;
import xiao.battleroyale.block.LootSpawner;
import xiao.battleroyale.block.entity.LootSpawnerBlockEntity;
import xiao.battleroyale.config.common.loot.LootConfigManager;

import java.awt.*;

public class LootBlockRenderer implements BlockEntityRenderer<LootSpawnerBlockEntity> {
    private static final Color DEFAULT_COLOR = new Color(255, 255, 255); // #FFFFFF
    private static final float BORDER_OFFSET = 0.45F;
    private static final float BORDER_HEIGHT = 0.1F;
    private static final float MIN = 0.0F;
    private static final float MAX = 1.0F;

    public LootBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LootSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ResourceLocation blockId = blockEntity.getLootObjectId();
        if (blockId == null) {
            return;
        }

        Block block = blockEntity.getBlockState().getBlock();
        LootConfigManager lootConfigManager = LootConfigManager.get();
        LootConfigManager.LootConfig config = null;
        int configId = -1; // Default invalid ID

        if (block instanceof LootSpawner) {
            configId = blockEntity.getConfigId();
            config = lootConfigManager.getLootSpawnerConfig(configId);
        } else if (block instanceof EntitySpawner) {
            configId = blockEntity.getConfigId();
            config = lootConfigManager.getEntitySpawnerConfig(configId);
        }

        if (config == null) {
            BattleRoyale.LOGGER.warn("Could not find LootConfig for block type: {} with id: {} for block: {}", block.getClass().getName(), configId, blockId);
            return;
        }

        Color colorToRender = DEFAULT_COLOR;
        try {
            colorToRender = Color.decode(config.getColor());
        } catch (NumberFormatException e) {
            BattleRoyale.LOGGER.error("Invalid color code in LootConfig id {}: {}, using default white.", config.getId(), config.getColor(), e);
        }

        poseStack.pushPose();
        VertexConsumer builder = bufferIn.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float r = colorToRender.getRed() / 255.0F;
        float g = colorToRender.getGreen() / 255.0F;
        float b = colorToRender.getBlue() / 255.0F;
        float a = colorToRender.getAlpha() / 255.0F;

        // South face (Z+)
        renderZeroThicknessFace(matrix, builder, r, g, b, a, MIN, BORDER_OFFSET + BORDER_HEIGHT, MAX, MAX, BORDER_OFFSET + BORDER_HEIGHT, MAX, MAX, BORDER_OFFSET, MAX, MIN, BORDER_OFFSET, MAX, 0, 0, 1, combinedLightIn, combinedOverlayIn);
        // North face (Z-)
        renderZeroThicknessFace(matrix, builder, r, g, b, a, MIN, 1 - BORDER_OFFSET - BORDER_HEIGHT, MIN, MAX, 1 - BORDER_OFFSET - BORDER_HEIGHT, MIN, MAX, 1 - BORDER_OFFSET, MIN, MIN, 1 - BORDER_OFFSET, MIN, 0, 0, -1, combinedLightIn, combinedOverlayIn);
        // West face (X-)
        renderZeroThicknessFace(matrix, builder, r, g, b, a, BORDER_OFFSET, MIN, MIN, BORDER_OFFSET + BORDER_HEIGHT, MIN, MIN, BORDER_OFFSET + BORDER_HEIGHT, MAX, MIN, BORDER_OFFSET, MAX, MIN, -1, 0, 0, combinedLightIn, combinedOverlayIn);
        // East face (X+)
        renderZeroThicknessFace(matrix, builder, r, g, b, a, 1 - BORDER_OFFSET - BORDER_HEIGHT, MIN, MAX, 1 - BORDER_OFFSET, MIN, MAX, 1 - BORDER_OFFSET, MAX, MAX, 1 - BORDER_OFFSET - BORDER_HEIGHT, MAX, MAX, 1, 0, 0, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    }

    private void renderZeroThicknessFace(Matrix4f matrix, VertexConsumer builder, float r, float g, float b, float a,
                                         float x1, float y1, float z1,
                                         float x2, float y2, float z2,
                                         float x3, float y3, float z3,
                                         float x4, float y4, float z4,
                                         float normalX, float normalY, float normalZ,
                                         int light, int overlay) {
        builder.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(0, 0).uv2(light, overlay).normal(normalX, normalY, normalZ).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(1, 0).uv2(light, overlay).normal(normalX, normalY, normalZ).endVertex();
        builder.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(1, 1).uv2(light, overlay).normal(normalX, normalY, normalZ).endVertex();
        builder.vertex(matrix, x4, y4, z4).color(r, g, b, a).uv(0, 1).uv2(light, overlay).normal(normalX, normalY, normalZ).endVertex();
    }
}