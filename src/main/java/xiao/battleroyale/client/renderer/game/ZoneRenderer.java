package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
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
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientZoneData;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ZoneRenderer {

    private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation(BattleRoyale.MOD_ID, "textures/white.png");
    private static final RenderType CUSTOM_ZONE_RENDER_TYPE = createRenderType();
    public static final float FLOAT_EPSILON = 1.0E-5F;
    public static final int CIRCLE_SEGMENTS = 64;
    public static final int ELLIPSE_SEGMENTS = 64;

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
                // 平移到区域中心，并抵消相机位置
                poseStack.translate(zoneData.center.x - cameraPos.x,
                        zoneData.center.y - cameraPos.y,
                        zoneData.center.z - cameraPos.z);

                // 正角度为顺时针旋转区域
                poseStack.mulPose(Axis.YP.rotationDegrees((float) -zoneData.rotateDegree));

                VertexConsumer consumer = bufferSource.getBuffer(CUSTOM_ZONE_RENDER_TYPE);

                float r = zoneData.color.getRed() / 255.0f;
                float g = zoneData.color.getGreen() / 255.0f;
                float b = zoneData.color.getBlue() / 255.0f;
                float a = zoneData.color.getAlpha() / 255.0f;

                switch (zoneData.shapeType) {
                    case CIRCLE ->
                            drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, CIRCLE_SEGMENTS, 0);
                    case SQUARE, RECTANGLE ->
                            drawFilledRectangleBox(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y);
                    case HEXAGON -> // 起始角度为PI/6以实现平顶
                            drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, 6, (float) (Math.PI / 6.0));
                    case POLYGON ->
                            drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, zoneData.segments, zoneData.angle);
                    case ELLIPSE ->
                            drawFilledEllipseCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y, ELLIPSE_SEGMENTS);
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

        // 缓存矩阵
        final Matrix4f currentPoseMatrix = poseStack.last().pose();

        // 渲染侧面
        // 前面 (负Z轴方向)
        consumer.vertex(currentPoseMatrix, x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();
        consumer.vertex(currentPoseMatrix, x2, 0, z1).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();
        consumer.vertex(currentPoseMatrix, x2, height, z1).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();
        consumer.vertex(currentPoseMatrix, x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, -1).endVertex();

        // 后面 (正Z轴方向)
        consumer.vertex(currentPoseMatrix, x1, 0, z2).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();
        consumer.vertex(currentPoseMatrix, x1, height, z2).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();
        consumer.vertex(currentPoseMatrix, x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();
        consumer.vertex(currentPoseMatrix, x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(0, 0, 1).endVertex();

        // 左侧 (负X轴方向)
        consumer.vertex(currentPoseMatrix, x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();
        consumer.vertex(currentPoseMatrix, x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();
        consumer.vertex(currentPoseMatrix, x1, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();
        consumer.vertex(currentPoseMatrix, x1, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(-1, 0, 0).endVertex();

        // 右侧 (正X轴方向)
        consumer.vertex(currentPoseMatrix, x2, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
        consumer.vertex(currentPoseMatrix, x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
        consumer.vertex(currentPoseMatrix, x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
        consumer.vertex(currentPoseMatrix, x2, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(1, 0, 0).endVertex();
    }

    private void drawFilledPolygonCylinder(PoseStack poseStack, VertexConsumer consumer,
                                           float r, float g, float b, float a,
                                           float radius, float height, int segments, float initialAngle) {
        // 缓存矩阵，因为它会被重复使用
        final Matrix4f currentPoseMatrix = poseStack.last().pose();
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);
        final float halfTwoPiDivSegments = TWO_PI_DIV_SEGMENTS / 2.0f; // 预计算用于 midAngle

        for (int i = 0; i < segments; i++) {
            float angle1 = initialAngle + (i * TWO_PI_DIV_SEGMENTS);
            float angle2 = initialAngle + ((i + 1) * TWO_PI_DIV_SEGMENTS);

            float cosAngle1 = Mth.cos(angle1);
            float sinAngle1 = Mth.sin(angle1);
            float cosAngle2 = Mth.cos(angle2);
            float sinAngle2 = Mth.sin(angle2);

            float x1 = radius * cosAngle1;
            float z1 = radius * sinAngle1;
            float x2 = radius * cosAngle2;
            float z2 = radius * sinAngle2;

            // 计算侧面法线
            // 法线应垂直于该侧面，指向外部
            float midAngle = initialAngle + (i * TWO_PI_DIV_SEGMENTS) + halfTwoPiDivSegments; // 更精确的 midAngle 计算

            float normalX = Mth.cos(midAngle);
            float normalZ = Mth.sin(midAngle);

            consumer.vertex(currentPoseMatrix, x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(currentPoseMatrix, x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(currentPoseMatrix, x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(currentPoseMatrix, x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
        }
    }

    private void drawFilledEllipseCylinder(PoseStack poseStack, VertexConsumer consumer,
                                           float r, float g, float b, float a,
                                           float halfA, float halfB, float height, int segments) {
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);
        final Matrix4f lastPose = poseStack.last().pose();

        for (int i = 0; i < segments; i++) {
            float angle1 = i * TWO_PI_DIV_SEGMENTS;
            float angle2 = (i + 1) * TWO_PI_DIV_SEGMENTS;

            float cosAngle1 = Mth.cos(angle1);
            float sinAngle1 = Mth.sin(angle1);
            float cosAngle2 = Mth.cos(angle2);
            float sinAngle2 = Mth.sin(angle2);

            float x1 = halfA * cosAngle1;
            float z1 = halfB * sinAngle1;
            float x2 = halfA * cosAngle2;
            float z2 = halfB * sinAngle2;

            float midAngle = angle1 + (TWO_PI_DIV_SEGMENTS / 2.0f); // 优化的 midAngle 计算

            float normalX = halfB * Mth.cos(midAngle);
            float normalZ = halfA * Mth.sin(midAngle);

            float normalLength = Mth.sqrt(normalX * normalX + normalZ * normalZ);
            if (normalLength > FLOAT_EPSILON) { // 避免除以零
                normalX /= normalLength;
                normalZ /= normalLength;
            } else { // 长度接近零，可能是退化的椭圆，给予默认法线
                normalX = 1.0F;
                normalZ = 0.0F;
            }

            consumer.vertex(lastPose, x1, 0, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(lastPose, x1, height, z1).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(lastPose, x2, height, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(lastPose, x2, 0, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normalX, 0, normalZ).endVertex();
        }
    }
}