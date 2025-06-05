package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientZoneData;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ZoneRenderer {

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(BattleRoyale.MOD_ID, "textures/white.png");
    private static final RenderType CUSTOM_ZONE_RENDER_TYPE = createRenderType();

    private static ZoneRenderer instance;

    private ZoneRenderer() {}

    public static ZoneRenderer get() {
        if (instance == null) {
            instance = new ZoneRenderer();
        }
        return instance;
    }

    private static RenderType createRenderType() {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(WHITE_TEXTURE, false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }, RenderSystem::disableBlend))
                .setDepthTestState(new RenderStateShard.DepthTestStateShard("always", 519))
                .setCullState(new RenderStateShard.CullStateShard(false))
                .setLightmapState(new RenderStateShard.LightmapStateShard(false))
                .setOverlayState(new RenderStateShard.OverlayStateShard(false))
                .createCompositeState(true);

        return RenderType.create("zone_render_type",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, true, false,
                compositeState);
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ZoneRenderer.get());
    }

    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            BattleRoyale.LOGGER.warn("In ZoneRender, mc.level == null || mc.player == null");
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera().getPosition();

        for (ClientZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            poseStack.pushPose();
            try {
                poseStack.translate(zoneData.center.x - cameraPos.x,
                        zoneData.center.y - cameraPos.y,
                        zoneData.center.z - cameraPos.z);

                VertexConsumer consumer = bufferSource.getBuffer(CUSTOM_ZONE_RENDER_TYPE);

                float r = zoneData.color.getRed() / 255.0f;
                float g = zoneData.color.getGreen() / 255.0f;
                float b = zoneData.color.getBlue() / 255.0f;
                float a = zoneData.color.getAlpha() / 255.0f;

                switch (zoneData.shapeType) {
                    case CIRCLE ->
                            drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, 64, 0);
                    case SQUARE, RECTANGLE ->
                            drawFilledRectangleBox(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y);
                    case HEXAGON -> // 起始角度为PI/6以实现平顶
                            drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, 6, (float) (Math.PI / 6.0));
                    case POLYGON ->
                            drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, zoneData.segments, zoneData.angle);
                }
            } finally {
                poseStack.popPose();
            }
        }
        bufferSource.endBatch();
    }

    private void drawFilledRectangleBox(PoseStack poseStack, VertexConsumer consumer,
                                        float r, float g, float b, float a,
                                        float halfWidth, float halfDepth, float height) {
        float x1 = -halfWidth;
        float z1 = -halfDepth;
        float x2 = halfWidth;
        float z2 = halfDepth;

        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();

        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();

        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
    }

    private void drawFilledPolygonCylinder(PoseStack poseStack, VertexConsumer consumer,
                                           float r, float g, float b, float a,
                                           float radius, float height, int segments, float initialAngle) {
        for (int i = 0; i < segments; i++) {
            float angle1 = initialAngle + (float) (2 * Math.PI * i / segments);
            float angle2 = initialAngle + (float) (2 * Math.PI * (i + 1) / segments);

            float x1 = radius * Mth.cos(angle1);
            float z1 = radius * Mth.sin(angle1);
            float x2 = radius * Mth.cos(angle2);
            float z2 = radius * Mth.sin(angle2);

            // 计算侧面法线
            // 法线应垂直于该侧面，指向外部
            float midAngle = (angle1 + angle2) / 2.0f;
            float normalX = Mth.cos(midAngle);
            float normalZ = Mth.sin(midAngle);

            consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
        }
    }
}