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
        if (true) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera().getPosition();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (ClientZoneData zoneData : ClientGameDataManager.get().getActiveZones().values()) {
            poseStack.pushPose();
            poseStack.translate(zoneData.center.x - cameraPos.x,
                    zoneData.center.y - cameraPos.y,
                    zoneData.center.z - cameraPos.z);

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

            float r = zoneData.color.getRed() / 255.0f;
            float g = zoneData.color.getGreen() / 255.0f;
            float b = zoneData.color.getBlue() / 255.0f;
            float a = zoneData.color.getAlpha() / 255.0f;

            switch (zoneData.shapeType) {
                case CIRCLE -> drawFilledCircleCylinder(poseStack, consumer, r, g, b, a, (float) zoneData.dimension.x, (float) zoneData.dimension.y);
                case SQUARE, RECTANGLE -> drawFilledRectangleBox(poseStack, consumer, r, g, b, a, (float) zoneData.dimension.x, (float) zoneData.dimension.z, (float) zoneData.dimension.y);
            }

            poseStack.popPose();
        }

        bufferSource.endBatch();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawFilledCircleCylinder(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, float radius, float height) {
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        int segments = 64;
        float startAngle = 0;
        float endAngle = (float) (2 * Math.PI);

        // 绘制侧面
        for (int i = 0; i < segments; i++) {
            float angle1 = (startAngle + (endAngle - startAngle) * i / segments);
            float angle2 = (startAngle + (endAngle - startAngle) * (i + 1) / segments);

            float x1 = radius * Mth.cos(angle1);
            float z1 = radius * Mth.sin(angle1);
            float x2 = radius * Mth.cos(angle2);
            float z2 = radius * Mth.sin(angle2);

            float normalX = Mth.cos(angle1 + (angle2 - angle1) / 2.0F);
            float normalZ = Mth.sin(angle1 + (angle2 - angle1) / 2.0F);
            float normalY = 0.0F;

            consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).uv(0.0f, 0.0f).normal(normalX, normalY, normalZ).endVertex();
            consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).uv(0.0f, 1.0f).normal(normalX, normalY, normalZ).endVertex();
            consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).uv(1.0f, 1.0f).normal(normalX, normalY, normalZ).endVertex();
            consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).uv(1.0f, 0.0f).normal(normalX, normalY, normalZ).endVertex();
        }
    }

    private void drawFilledRectangleBox(PoseStack poseStack, VertexConsumer consumer, float r, float g, float b, float a, float halfWidth, float halfDepth, float height) {
        float x1 = -halfWidth;
        float z1 = -halfDepth;
        float x2 = halfWidth;
        float z2 = halfDepth;

        // 绘制前面
        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).uv(0.0f, 0.0f).normal(0, 0, -1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).uv(1.0f, 0.0f).normal(0, 0, -1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).uv(1.0f, 1.0f).normal(0, 0, -1).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).uv(0.0f, 1.0f).normal(0, 0, -1).endVertex();

        // 绘制后面
        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).uv(0.0f, 0.0f).normal(0, 0, 1).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).uv(0.0f, 1.0f).normal(0, 0, 1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).uv(1.0f, 1.0f).normal(0, 0, 1).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).uv(1.0f, 0.0f).normal(0, 0, 1).endVertex();

        // 绘制左面
        consumer.vertex(poseStack.last().pose(), x1, 0, z1).color(r, g, b, a).uv(0.0f, 0.0f).normal(-1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z1).color(r, g, b, a).uv(0.0f, 1.0f).normal(-1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, height, z2).color(r, g, b, a).uv(1.0f, 1.0f).normal(-1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x1, 0, z2).color(r, g, b, a).uv(1.0f, 0.0f).normal(-1, 0, 0).endVertex();

        // 绘制右面
        consumer.vertex(poseStack.last().pose(), x2, 0, z1).color(r, g, b, a).uv(0.0f, 0.0f).normal(1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, 0, z2).color(r, g, b, a).uv(1.0f, 0.0f).normal(1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z2).color(r, g, b, a).uv(1.0f, 1.0f).normal(1, 0, 0).endVertex();
        consumer.vertex(poseStack.last().pose(), x2, height, z1).color(r, g, b, a).uv(0.0f, 1.0f).normal(1, 0, 0).endVertex();
    }
}