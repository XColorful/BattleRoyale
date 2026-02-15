package xiao.battleroyale.client.renderer.level;

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
    public static void drawFilledRectangleBox(final Matrix4f matrix, VertexConsumer consumer,
                                              float r, float g, float b, float a,
                                              float halfWidth, float halfDepth, float height) {
        float x1 = -halfWidth;
        float z1 = -halfDepth;
        float x2 = halfWidth;
        float z2 = halfDepth;

        // 渲染侧面
        // 前面 (负Z轴方向)
        // consumer.setNormal(0, 0, -1);
        consumer.addVertex(matrix, x1, 0, z1).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, 0, z1).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, height, z1).setColor(r, g, b, a);
        consumer.addVertex(matrix, x1, height, z1).setColor(r, g, b, a);

        // 后面 (正Z轴方向)
        // consumer.setNormal(0, 0, 1);
        consumer.addVertex(matrix, x1, 0, z2).setColor(r, g, b, a);
        consumer.addVertex(matrix, x1, height, z2).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, height, z2).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, 0, z2).setColor(r, g, b, a);

        // 左侧 (负X轴方向)
        // consumer.setNormal(-1, 0, 0);
        consumer.addVertex(matrix, x1, 0, z1).setColor(r, g, b, a);
        consumer.addVertex(matrix, x1, height, z1).setColor(r, g, b, a);
        consumer.addVertex(matrix, x1, height, z2).setColor(r, g, b, a);
        consumer.addVertex(matrix, x1, 0, z2).setColor(r, g, b, a);

        // 右侧 (正X轴方向)
        // consumer.setNormal(1, 0, 0);
        consumer.addVertex(matrix, x2, 0, z1).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, 0, z2).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, height, z2).setColor(r, g, b, a);
        consumer.addVertex(matrix, x2, height, z1).setColor(r, g, b, a);
    }

    /**
     * 绘制一个以模型原点为中心的填充多边形柱体。
     * 柱体的底面是一个正多边形，位于Y=0平面，向上延伸至指定高度。
     * @param radius 多边形的外接圆半径。
     * @param height 柱体的高度。
     * @param segments 多边形的边数（或圆的近似段数），值越大越平滑。
     * @param initialAngle 初始旋转角度（弧度），用于调整多边形的朝向。
     */
    public static void drawFilledPolygonCylinder(final Matrix4f matrix, VertexConsumer consumer,
                                                 float r, float g, float b, float a,
                                                 float radius, float height, int segments, float initialAngle) {
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

            consumer.setNormal(normalX, 0, normalZ);
            consumer.addVertex(matrix, x1, 0, z1).setColor(r, g, b, a);
            consumer.addVertex(matrix, x1, height, z1).setColor(r, g, b, a);
            consumer.addVertex(matrix, x2, height, z2).setColor(r, g, b, a);
            consumer.addVertex(matrix, x2, 0, z2).setColor(r, g, b, a);
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
    public static void drawFilledEllipseCylinder(Matrix4f matrix, VertexConsumer consumer,
                                                 float r, float g, float b, float a,
                                                 float halfA, float halfB, float height, int segments) {
        // 缓存矩阵
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);

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

            consumer.setNormal(normalX, 0, normalZ);
            consumer.addVertex(matrix, x1, 0, z1).setColor(r, g, b, a);
            consumer.addVertex(matrix, x1, height, z1).setColor(r, g, b, a);
            consumer.addVertex(matrix, x2, height, z2).setColor(r, g, b, a);
            consumer.addVertex(matrix, x2, 0, z2).setColor(r, g, b, a);
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
    public static void drawFilledStarCylinder(Matrix4f matrix, VertexConsumer consumer,
                                              float r, float g, float b, float a,
                                              float outerRadius, float innerRadius, float height, int segments, float initialAngle) {
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

            consumer.setNormal(normalX1, 0, normalZ1);
            consumer.addVertex(matrix, outerX1, 0, outerZ1).setColor(r, g, b, a);
            consumer.addVertex(matrix, outerX1, height, outerZ1).setColor(r, g, b, a);
            consumer.addVertex(matrix, innerX1, height, innerZ1).setColor(r, g, b, a);
            consumer.addVertex(matrix, innerX1, 0, innerZ1).setColor(r, g, b, a);

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

            consumer.setNormal(normalX2, 0, normalZ2);
            consumer.addVertex(matrix, innerX2, 0, innerZ2).setColor(r, g, b, a);
            consumer.addVertex(matrix, innerX2, height, innerZ2).setColor(r, g, b, a);
            consumer.addVertex(matrix, outerX2, height, outerZ2).setColor(r, g, b, a);
            consumer.addVertex(matrix, outerX2, 0, outerZ2).setColor(r, g, b, a);
        }
    }

    /**
     * 绘制一个以模型原点为中心的填充十字形柱体。
     * 十字由两个相互垂直的矩形组成，底面位于Y=0平面。
     * @param outerHalfWidth 十字架长臂的半长度（外宽）。
     * @param innerHalfWidth 十字架短臂的半宽度（内宽）。
     * @param height 柱体的高度。
     */
    public static void drawFilledCrossCylinder(Matrix4f matrix, VertexConsumer consumer,
                                               float r, float g, float b, float a,
                                               float outerHalfWidth, float innerHalfWidth, float height) {
        // 十字形的12个顶点坐标（按顺时针/逆时针排列，避免内部重叠）
        // 坐标点分布示意：
        //      1--2
        //      |  |
        // 12---3  4---5
        // |           |
        // 11---9  7---6
        //      |  |
        //      10-8
        float[] x = {
                -innerHalfWidth,  innerHalfWidth,  innerHalfWidth,  outerHalfWidth, outerHalfWidth,  innerHalfWidth,
                innerHalfWidth, -innerHalfWidth, -innerHalfWidth, -outerHalfWidth, -outerHalfWidth, -innerHalfWidth
        };
        float[] z = {
                -outerHalfWidth, -outerHalfWidth, -innerHalfWidth, -innerHalfWidth,  innerHalfWidth,  innerHalfWidth,
                outerHalfWidth,  outerHalfWidth,  innerHalfWidth,  innerHalfWidth, -innerHalfWidth, -innerHalfWidth
        };
        // 对应的法线向量（12条边）
        float[] nx = { 0, 1, 1, 0, -1, 0, 0, -1, -1, 0, 1, 0 };
        float[] nz = { -1, 0, 0, 1, 0, 1, 1, 0, 0, -1, 0, -1 };

        for (int i = 0; i < 12; i++) {
            int next = (i + 1) % 12;
            consumer.vertex(matrix, x[i], 0, z[i]).color(r, g, b, a).normal(nx[i], 0, nz[i]).endVertex();
            consumer.vertex(matrix, x[i], height, z[i]).color(r, g, b, a).normal(nx[i], 0, nz[i]).endVertex();
            consumer.vertex(matrix, x[next], height, z[next]).color(r, g, b, a).normal(nx[i], 0, nz[i]).endVertex();
            consumer.vertex(matrix, x[next], 0, z[next]).color(r, g, b, a).normal(nx[i], 0, nz[i]).endVertex();
        }
    }

    /**
     * 绘制一个以模型原点为中心的填充圆环柱体。
     * 包含外圆柱面和内圆柱面（内壁法线指向中心），底面位于Y=0平面。
     * @param outerRadius 圆环外径。
     * @param innerRadius 圆环内径。
     * @param height 柱体的高度。
     * @param segments 近似段数。
     * @param initialAngle 初始旋转角度。
     */
    public static void drawFilledRingCylinder(Matrix4f matrix, VertexConsumer consumer,
                                              float r, float g, float b, float a,
                                              float outerRadius, float innerRadius, float height, int segments, float initialAngle) {
        // 1. 渲染外环面 (法线向外)
        drawFilledPolygonCylinder(matrix, consumer, r, g, b, a, outerRadius, height, segments, initialAngle);

        // 2. 渲染内环面 (手动实现，法线向内)
        final float TWO_PI_DIV_SEGMENTS = (float) (2 * Math.PI / segments);
        for (int i = 0; i < segments; i++) {
            float angle1 = initialAngle + (i * TWO_PI_DIV_SEGMENTS);
            float angle2 = initialAngle + ((i + 1) * TWO_PI_DIV_SEGMENTS);

            float x1 = innerRadius * Mth.cos(angle1);
            float z1 = innerRadius * Mth.sin(angle1);
            float x2 = innerRadius * Mth.cos(angle2);
            float z2 = innerRadius * Mth.sin(angle2);

            // 内壁法线指向圆心，取外壁法线的反方向
            float midAngle = angle1 + (TWO_PI_DIV_SEGMENTS / 2.0f);
            float normalX = -Mth.cos(midAngle);
            float normalZ = -Mth.sin(midAngle);

            // 为了保证背面剔除(Backface Culling)正确，内壁的顶点顺序应与外壁相反
            consumer.vertex(matrix, x1, 0, z1).color(r, g, b, a).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(matrix, x2, 0, z2).color(r, g, b, a).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(matrix, x2, height, z2).color(r, g, b, a).normal(normalX, 0, normalZ).endVertex();
            consumer.vertex(matrix, x1, height, z1).color(r, g, b, a).normal(normalX, 0, normalZ).endVertex();
        }
    }
}
