package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.renderer.CustomRenderType;

public class ZoneRenderer {

    private static class ZoneRendererHolder {
        private static final ZoneRenderer INSTANCE = new ZoneRenderer();
    }

    public static ZoneRenderer get() {
        return ZoneRendererHolder.INSTANCE;
    }

    private ZoneRenderer() {}

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

    public void onRenderLevelStage(IRenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        onAfterTranslucentBlocks(event);
    }

    public void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {
        if (!ClientGameDataManager.get().hasZoneRender()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            BattleRoyale.LOGGER.warn("In ZoneRender, mc.serverLevel == null || mc.player == null");
            return;
        }

        Matrix4f baseModelView = event.getModelViewMatrix();
        BattleRoyale.LOGGER.debug("BaseModelView:{}", baseModelView);
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera_getPosition();

        for (ClientSingleZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            Matrix4f modelMatrix = new Matrix4f();
            // 平移到区域中心，并抵消相机位置
            modelMatrix.translate(
                    (float) (zoneData.center.x - cameraPos.x),
                    (float) (zoneData.center.y - cameraPos.y),
                    (float) (zoneData.center.z - cameraPos.z));
            BattleRoyale.LOGGER.debug("Center:{}, {}, {}, CameraPos:{}, {}, {}", zoneData.center.x, zoneData.center.y, zoneData.center.z, cameraPos.x, cameraPos.y, cameraPos.z);
            BattleRoyale.LOGGER.debug("ModelMatrix:{}", modelMatrix);
            // 正角度为顺时针旋转区域
            modelMatrix.rotate(Axis.YP.rotationDegrees((float) -zoneData.rotateDegree));
            BattleRoyale.LOGGER.debug("ModelMatrix:{}", modelMatrix);
            Matrix4f finalMatrix = new Matrix4f(baseModelView);
            finalMatrix.mul(modelMatrix);
            float r = zoneData.r;
            float g = zoneData.g;
            float b = zoneData.b;
            float a = zoneData.a;

            // 对光影没用，对原版云有用
            VertexConsumer consumer = bufferSource.getBuffer(a < 0.999F ? TRANSLUCENT_ZONE : OPAQUE_ZONE);

            BattleRoyale.LOGGER.debug("FinalMatrix:{}", finalMatrix);
            switch (zoneData.shapeType) {
                // 2D shape
                case CIRCLE ->
                        Shape2D.drawFilledPolygonCylinder(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.y, CIRCLE_SEGMENTS, 0);
                case SQUARE, RECTANGLE ->
                        Shape2D.drawFilledRectangleBox(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y);
                case HEXAGON -> // 平顶正六边形
                        Shape2D.drawFilledPolygonCylinder(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.y, 6, 0);
                case POLYGON -> // 尖顶正多边形
                        Shape2D.drawFilledPolygonCylinder(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.y, zoneData.segments, POINTING_POLYGON_ANGLE);
                case ELLIPSE ->
                        Shape2D.drawFilledEllipseCylinder(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y, ELLIPSE_SEGMENTS);
                case STAR -> // 尖顶星形
                        Shape2D.drawFilledStarCylinder(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y, zoneData.segments, POINTING_POLYGON_ANGLE);
                // 3D shape
                case SPHERE ->
                        Shape3D.drawFilledSphere(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.y, SPHERE_SEGMENTS);
                case CUBE, CUBOID ->
                        Shape3D.drawFilledCuboid(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.y, (float) zoneData.dimension.z);
                case ELLIPSOID ->
                        Shape3D.drawFilledEllipsoid(finalMatrix, consumer, r, g, b, a,
                                (float) zoneData.dimension.x, (float) zoneData.dimension.y, (float) zoneData.dimension.z, ELLIPSOID_SEGMENTS);
                default -> {
                    ;
                }
            }
        }
        bufferSource.endBatch();
    }
}