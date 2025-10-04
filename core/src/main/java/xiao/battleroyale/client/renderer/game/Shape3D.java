package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Shape3D {

    /**
     * 绘制一个以模型原点为中心的填充球体
     * 球体通过经纬度（UV Sphere）方式生成，由一系列四边形面组成
     * @param segments 控制球体的细分数量，值越大球体越平滑
     */
    public static void drawFilledSphere(final Matrix4f matrix, VertexConsumer consumer,
                                        float r, float g, float b, float a,
                                        float radius, int segments) {
        // 纬度带数量，通常是经度切片的一半，避免在极点过度细分
        int stacks = segments / 2;
        // 经度切片数量
        int slices = segments;

        // 遍历纬度带
        for (int i = 0; i <= stacks; i++) {
            // 计算当前纬度的垂直角度 (phi)，从顶部 (0) 到底部 (PI)
            float phi = (float) Math.PI * i / stacks;
            float cosPhi = Mth.cos(phi);
            float sinPhi = Mth.sin(phi);

            // 遍历经度切片
            for (int j = 0; j <= slices; j++) {
                // 计算当前经度的水平角度 (theta)，从 0 到 2 * PI
                float theta = (float) (2 * Math.PI) * j / slices;
                float cosTheta = Mth.cos(theta);
                float sinTheta = Mth.sin(theta);

                // 计算当前经纬度点的坐标 (v1)
                float x1 = radius * sinPhi * cosTheta;
                float y1 = radius * cosPhi;
                float z1 = radius * sinPhi * sinTheta;

                // 计算下一个经度点的坐标 (v2)
                float nextTheta = (float) (2 * Math.PI) * (j + 1) / slices;
                float nextCosTheta = Mth.cos(nextTheta);
                float nextSinTheta = Mth.sin(nextTheta);
                float x2 = radius * sinPhi * nextCosTheta;
                float y2 = radius * cosPhi;
                float z2 = radius * sinPhi * nextSinTheta;

                // 计算下一个纬度带的当前经度点的坐标 (v4)
                float nextPhi = (float) Math.PI * (i + 1) / stacks;
                float nextCosPhi = Mth.cos(nextPhi);
                float nextSinPhi = Mth.sin(nextPhi);
                float x4 = radius * nextSinPhi * cosTheta;
                float y4 = radius * nextCosPhi;
                float z4 = radius * nextSinPhi * sinTheta;

                // 计算下一个纬度带的下一个经度点的坐标 (v3)
                float x3 = radius * nextSinPhi * nextCosTheta;
                float y3 = radius * nextCosPhi;
                float z3 = radius * nextSinPhi * nextSinTheta;

                // 对于原点在中心的球体，法线就是归一化后的顶点向量
                Vector3f normal1 = new Vector3f(x1, y1, z1).normalize();
                Vector3f normal2 = new Vector3f(x2, y2, z2).normalize();
                Vector3f normal3 = new Vector3f(x3, y3, z3).normalize();
                Vector3f normal4 = new Vector3f(x4, y4, z4).normalize();

                // 渲染四边形面，避免在极点附近渲染重复面
                // 顶点顺序: (x1,y1,z1) -> (x2,y2,z2) -> (x3,y3,z3) -> (x4,y4,z4)
                if (i < stacks) {
                    consumer.setNormal(normal1.x(), normal1.y(), normal1.z());
                    consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);

                    consumer.setNormal(normal2.x(), normal2.y(), normal2.z());
                    consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);

                    consumer.setNormal(normal3.x(), normal3.y(), normal3.z());
                    consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a);

                    consumer.setNormal(normal4.x(), normal4.y(), normal4.z());
                    consumer.addVertex(matrix, x4, y4, z4).setColor(r, g, b, a);
                }
            }
        }
    }

    /**
     * 绘制一个以模型原点为中心的填充长方体。
     * 长方体的所有六个面都将被渲染。
     * @param halfWidth 长方体在X轴方向上的半长。
     * @param halfHeight 长方体在Y轴方向上的半高。
     * @param halfDepth 长方体在Z轴方向上的半深。
     */
    public static void drawFilledCuboid(final Matrix4f matrix, VertexConsumer consumer,
                                        float r, float g, float b, float a,
                                        float halfWidth, float halfHeight, float halfDepth) {
        float x_neg = -halfWidth;
        float y_neg = -halfHeight;
        float z_neg = -halfDepth;
        float x_pos = halfWidth;
        float y_pos = halfHeight;
        float z_pos = halfDepth;

        // 前面 (负Z轴方向)
        consumer.setNormal(0, 0, -1);
        consumer.addVertex(matrix, x_neg, y_neg, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_neg, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_pos, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_pos, z_neg).setColor(r, g, b, a);

        // 后面 (正Z轴方向)
        consumer.setNormal(0, 0, 1);
        consumer.addVertex(matrix, x_neg, y_neg, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_pos, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_pos, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_neg, z_pos).setColor(r, g, b, a);

        // 左侧 (负X轴方向)
        consumer.setNormal(-1, 0, 0);
        consumer.addVertex(matrix, x_neg, y_neg, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_pos, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_pos, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_neg, z_neg).setColor(r, g, b, a);

        // 右侧 (正X轴方向)
        consumer.setNormal(1, 0, 0);
        consumer.addVertex(matrix, x_pos, y_neg, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_pos, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_pos, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_neg, z_pos).setColor(r, g, b, a);

        // 顶面 (正Y方向)
        consumer.setNormal(0, 1, 0);
        consumer.addVertex(matrix, x_neg, y_pos, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_pos, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_pos, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_pos, z_pos).setColor(r, g, b, a);

        // 底面 (负Y方向)
        consumer.setNormal(0, -1, 0);
        consumer.addVertex(matrix, x_neg, y_neg, z_neg).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_neg, y_neg, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_neg, z_pos).setColor(r, g, b, a);
        consumer.addVertex(matrix, x_pos, y_neg, z_neg).setColor(r, g, b, a);
    }

    /**
     * 绘制一个以模型原点为中心的填充椭球。
     * 椭球通过经纬度（UV Ellipsoid）方式生成，由一系列四边形面组成。
     * @param halfA 椭球在X轴方向上的半轴长。
     * @param halfB 椭球在Y轴方向上的半轴长。
     * @param halfC 椭球在Z轴方向上的半轴长。
     * @param segments 控制椭球的细分数量，值越大椭球越平滑。
     */
    public static void drawFilledEllipsoid(final Matrix4f matrix, VertexConsumer consumer,
                                           float r, float g, float b, float a,
                                           float halfA, float halfB, float halfC, int segments) {
        // 纬度带数量，通常是经度切片的一半，避免在极点过度细分
        int stacks = segments / 2;
        // 经度切片数量
        int slices = segments;

        // 遍历纬度带
        for (int i = 0; i <= stacks; i++) {
            // 计算当前纬度的垂直角度 (phi)，从顶部 (0) 到底部 (PI)
            float phi = (float) Math.PI * i / stacks;
            float cosPhi = Mth.cos(phi);
            float sinPhi = Mth.sin(phi);

            // 遍历经度切片
            for (int j = 0; j <= slices; j++) {
                // 计算当前经度的水平角度 (theta)，从 0 到 2 * PI
                float theta = (float) (2 * Math.PI) * j / slices;
                float cosTheta = Mth.cos(theta);
                float sinTheta = Mth.sin(theta);

                // 计算当前经纬度点的坐标 (v1)，根据半轴缩放
                float x1 = halfA * sinPhi * cosTheta;
                float y1 = halfB * cosPhi;
                float z1 = halfC * sinPhi * sinTheta;

                // 计算下一个经度点的坐标 (v2)
                float nextTheta = (float) (2 * Math.PI) * (j + 1) / slices;
                float nextCosTheta = Mth.cos(nextTheta);
                float nextSinTheta = Mth.sin(nextTheta);
                float x2 = halfA * sinPhi * nextCosTheta;
                float y2 = halfB * cosPhi;
                float z2 = halfC * sinPhi * nextSinTheta;

                // 计算下一个纬度带的当前经度点的坐标 (v4)
                float nextPhi = (float) Math.PI * (i + 1) / stacks;
                float nextCosPhi = Mth.cos(nextPhi);
                float nextSinPhi = Mth.sin(nextPhi);
                float x4 = halfA * nextSinPhi * cosTheta;
                float y4 = halfB * nextCosPhi;
                float z4 = halfC * nextSinPhi * sinTheta;

                // 计算下一个纬度带的下一个经度点的坐标 (v3)
                float x3 = halfA * nextSinPhi * nextCosTheta;
                float y3 = halfB * nextCosPhi;
                float z3 = halfC * nextSinPhi * nextSinTheta;

                // 计算法线向量 (对于椭球，法线不再是简单的顶点归一化)
                // 椭球表面的法线向量与梯度成正比。
                // 椭球方程为 (x/a)^2 + (y/b)^2 + (z/c)^2 = 1
                // 梯度向量为 (2x/a^2, 2y/b^2, 2z/c^2)。归一化此向量即可得到法线。
                Vector3f normal1 = new Vector3f(x1 / (halfA * halfA), y1 / (halfB * halfB), z1 / (halfC * halfC)).normalize();
                Vector3f normal2 = new Vector3f(x2 / (halfA * halfA), y2 / (halfB * halfB), z2 / (halfC * halfC)).normalize();
                Vector3f normal3 = new Vector3f(x3 / (halfA * halfA), y3 / (halfB * halfB), z3 / (halfC * halfC)).normalize();
                Vector3f normal4 = new Vector3f(x4 / (halfA * halfA), y4 / (halfB * halfB), z4 / (halfC * halfC)).normalize();

                // 渲染四边形面
                if (i < stacks) { // 避免在极点渲染重复面
                    consumer.setNormal(normal1.x(), normal1.y(), normal1.z());
                    consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);

                    consumer.setNormal(normal2.x(), normal2.y(), normal2.z());
                    consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);

                    consumer.setNormal(normal3.x(), normal3.y(), normal3.z());
                    consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a);

                    consumer.setNormal(normal4.x(), normal4.y(), normal4.z());
                    consumer.addVertex(matrix, x4, y4, z4).setColor(r, g, b, a);
                }
            }
        }
    }
}