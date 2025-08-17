package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.renderer.CustomRenderType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ZoneRenderer {

    private static class ZoneRendererHolder {
        private static final ZoneRenderer INSTANCE = new ZoneRenderer();
    }

    public static ZoneRenderer get() {
        return ZoneRendererHolder.INSTANCE;
    }

    private ZoneRenderer() {}

    private static boolean registered = false;
    public static boolean isRegistered() { return registered; }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        registered = true;
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        registered = false;
    }

    public static final RenderType TRANSLUCENT_ZONE = CustomRenderType.SolidTranslucentColor;
    public static final RenderType OPAQUE_ZONE = CustomRenderType.SolidOpaqueColor;
    private static int CIRCLE_SEGMENTS = 64;
    private static int ELLIPSE_SEGMENTS = 64;
    public static final float POINTING_POLYGON_ANGLE = (float) (Math.PI / 2.0);
    private static int SPHERE_SEGMENTS = 64;
    private static int ELLIPSOID_SEGMENTS = 64;

    public static int getCircleSegments() { return CIRCLE_SEGMENTS; }
    public static void setCircleSegments(int segments) { CIRCLE_SEGMENTS = Math.max(32, segments); }
    public static int getEllipseSegments() { return ELLIPSE_SEGMENTS; }
    public static void setEllipseSegments(int segments) { ELLIPSE_SEGMENTS = Math.max(32, segments); }
    public static int getSphereSegments() { return SPHERE_SEGMENTS; }
    public static void setSphereSegments(int segments) { SPHERE_SEGMENTS = segments; }
    public static int getEllipsoidSegments() { return ELLIPSOID_SEGMENTS; }
    public static void setEllipsoidSegments(int segments) { ELLIPSOID_SEGMENTS = segments; }

    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS
                || !ClientGameDataManager.get().hasZoneRender()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            BattleRoyale.LOGGER.warn("In ZoneRender, mc.serverLevel == null || mc.player == null");
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera().getPosition();

        for (ClientSingleZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            poseStack.pushPose();
            try {
                // 平移到区域中心，并抵消相机位置
                poseStack.translate(zoneData.center.x - cameraPos.x,
                        zoneData.center.y - cameraPos.y,
                        zoneData.center.z - cameraPos.z);

                // 正角度为顺时针旋转区域
                poseStack.mulPose(Axis.YP.rotationDegrees((float) -zoneData.rotateDegree));

                float r = zoneData.r;
                float g = zoneData.g;
                float b = zoneData.b;
                float a = zoneData.a;

                // 对光影没用，对原版云有用
                VertexConsumer consumer = bufferSource.getBuffer(a < 0.999F ? TRANSLUCENT_ZONE : OPAQUE_ZONE);

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
                    case CUBE, CUBOID ->
                            Shape3D.drawFilledCuboid(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, (float) zoneData.dimension.z);
                    case ELLIPSOID ->
                            Shape3D.drawFilledEllipsoid(poseStack, consumer, r, g, b, a,
                                    (float) zoneData.dimension.x, (float) zoneData.dimension.y, (float) zoneData.dimension.z, ELLIPSOID_SEGMENTS);
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