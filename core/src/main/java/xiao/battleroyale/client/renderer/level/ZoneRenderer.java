package xiao.battleroyale.client.renderer.level;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.event.RenderLevelStage;
import xiao.battleroyale.api.client.game.sub.IClientZoneDataManager;
import xiao.battleroyale.api.client.render.level.IClientZoneRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.renderer.CustomRenderType;
import xiao.battleroyale.util.Vec3Utils;

public class ZoneRenderer implements IClientZoneRenderer, IEventHandler {

    private static class ZoneRendererHolder {
        private static final ZoneRenderer INSTANCE = new ZoneRenderer();
    }

    public static ZoneRenderer get() {
        return ZoneRendererHolder.INSTANCE;
    }

    protected ZoneRenderer() {}

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            BattleRoyale.LOGGER.debug("ZoneRenderer skipped init() at {}", mcSide.toString());
            return;
        }
    }

    public static final RenderType TRANSLUCENT_ZONE = CustomRenderType.SolidTranslucentColor;
    public static final RenderType OPAQUE_ZONE = CustomRenderType.SolidOpaqueColor;
    private int CIRCLE_SEGMENTS = 64;
    private int ELLIPSE_SEGMENTS = 64;
    public static final float POINTING_POLYGON_ANGLE = (float) (Math.PI / 2.0);
    private int SPHERE_SEGMENTS = 64;
    private int ELLIPSOID_SEGMENTS = 64;

    public int getCircleSegments() { return CIRCLE_SEGMENTS; }
    public void setCircleSegments(int segments) { CIRCLE_SEGMENTS = Math.max(32, segments); }
    public int getEllipseSegments() { return ELLIPSE_SEGMENTS; }
    public void setEllipseSegments(int segments) { ELLIPSE_SEGMENTS = Math.max(32, segments); }
    public int getSphereSegments() { return SPHERE_SEGMENTS; }
    public void setSphereSegments(int segments) { SPHERE_SEGMENTS = segments; }
    public int getEllipsoidSegments() { return ELLIPSOID_SEGMENTS; }
    public void setEllipsoidSegments(int segments) { ELLIPSOID_SEGMENTS = segments; }

    private @Nullable Matrix4f currentZoneMatrix;
    private @Nullable VertexConsumer consumer;
    public @Nullable Matrix4f getCurrentZoneMatrix() {
        return currentZoneMatrix;
    }
    public @Nullable VertexConsumer getVertexConsumer() {
        return consumer;
    }

    public String getRendererName() {
        return String.format("%s:ZoneRenderer", BattleRoyale.MOD_ID);
    }

    @Override
    public boolean registerRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), EventType.RENDER_TRANSLUCENT_EVENT, EventPriority.HIGH, false);
        return true;
    }

    @Override
    public boolean unregisterRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), EventType.RENDER_TRANSLUCENT_EVENT, EventPriority.HIGH, false);
        return true;
    }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:ZoneRenderer", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.RENDER_TRANSLUCENT_EVENT) {
            onAfterTranslucentBlocks((IRenderLevelStageEvent) event);
        } else {
            onReceiveWrongEvent(eventType);
        }
    }

    public void onRenderLevelStage(IRenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        onAfterTranslucentBlocks(event);
    }

    public void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {
        IClientZoneDataManager clientZoneDataManager = BattleRoyale.getClientGameDataManager();
        if (!clientZoneDataManager.hasClientZone()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            BattleRoyale.LOGGER.warn("In ZoneRender, mc.serverLevel == null || mc.player == null");
            return;
        }

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera_getPosition();

        float partialTick = event.getPartialTick();
        for (ClientSingleZoneData zoneData : clientZoneDataManager.getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            Vec3 centerOld = zoneData.centerOld; Vec3 center = zoneData.center;
            double centerX = Mth.lerp(partialTick, centerOld.x, center.x);
            double centerY = Mth.lerp(partialTick, centerOld.y, center.y);
            double centerZ = Mth.lerp(partialTick, centerOld.z, center.z);
            Vec3 dimensionOld = zoneData.dimensionOld; Vec3 dimension = zoneData.dimension;
            double dimX = Mth.lerp(partialTick, dimensionOld.x, dimension.x);
            double dimY = Mth.lerp(partialTick, dimensionOld.y, dimension.y);
            double dimZ = Mth.lerp(partialTick, dimensionOld.z, dimension.z);
            double rotateDegree = Mth.lerp(partialTick, zoneData.rotateDegreeOld, zoneData.rotateDegree);

            currentZoneMatrix = createCenterOffsetMatrix(event, centerX, centerY, centerZ, cameraPos);

            // 正角度为顺时针旋转区域
            currentZoneMatrix.rotate(Axis.YP.rotationDegrees((float) -rotateDegree));

            float r = zoneData.r;
            float g = zoneData.g;
            float b = zoneData.b;
            float a = zoneData.a;

            // 对光影没用，对原版云有用
            consumer = bufferSource.getBuffer(a < 0.999F ? TRANSLUCENT_ZONE : OPAQUE_ZONE);

            switch (zoneData.shapeType) {
                // 2D shape
                case CIRCLE ->
                        Shape2D.drawFilledPolygonCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimY, CIRCLE_SEGMENTS, 0);
                case SQUARE, RECTANGLE ->
                        Shape2D.drawFilledRectangleBox(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimZ, (float) dimY);
                case HEXAGON -> // 平顶正六边形
                        Shape2D.drawFilledPolygonCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimY, 6, 0);
                case POLYGON -> // 尖顶正多边形
                        Shape2D.drawFilledPolygonCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimY, zoneData.segments, POINTING_POLYGON_ANGLE);
                case ELLIPSE ->
                        Shape2D.drawFilledEllipseCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimZ, (float) dimY, ELLIPSE_SEGMENTS);
                case STAR -> // 尖顶星形
                        Shape2D.drawFilledStarCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimZ, (float) dimY, zoneData.segments, POINTING_POLYGON_ANGLE);
                case CROSS -> // 十字形
                        Shape2D.drawFilledCrossCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimZ, (float) dimY);
                case RING -> // 环形
                        Shape2D.drawFilledRingCylinder(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimZ, (float) dimY, CIRCLE_SEGMENTS, 0);
                // 3D shape
                case SPHERE ->
                        Shape3D.drawFilledSphere(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimY, SPHERE_SEGMENTS);
                case CUBE, CUBOID ->
                        Shape3D.drawFilledCuboid(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimY, (float) dimZ);
                case ELLIPSOID ->
                        Shape3D.drawFilledEllipsoid(currentZoneMatrix, consumer, r, g, b, a,
                                (float) dimX, (float) dimY, (float) dimZ, ELLIPSOID_SEGMENTS);
                default -> {
                    ;
                }
            }
            if (zoneData.specialHandler != null) {
                zoneData.specialHandler.additionalZoneRender(event, this, zoneData);
            }

            this.currentZoneMatrix = null;
            this.consumer = null;
        }
        bufferSource.endBatch();
    }

    /**
     * 创建一个新的模型视图矩阵
     * 该矩阵基于 baseModelView, 并应用了平移到指定世界中心点的变换 (同时抵消了相机偏移)
     */
    @Deprecated(forRemoval = false)
    public static Matrix4f createCenterOffsetMatrix(Matrix4f baseModelView, Vec3 worldCenter, Vec3 cameraPos) {
        return createCenterOffsetMatrix(baseModelView, worldCenter.x, worldCenter.y, worldCenter.z, cameraPos);
    }
    public static Matrix4f createCenterOffsetMatrix(Matrix4f baseModelView, double centerX, double centerY, double centerZ, Vec3 cameraPos) {
        Matrix4f matrix = baseModelView != null ? new Matrix4f(baseModelView) : new Matrix4f();
        // 平移到目标中心点，并抵消相机位置
        matrix.translate(
                (float) (centerX - cameraPos.x()),
                (float) (centerY - cameraPos.y()),
                (float) (centerZ - cameraPos.z()));
        return matrix;
    }

    /**
     * MC各版本通用
     */
    @Deprecated(forRemoval = false)
    public static Matrix4f createCenterOffsetMatrix(IRenderLevelStageEvent event, Vec3 worldCenter, Vec3 cameraPos) {
        return createCenterOffsetMatrix(new Matrix4f(), worldCenter.x, worldCenter.y, worldCenter.z, cameraPos);
    }
    public static Matrix4f createCenterOffsetMatrix(IRenderLevelStageEvent event, double centerX, double centerY, double centerZ, Vec3 cameraPos) {
        return createCenterOffsetMatrix(new Matrix4f(), centerX, centerY, centerZ, cameraPos);
    }
}