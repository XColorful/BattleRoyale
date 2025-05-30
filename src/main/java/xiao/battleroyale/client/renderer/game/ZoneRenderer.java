package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientZoneData;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ZoneRenderer {

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation("minecraft", "textures/white.png");

    public static void register() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ZoneRenderer());
    }

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // 绑定纯白纹理，确保渲染时不采样到块图集中的其他材质
        RenderSystem.setShaderTexture(0, WHITE_TEXTURE);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera().getPosition();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (ClientZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            poseStack.pushPose();
            try {
                poseStack.translate(zoneData.center.x - cameraPos.x,
                        zoneData.center.y - cameraPos.y,
                        zoneData.center.z - cameraPos.z);

                VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

                float r = zoneData.color.getRed() / 255.0f;
                float g = zoneData.color.getGreen() / 255.0f;
                float b = zoneData.color.getBlue() / 255.0f;
                float a = zoneData.color.getAlpha() / 255.0f;

                switch (zoneData.shapeType) {
                    case CIRCLE ->
                            drawFilledCircleCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y);
                    case SQUARE, RECTANGLE ->
                            drawFilledRectangleBox(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x,
                                    (float) zoneData.dimension.z,
                                    (float) zoneData.dimension.y);
                }
            } finally {
                poseStack.popPose();
            }
        }

        bufferSource.endBatch();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawFilledCircleCylinder(PoseStack poseStack, VertexConsumer consumer,
                                          float r, float g, float b, float a, float radius, float height) {
        int segments = 64;
        float startAngle = 0;
        float endAngle = (float) (2 * Math.PI);

        for (int i = 0; i < segments; i++) {
            float angle1 = startAngle + (endAngle - startAngle) * i / segments;
            float angle2 = startAngle + (endAngle - startAngle) * (i + 1) / segments;

            float x1 = radius * Mth.cos(angle1);
            float z1 = radius * Mth.sin(angle1);
            float x2 = radius * Mth.cos(angle2);
            float z2 = radius * Mth.sin(angle2);

            float normalX1 = Mth.cos(angle1);
            float normalZ1 = Mth.sin(angle1);
            float normalX2 = Mth.cos(angle2);
            float normalZ2 = Mth.sin(angle2);

            // 侧面四边形，补全所有顶点属性（uv, overlay, uv2, normal）
            consumer.vertex(poseStack.last().pose(), x1, 0, z1)
                    .color(r, g, b, a)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                    .uv2(7864440)
                    .normal(normalX1, 0, normalZ1)
                    .endVertex();
            consumer.vertex(poseStack.last().pose(), x1, height, z1)
                    .color(r, g, b, a)
                    .uv(0, 1)
                    .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                    .uv2(7864440)
                    .normal(normalX1, 0, normalZ1)
                    .endVertex();
            consumer.vertex(poseStack.last().pose(), x2, height, z2)
                    .color(r, g, b, a)
                    .uv(1, 1)
                    .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                    .uv2(7864440)
                    .normal(normalX2, 0, normalZ2)
                    .endVertex();
            consumer.vertex(poseStack.last().pose(), x2, 0, z2)
                    .color(r, g, b, a)
                    .uv(1, 0)
                    .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                    .uv2(7864440)
                    .normal(normalX2, 0, normalZ2)
                    .endVertex();
        }
    }

    private void drawFilledRectangleBox(PoseStack poseStack, VertexConsumer consumer,
                                        float r, float g, float b, float a,
                                        float halfWidth, float halfDepth, float height) {
        float x1 = -halfWidth;
        float z1 = -halfDepth;
        float x2 = halfWidth;
        float z2 = halfDepth;

        // 绘制前面（法向量：0, 0, -1）
        consumer.vertex(poseStack.last().pose(), x1, 0, z1)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, -1)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z1)
                .color(r, g, b, a)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, -1)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1)
                .color(r, g, b, a)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, -1)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1)
                .color(r, g, b, a)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, -1)
                .endVertex();

        // 绘制后面（法向量：0, 0, 1）
        consumer.vertex(poseStack.last().pose(), x1, 0, z2)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, 1)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2)
                .color(r, g, b, a)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, 1)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2)
                .color(r, g, b, a)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, 1)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2)
                .color(r, g, b, a)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(0, 0, 1)
                .endVertex();

        // 绘制左面（法向量：-1, 0, 0）
        consumer.vertex(poseStack.last().pose(), x1, 0, z1)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(-1, 0, 0)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1)
                .color(r, g, b, a)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(-1, 0, 0)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2)
                .color(r, g, b, a)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(-1, 0, 0)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x1, 0, z2)
                .color(r, g, b, a)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(-1, 0, 0)
                .endVertex();

        // 绘制右面（法向量：1, 0, 0）
        consumer.vertex(poseStack.last().pose(), x2, 0, z1)
                .color(r, g, b, a)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(1, 0, 0)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2)
                .color(r, g, b, a)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(1, 0, 0)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2)
                .color(r, g, b, a)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(1, 0, 0)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1)
                .color(r, g, b, a)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.WHITE_OVERLAY_V)
                .uv2(7864440)
                .normal(1, 0, 0)
                .endVertex();
    }
}