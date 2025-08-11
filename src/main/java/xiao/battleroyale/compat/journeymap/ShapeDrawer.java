package xiao.battleroyale.compat.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.compat.journeymap.draw.Shape2D;
import xiao.battleroyale.compat.journeymap.draw.Shape3D;

import java.awt.*;
import java.util.List;

public class ShapeDrawer {

    private static final float DEGREE_TO_RADIAN = (float) (Math.PI / 180.0);
    private static float THICKNESS = 2.0F;
    public static void setThickness(float value) { THICKNESS = value; }
    private static int CIRCLE_SEGMENTS = 64;
    private static int ELLIPSE_SEGMENTS = 64;
    public static final float POINTING_POLYGON_ANGLE = (float) (Math.PI / 2.0);
    private static int SPHERE_SEGMENTS = 64;
    private static int ELLIPSOID_SEGMENTS = 64;

    public static ResourceKey<Level> cachedDimension = null;
    public static boolean isCleared = false;

    public static void onMappingStarted(ResourceKey<Level> dimension, IClientAPI jmAPI) {
        cachedDimension = dimension;
        jmAPI.removeAll(JourneyMapPlugin.MOD_ID);

        for (ClientSingleZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            if (zoneData == null || zoneData.center == null || zoneData.dimension == null) continue;

            Color color = new Color(zoneData.r, zoneData.g, zoneData.b, zoneData.a);
            String displayId = "gz_" + zoneData.id; // GameZone
            float rotateDegree = (float) zoneData.rotateDegree; // 正角度为顺时针旋转区域
            double y = zoneData.center.y + zoneData.dimension.y;

            switch (zoneData.shapeType) {
                // 2D shape
                case CIRCLE ->
                        Shape2D.drawPolygonCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, CIRCLE_SEGMENTS, 0, rotateDegree, y, THICKNESS);
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
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, ELLIPSE_SEGMENTS, rotateDegree, y, THICKNESS);
                case STAR -> // 尖顶星形
                        Shape2D.drawStarCylinder(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, zoneData.segments, POINTING_POLYGON_ANGLE, rotateDegree, y, THICKNESS);
                // 3D shape
                case SPHERE ->
                        Shape3D.drawFilledSphere(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, SPHERE_SEGMENTS, rotateDegree, y);
                case CUBE, CUBOID ->
                        Shape3D.drawFilledCuboid(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, rotateDegree, y);
                case ELLIPSOID ->
                        Shape3D.drawFilledEllipsoid(jmAPI, displayId, dimension, color,
                                zoneData.center, (float) zoneData.dimension.x, (float) zoneData.dimension.z, ELLIPSOID_SEGMENTS, rotateDegree, y);
                default -> {}
            }
        }
        isCleared = false;
    }

    /**
     * 在JourneyMap上绘制多边形。
     */
    public static void drawPolygon(IClientAPI jmAPI, String displayId, ResourceKey<Level> dimension, Color color, List<BlockPos> points, float strokeWidth) {
        try {
            ShapeProperties shapeProperties = new ShapeProperties()
                    .setFillOpacity(0.0f) // 默认是0.5，需要覆盖
                    .setStrokeColor(color.getRGB())
                    .setStrokeOpacity(color.getAlpha() / 255.0f)
                    .setStrokeWidth(strokeWidth);

            MapPolygon mapPolygon = new MapPolygon(points);
            PolygonOverlay overlay = new PolygonOverlay(JourneyMapPlugin.MOD_ID, displayId, dimension, shapeProperties, mapPolygon);
            jmAPI.show(overlay);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to draw polygon on JourneyMap: {}", e.getMessage(), e);
        }
    }
    public static void drawFilledPolygon(IClientAPI jmAPI, String displayId, ResourceKey<Level> dimension, Color color, List<BlockPos> points) {
        try {
            ShapeProperties shapeProperties = new ShapeProperties()
                    .setFillColor(color.getRGB())
                    .setFillOpacity(color.getAlpha() / 255.0f)
                    .setStrokeWidth(0); // 默认是2

            MapPolygon mapPolygon = new MapPolygon(points);
            PolygonOverlay overlay = new PolygonOverlay(JourneyMapPlugin.MOD_ID, displayId, dimension, shapeProperties, mapPolygon);
            jmAPI.show(overlay);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to draw filled polygon on JourneyMap: {}", e.getMessage(), e);
        }
    }

    /**
     * 旋转点列表
     */
    public static void rotatePoints(List<BlockPos> points, Vec3 center, float rotateDegree) {
        if (Math.abs(rotateDegree) < 0.001) {
            return;
        }

        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate(-rotateDegree * DEGREE_TO_RADIAN, new Vector3f(0, 1, 0));

        for (int i = 0; i < points.size(); i++) {
            BlockPos p = points.get(i);
            Vector4f vec = new Vector4f((float) (p.getX() - center.x), 0, (float) (p.getZ() - center.z), 1.0f);
            vec.mul(rotationMatrix);
            points.set(i, new BlockPos((int) (vec.x() + center.x), p.getY(), (int) (vec.z() + center.z)));
        }
    }
}