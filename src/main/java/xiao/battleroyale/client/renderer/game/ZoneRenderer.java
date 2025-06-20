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
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
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
    public static final int CIRCLE_SEGMENTS = 64;
    public static final int ELLIPSE_SEGMENTS = 64;
    public static final float POINTING_POLYGON_ANGLE = (float) (Math.PI / 2.0);
    public static final int SPHERE_SEGMENTS = 64;

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
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS
                || !ClientGameDataManager.get().hasZoneRender()) {
            return;
        }

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
                    // 2D shape
                    case CIRCLE ->
                            Shape2D.drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, CIRCLE_SEGMENTS, 0);
                    case SQUARE, RECTANGLE ->
                            Shape2D.drawFilledRectangleBox(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y);
                    case HEXAGON -> // 平顶正六边形
                            Shape2D.drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, 6, 0);
                    case POLYGON -> // 尖顶正多边形
                            Shape2D.drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, zoneData.segments, POINTING_POLYGON_ANGLE);
                    case ELLIPSE ->
                            Shape2D.drawFilledEllipseCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y, ELLIPSE_SEGMENTS);
                    case STAR -> // 尖顶星形
                            Shape2D.drawFilledStarCylinder(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y, zoneData.segments, POINTING_POLYGON_ANGLE);
                    // 3D shape
                    case SPHERE ->
                            Shape3D.drawFilledSphere(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.y, SPHERE_SEGMENTS);
                    default -> {
                        ;
                    }
                }
            } finally {
                poseStack.popPose();
            }
        }
        bufferSource.endBatch();
    }
}