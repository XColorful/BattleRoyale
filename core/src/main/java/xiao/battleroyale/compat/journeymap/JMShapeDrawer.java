package xiao.battleroyale.compat.journeymap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.render.level.IClientZoneRenderer;
import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.compat.journeymap.draw.Shape2D;
import xiao.battleroyale.compat.journeymap.draw.Shape3D;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class JMShapeDrawer {

    private static final float DEGREE_TO_RADIAN = (float) (Math.PI / 180.0);
    private static float THICKNESS = 4.0F;
    public static void setThickness(float value) {
        THICKNESS = value;
    }
    public static final float POINTING_POLYGON_ANGLE = (float) (Math.PI / 2.0);

    public static ResourceKey<Level> cachedDimension = null;
    public static boolean isCleared = false;

    public static void onMappingStarted(IJmApi jmAPI, ResourceKey<Level> dimension) {
        cachedDimension = dimension;
        jmAPI.removeAll(JMEventHandler.MOD_JM_ID);

        for (ClientSingleZoneData zoneData : BattleRoyale.getClientGameDataManager().getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            Color color = new Color(zoneData.r, zoneData.g, zoneData.b, zoneData.a);
            String displayId = "gz_" + zoneData.id; // GameZone
            float rotateDegree = (float) zoneData.rotateDegree; // 正角度为顺时针旋转区域
            double y = zoneData.center.y + zoneData.dimension.y;

            IClientZoneRenderer zoneRenderer = BattleRoyale.getClientRenderer().getClientZoneRenderer();
            switch (zoneData.shapeType) {
                // 2D shape
                case CIRCLE ->
                        Shape2D.drawPolygonCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, zoneRenderer.getCircleSegments(), 0, rotateDegree, y, THICKNESS);
                case SQUARE ->
                        Shape2D.drawRectangleBox(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.x, rotateDegree, y, THICKNESS);
                case RECTANGLE ->
                        Shape2D.drawRectangleBox(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, rotateDegree, y, THICKNESS);
                case HEXAGON -> // 平顶正六边形
                        Shape2D.drawPolygonCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, 6, 0, rotateDegree, y, THICKNESS);
                case POLYGON -> // 尖顶正多边形
                        Shape2D.drawPolygonCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, zoneData.segments, POINTING_POLYGON_ANGLE, rotateDegree, y, THICKNESS);
                case ELLIPSE ->
                        Shape2D.drawEllipseCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, zoneRenderer.getEllipseSegments(), rotateDegree, y, THICKNESS);
                case STAR -> // 尖顶星形
                        Shape2D.drawStarCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, zoneData.segments, POINTING_POLYGON_ANGLE, rotateDegree, y, THICKNESS);
                case CROSS -> // 十字形
                        Shape2D.drawCrossCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, rotateDegree, y, THICKNESS);
                case RING -> // 环形
                        Shape2D.drawRingCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, zoneRenderer.getCircleSegments(), 0, rotateDegree, y, THICKNESS);
                // 3D shape
                case SPHERE ->
                        Shape3D.drawFilledSphere(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, zoneRenderer.getSphereSegments(), rotateDegree, y);
                case CUBE, CUBOID ->
                        Shape3D.drawFilledCuboid(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, rotateDegree, y);
                case ELLIPSOID ->
                        Shape3D.drawFilledEllipsoid(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, zoneRenderer.getEllipsoidSegments(), rotateDegree, y);
                default -> {}
            }
        }
        isCleared = false;
    }

    /**
     * 在JourneyMap上绘制多边形。
     */
    public static void drawPolygon(IJmApi jmAPI, String displayId, ResourceKey<Level> dimension, Color color, List<Vec3> points, float strokeWidth) {
        try {
            List<BlockPos> blockPoints = points.stream().map(BlockPos::containing).collect(Collectors.toList());

            JMShapeProperties JMShapeProperties = new JMShapeProperties(0,
                    0.0F, // 默认是0.5，需要覆盖
                    color.getRGB(),
                    color.getAlpha() / 255.0F,
                    strokeWidth);

            JMMapPolygon JMMapPolygon = new JMMapPolygon(blockPoints);
            JMPolygonOverlay overlay = new JMPolygonOverlay(JMEventHandler.MOD_JM_ID, displayId, dimension, JMShapeProperties, JMMapPolygon);
            jmAPI.show(overlay);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to draw polygon on JourneyMap: {}", e.getMessage(), e);
        }
    }

    public static void drawFilledPolygon(IJmApi jmAPI, String displayId, ResourceKey<Level> dimension, Color color, List<Vec3> points) {
        try {
            List<BlockPos> blockPoints = points.stream().map(BlockPos::containing).collect(Collectors.toList());

            JMShapeProperties JMShapeProperties = new JMShapeProperties(color.getRGB(),
                    color.getAlpha() / 255.0F,
                    0,
                    0,
                    0); // 默认是2

            JMMapPolygon JMMapPolygon = new JMMapPolygon(blockPoints);
            JMPolygonOverlay overlay = new JMPolygonOverlay(JMEventHandler.MOD_JM_ID, displayId, dimension, JMShapeProperties, JMMapPolygon);
            jmAPI.show(overlay);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to draw filled polygon on JourneyMap: {}", e.getMessage(), e);
        }
    }

    /**
     * 旋转点列表
     */
    public static void rotatePoints(List<Vec3> points, Vec3 center, float rotateDegree) {
        if (Math.abs(rotateDegree) < 0.001) {
            return;
        }

        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate(-rotateDegree * DEGREE_TO_RADIAN, new Vector3f(0, 1, 0));

        for (int i = 0; i < points.size(); i++) {
            Vec3 p = points.get(i);
            Vector4f vec = new Vector4f((float) (p.x - center.x), 0, (float) (p.z - center.z), 1.0f);
            vec.mul(rotationMatrix);
            points.set(i, new Vec3(vec.x() + center.x, p.y, vec.z() + center.z));
        }
    }
}