package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Shape3D {

    /**
     * 绘制一个以模型原点为中心的填充球体
     * 球体通过经纬度（UV Sphere）方式生成，由一系列四边形面组成
     * @param segments 控制球体的细分数量，值越大球体越平滑
     */
    public static void drawFilledSphere(PoseStack poseStack, VertexConsumer consumer,
                                        float r, float g, float b, float a,
                                        float radius, int segments) {
        final Matrix4f currentPoseMatrix = poseStack.last().pose();

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
                    consumer.vertex(currentPoseMatrix, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
                    consumer.vertex(currentPoseMatrix, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
                    consumer.vertex(currentPoseMatrix, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normal3.x(), normal3.y(), normal3.z()).endVertex();
                    consumer.vertex(currentPoseMatrix, x4, y4, z4).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.WHITE_OVERLAY_V).uv2(7864440).normal(normal4.x(), normal4.y(), normal4.z()).endVertex();
                }
            }
        }
    }
}