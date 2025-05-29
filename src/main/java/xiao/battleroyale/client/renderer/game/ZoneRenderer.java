package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientZoneData;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class ZoneRenderer {

    public static void register() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ZoneRenderer());
    }

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return; // 仅在半透明方块渲染后执行

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return; // 确保游戏和玩家存在

        PoseStack poseStack = event.getPoseStack(); // 获取当前渲染的姿态栈
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource(); // 获取渲染缓冲源
        Vec3 cameraPos = event.getCamera().getPosition(); // 获取摄像机位置

        RenderSystem.enableDepthTest(); // 启用深度测试，使圈被地形正确遮挡
        RenderSystem.disableCull(); // 禁用背面剔除，确保线框始终完整可见
        RenderSystem.setShader(GameRenderer::getPositionColorShader); // 设置为基本颜色着色器

        for (ClientZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            poseStack.pushPose(); // 保存当前姿态栈状态
            poseStack.translate(zoneData.center.x - cameraPos.x, // 将渲染原点平移到圈的中心点，相对摄像机
                    zoneData.center.y - cameraPos.y,
                    zoneData.center.z - cameraPos.z);

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines()); // 获取线框渲染的顶点消费者

            float r = zoneData.color.getRed() / 255.0f; // 获取红色分量
            float g = zoneData.color.getGreen() / 255.0f; // 获取绿色分量
            float b = zoneData.color.getBlue() / 255.0f; // 获取蓝色分量
            float a = zoneData.color.getAlpha() / 255.0f; // 获取透明度分量

            switch (zoneData.shapeType) {
                case CIRCLE -> drawCircle(poseStack, consumer, r, g, b, a, (float) zoneData.dimension.x, (float) zoneData.dimension.y); // 绘制圆形线框柱体
                case SQUARE, RECTANGLE -> drawRectangle(poseStack, consumer, r, g, b, a, (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y); // 绘制矩形线框柱体
            }

            poseStack.popPose(); // 恢复之前保存的姿态栈状态
        }

        bufferSource.endBatch(); // 提交所有绘制指令

        RenderSystem.enableCull(); // 恢复背面剔除
        RenderSystem.disableDepthTest(); // 恢复深度测试（Minecraft在透明物体渲染后常禁用，此处保持一致）
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // 恢复默认着色器颜色
    }

    private void drawCircle(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, float radius, float height) {
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F)); // 旋转绘制平面，使圆柱体沿Y轴向上

        int segments = 64; // 圆的分段数
        float startAngle = 0;
        float endAngle = (float) (2 * Math.PI); // 绘制整个圆

        for (int i = 0; i < segments; i++) {
            float angle1 = (startAngle + (endAngle - startAngle) * i / segments);
            float angle2 = (startAngle + (endAngle - startAngle) * (i + 1) / segments);

            float x1 = radius * Mth.cos(angle1);
            float z1 = radius * Mth.sin(angle1);
            float x2 = radius * Mth.cos(angle2);
            float z2 = radius * Mth.sin(angle2);

            consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).endVertex(); // 底部圆线段
            consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).endVertex();

            consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).endVertex(); // 顶部圆线段
            consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).endVertex();
        }

        for (int i = 0; i < segments; i += (segments / 4)) { // 绘制4条垂直连接线
            float angle = (startAngle + (endAngle - startAngle) * i / segments);

            float x = radius * Mth.cos(angle);
            float z = radius * Mth.sin(angle);

            consumer.vertex(poseStack.last().pose(), x, 0, z).color(r, g, b, a).endVertex(); // 底部点
            consumer.vertex(poseStack.last().pose(), x, height, z).color(r, g, b, a).endVertex(); // 顶部点
        }
    }

    private void drawRectangle(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, float halfWidth, float halfDepth, float height) {
        float x1 = -halfWidth;
        float z1 = -halfDepth;
        float x2 = halfWidth;
        float z2 = halfDepth;

        // 绘制底部矩形
        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).endVertex();

        // 绘制顶部矩形
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).endVertex();

        // 绘制连接底部和顶部的垂直线
        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).endVertex(); // 左前角垂直线
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).endVertex(); // 右前角垂直线
        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).endVertex(); // 右后角垂直线
        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).endVertex();

        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).endVertex(); // 左后角垂直线
        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).endVertex();
    }
}