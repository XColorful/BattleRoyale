package xiao.battleroyale.compat.journeymap.draw;

import journeymap.client.api.IClientAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xiao.battleroyale.compat.journeymap.ShapeDrawer.drawPolygon;
import static xiao.battleroyale.compat.journeymap.ShapeDrawer.rotatePoints;

public class Shape2D {

    /**
     * 在JourneyMap上绘制一个带描边的矩形。
     */
    public static void drawRectangleBox(IClientAPI jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                        Vec3 center, float halfWidth, float halfDepth, float rotateDegree, double y, float thickness) {
        List<BlockPos> points = new ArrayList<>();
        points.add(new BlockPos((int) (center.x - halfWidth), (int) y, (int) (center.z - halfDepth)));
        points.add(new BlockPos((int) (center.x + halfWidth), (int) y, (int) (center.z - halfDepth)));
        points.add(new BlockPos((int) (center.x + halfWidth), (int) y, (int) (center.z + halfDepth)));
        points.add(new BlockPos((int) (center.x - halfWidth), (int) y, (int) (center.z + halfDepth)));

        rotatePoints(points, center, rotateDegree);

        drawPolygon(jmAPI, displayId, dimension, color, points, thickness);
    }

    /**
     * 在JourneyMap上绘制一个带描边的多边形。
     */
    public static void drawPolygonCylinder(IClientAPI jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                           Vec3 center, float radius, int segments, float initialAngle, float rotateDegree, double y, float thickness) {
        List<BlockPos> points = new ArrayList<>();
        float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float angle = initialAngle + (i * TWO_PI_DIV_SEGMENTS);
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            points.add(new BlockPos((int) x, (int) y, (int) z));
        }

        rotatePoints(points, center, rotateDegree);

        drawPolygon(jmAPI, displayId, dimension, color, points, thickness);
    }

    /**
     * 在JourneyMap上绘制一个带描边的椭圆。
     */
    public static void drawEllipseCylinder(IClientAPI jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                           Vec3 center, float halfA, float halfB, int segments, float rotateDegree, double y, float thickness) {
        List<BlockPos> points = new ArrayList<>();
        float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float angle = i * TWO_PI_DIV_SEGMENTS;
            double x = center.x + halfA * Math.cos(angle);
            double z = center.z + halfB * Math.sin(angle);
            points.add(new BlockPos((int) x, (int) y, (int) z));
        }

        rotatePoints(points, center, rotateDegree);

        drawPolygon(jmAPI, displayId, dimension, color, points, thickness);
    }

    /**
     * 在JourneyMap上绘制一个带描边的星形。
     */
    public static void drawStarCylinder(IClientAPI jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                        Vec3 center, float outerRadius, float innerRadius, int segments, float initialAngle, float rotateDegree, double y, float thickness) {
        List<BlockPos> points = new ArrayList<>();
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);
        final float PI_DIV_SEGMENTS = (float) (Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float outerAngle = initialAngle + (i * TWO_PI_DIV_SEGMENTS);
            float innerAngle = initialAngle + (i * TWO_PI_DIV_SEGMENTS) + PI_DIV_SEGMENTS;

            double outerX = center.x + outerRadius * Math.cos(outerAngle);
            double outerZ = center.z + outerRadius * Math.sin(outerAngle);
            points.add(new BlockPos((int) outerX, (int) y, (int) outerZ));

            double innerX = center.x + innerRadius * Math.cos(innerAngle);
            double innerZ = center.z + innerRadius * Math.sin(innerAngle);
            points.add(new BlockPos((int) innerX, (int) y, (int) innerZ));
        }

        rotatePoints(points, center, rotateDegree);

        drawPolygon(jmAPI, displayId, dimension, color, points, thickness);
    }
}