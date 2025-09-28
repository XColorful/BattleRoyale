package xiao.battleroyale.compat.journeymap.draw;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.compat.journeymap.IJmApi;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xiao.battleroyale.compat.journeymap.JMShapeDrawer.drawFilledPolygon;
import static xiao.battleroyale.compat.journeymap.JMShapeDrawer.rotatePoints;

public class Shape3D {

    /**
     * 在JourneyMap上绘制一个填充的球体投影。
     */
    public static void drawFilledSphere(IJmApi jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                        Vec3 center, float radius, int segments, float rotateDegree, double y) {
        List<BlockPos> points = new ArrayList<>();
        float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float angle = i * TWO_PI_DIV_SEGMENTS;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            points.add(new BlockPos((int) x, (int) y, (int) z));
        }

        rotatePoints(points, center, rotateDegree);

        drawFilledPolygon(jmAPI, displayId, dimension, color, points);
    }

    /**
     * 在JourneyMap上绘制一个填充的长方体投影。
     */
    public static void drawFilledCuboid(IJmApi jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                        Vec3 center, float halfWidth, float halfDepth, float rotateDegree, double y) {
        List<BlockPos> points = new ArrayList<>();
        points.add(new BlockPos((int) (center.x - halfWidth), (int) y, (int) (center.z - halfDepth)));
        points.add(new BlockPos((int) (center.x + halfWidth), (int) y, (int) (center.z - halfDepth)));
        points.add(new BlockPos((int) (center.x + halfWidth), (int) y, (int) (center.z + halfDepth)));
        points.add(new BlockPos((int) (center.x - halfWidth), (int) y, (int) (center.z + halfDepth)));

        rotatePoints(points, center, rotateDegree);

        drawFilledPolygon(jmAPI, displayId, dimension, color, points);
    }

    /**
     * 在JourneyMap上绘制一个填充的椭球体投影。
     */
    public static void drawFilledEllipsoid(IJmApi jmAPI, String displayId, ResourceKey<Level> dimension, Color color,
                                           Vec3 center, float halfA, float halfB, int segments, float rotateDegree, double y) {
        List<BlockPos> points = new ArrayList<>();
        float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float angle = i * TWO_PI_DIV_SEGMENTS;
            double x = center.x + halfA * Math.cos(angle);
            double z = center.z + halfB * Math.sin(angle);
            points.add(new BlockPos((int) x, (int) y, (int) z));
        }

        rotatePoints(points, center, rotateDegree);

        drawFilledPolygon(jmAPI, displayId, dimension, color, points);
    }
}