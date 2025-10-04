package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class Shape2D {

    public static final float FLOAT_EPSILON = 1.0E-5F;

    /**
     * 绘制一个以模型原点为中心的填充矩形盒子。
     * 盒子在Y轴上具有高度，底面位于Y=0平面。
     * @param halfWidth 矩形在X轴方向上的半宽。
     * @param halfDepth 矩形在Z轴方向上的半深。
     * @param height 矩形在Y轴方向上的高度。
     */
    public static void drawFilledRectangleBox(PoseStack poseStack, VertexConsumer consumer,
                                              float r, float g, float b, float a,
                                              float halfWidth, float halfDepth, float height) {
        float x1 = -halfWidth;
        float z1 = -halfDepth;
        float x2 = halfWidth;
        float z2 = halfDepth;

        // 缓存矩阵
        final PoseStack.Pose currentPose = poseStack.last();

        // 渲染侧面
        // 前面 (负Z轴方向)
        consumer.addVertex(currentPose, x1, 0.0f, z1).setColor(r, g, b, a).setNormal(0.0f, 0.0f, -1.0f);
        consumer.addVertex(currentPose, x2, 0.0f, z1).setColor(r, g, b, a).setNormal(0.0f, 0.0f, -1.0f);
        consumer.addVertex(currentPose, x2, height, z1).setColor(r, g, b, a).setNormal(0.0f, 0.0f, -1.0f);
        consumer.addVertex(currentPose, x1, height, z1).setColor(r, g, b, a).setNormal(0.0f, 0.0f, -1.0f);

        // 后面 (正Z轴方向)
        consumer.addVertex(currentPose, x1, 0.0f, z2).setColor(r, g, b, a).setNormal(0.0f, 0.0f, 1.0f);
        consumer.addVertex(currentPose, x1, height, z2).setColor(r, g, b, a).setNormal(0.0f, 0.0f, 1.0f);
        consumer.addVertex(currentPose, x2, height, z2).setColor(r, g, b, a).setNormal(0.0f, 0.0f, 1.0f);
        consumer.addVertex(currentPose, x2, 0.0f, z2).setColor(r, g, b, a).setNormal(0.0f, 0.0f, 1.0f);

        // 左侧 (负X轴方向)
        consumer.addVertex(currentPose, x1, 0.0f, z1).setColor(r, g, b, a).setNormal(-1.0f, 0.0f, 0.0f);
        consumer.addVertex(currentPose, x1, height, z1).setColor(r, g, b, a).setNormal(-1.0f, 0.0f, 0.0f);
        consumer.addVertex(currentPose, x1, height, z2).setColor(r, g, b, a).setNormal(-1.0f, 0.0f, 0.0f);
        consumer.addVertex(currentPose, x1, 0.0f, z2).setColor(r, g, b, a).setNormal(-1.0f, 0.0f, 0.0f);

        // 右侧 (正X轴方向)
        consumer.addVertex(currentPose, x2, 0.0f, z1).setColor(r, g, b, a).setNormal(1.0f, 0.0f, 0.0f);
        consumer.addVertex(currentPose, x2, 0.0f, z2).setColor(r, g, b, a).setNormal(1.0f, 0.0f, 0.0f);
        consumer.addVertex(currentPose, x2, height, z2).setColor(r, g, b, a).setNormal(1.0f, 0.0f, 0.0f);
        consumer.addVertex(currentPose, x2, height, z1).setColor(r, g, b, a).setNormal(1.0f, 0.0f, 0.0f);
    }

    /**
     * 绘制一个以模型原点为中心的填充多边形柱体。
     * 柱体的底面是一个正多边形，位于Y=0平面，向上延伸至指定高度。
     * @param radius 多边形的外接圆半径。
     * @param height 柱体的高度。
     * @param segments 多边形的边数（或圆的近似段数），值越大越平滑。
     * @param initialAngle 初始旋转角度（弧度），用于调整多边形的朝向。
     */
    public static void drawFilledPolygonCylinder(PoseStack poseStack, VertexConsumer consumer,
                                                 float r, float g, float b, float a,
                                                 float radius, float height, int segments, float initialAngle) {
        // 缓存矩阵
        final PoseStack.Pose currentPose = poseStack.last();
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);
        final float halfTwoPiDivSegments = TWO_PI_DIV_SEGMENTS / 2.0f;

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

            // 侧面法线
            float midAngle = initialAngle + (i * TWO_PI_DIV_SEGMENTS) + halfTwoPiDivSegments;

            float normalX = Mth.cos(midAngle);
            float normalZ = Mth.sin(midAngle);

            consumer.addVertex(currentPose, x1, 0.0f, z1).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
            consumer.addVertex(currentPose, x1, height, z1).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
            consumer.addVertex(currentPose, x2, height, z2).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
            consumer.addVertex(currentPose, x2, 0.0f, z2).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
        }
    }

    /**
     * 绘制一个以模型原点为中心的填充椭圆柱体。
     * 柱体的底面是一个椭圆，位于Y=0平面，向上延伸至指定高度。
     * @param halfA 椭圆在X轴方向上的半轴长。
     * @param halfB 椭圆在Z轴方向上的半轴长。
     * @param height 柱体的高度。
     * @param segments 椭圆的近似段数，值越大越平滑。
     */
    public static void drawFilledEllipseCylinder(PoseStack poseStack, VertexConsumer consumer,
                                                 float r, float g, float b, float a,
                                                 float halfA, float halfB, float height, int segments) {
        // 缓存矩阵
        final PoseStack.Pose currentPose = poseStack.last();
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

            consumer.addVertex(currentPose, x1, 0.0f, z1).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
            consumer.addVertex(currentPose, x1, height, z1).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
            consumer.addVertex(currentPose, x2, height, z2).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
            consumer.addVertex(currentPose, x2, 0.0f, z2).setColor(r, g, b, a).setNormal(normalX, 0.0f, normalZ);
        }
    }

    /**
     * 绘制一个以模型原点为中心的填充星形柱体。
     * 柱体的底面是一个星形，位于Y=0平面，向上延伸至指定高度。
     * @param outerRadius 星形外顶点到中心的距离。
     * @param innerRadius 星形内凹点到中心的距离。
     * @param height 柱体的高度。
     * @param segments 星形的瓣数（通常为5或更多），一个瓣由两条边组成。
     * @param initialAngle 初始旋转角度（弧度），用于调整星形的朝向。
     */
    public static void drawFilledStarCylinder(PoseStack poseStack, VertexConsumer consumer,
                                              float r, float g, float b, float a,
                                              float outerRadius, float innerRadius, float height, int segments, float initialAngle) {
        final PoseStack.Pose currentPose = poseStack.last();
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);

        // 星形侧面渲染
        for (int i = 0; i < segments; i++) {
            // 外瓣边
            float outerAngle1 = initialAngle + (i * TWO_PI_DIV_SEGMENTS);
            float innerAngle1 = initialAngle + (i * TWO_PI_DIV_SEGMENTS) + (float) (Math.PI / segments);

            float outerX1 = outerRadius * Mth.cos(outerAngle1);
            float outerZ1 = outerRadius * Mth.sin(outerAngle1);
            float innerX1 = innerRadius * Mth.cos(innerAngle1);
            float innerZ1 = innerRadius * Mth.sin(innerAngle1);

            // 外瓣边法线
            float midAngleOuterInner1 = (outerAngle1 + innerAngle1) / 2.0f;
            float normalX1 = Mth.cos(midAngleOuterInner1);
            float normalZ1 = Mth.sin(midAngleOuterInner1);

            consumer.addVertex(currentPose, outerX1, 0.0f, outerZ1).setColor(r, g, b, a).setNormal(normalX1, 0.0f, normalZ1);
            consumer.addVertex(currentPose, outerX1, height, outerZ1).setColor(r, g, b, a).setNormal(normalX1, 0.0f, normalZ1);
            consumer.addVertex(currentPose, innerX1, height, innerZ1).setColor(r, g, b, a).setNormal(normalX1, 0.0f, normalZ1);
            consumer.addVertex(currentPose, innerX1, 0.0f, innerZ1).setColor(r, g, b, a).setNormal(normalX1, 0.0f, normalZ1);

            // 内瓣边
            float innerAngle2 = initialAngle + (i * TWO_PI_DIV_SEGMENTS) + (float) (Math.PI / segments);
            float outerAngle2 = initialAngle + ((i + 1) * TWO_PI_DIV_SEGMENTS);

            float innerX2 = innerRadius * Mth.cos(innerAngle2);
            float innerZ2 = innerRadius * Mth.sin(innerAngle2);
            float outerX2 = outerRadius * Mth.cos(outerAngle2);
            float outerZ2 = outerRadius * Mth.sin(outerAngle2);

            // 内瓣边法线
            float midAngleInnerOuter2 = (innerAngle2 + outerAngle2) / 2.0f;
            float normalX2 = -Mth.cos(midAngleInnerOuter2);
            float normalZ2 = -Mth.sin(midAngleInnerOuter2);

            consumer.addVertex(currentPose, innerX2, 0.0f, innerZ2).setColor(r, g, b, a).setNormal(normalX2, 0.0f, normalZ2);
            consumer.addVertex(currentPose, innerX2, height, innerZ2).setColor(r, g, b, a).setNormal(normalX2, 0.0f, normalZ2);
            consumer.addVertex(currentPose, outerX2, height, outerZ2).setColor(r, g, b, a).setNormal(normalX2, 0.0f, normalZ2);
            consumer.addVertex(currentPose, outerX2, 0.0f, outerZ2).setColor(r, g, b, a).setNormal(normalX2, 0.0f, normalZ2);
        }
    }
}
