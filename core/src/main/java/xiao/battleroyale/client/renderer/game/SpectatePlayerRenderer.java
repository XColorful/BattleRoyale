package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientGameData.ClientSpectateData;
import xiao.battleroyale.client.renderer.CustomRenderType;
import xiao.battleroyale.util.ClassUtils;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;
import java.util.UUID;

public class SpectatePlayerRenderer {

    private static class SpectatePlayerRendererHolder {
        private static final SpectatePlayerRenderer INSTANCE = new SpectatePlayerRenderer();
    }

    public static SpectatePlayerRenderer get() {
        return SpectatePlayerRendererHolder.INSTANCE;
    }

    private SpectatePlayerRenderer() {}

    private static boolean registered = false;
    public static boolean isRegistered() {
        return registered;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        registered = true;
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        registered = false;
    }

    private static final RenderType SPECTATE_PLAYER_RENDER_TYPE = CustomRenderType.SolidTranslucentColor;
    private static boolean enableSpectateRender = true;
    public static void setEnableSpectateRender(boolean bool) { enableSpectateRender = bool; }
    private static boolean useClientColor = false;
    public static void setUseClientColor(boolean use) { useClientColor = use; }
    private static float R = 0f;
    private static float G = 1f;
    private static float B = 1f;
    public static void setClientColorString(String colorString) {
        Color color = ColorUtils.parseColorFromString(colorString);
        R = color.getRed() / 255.0F;
        G = color.getGreen() / 255.0F;
        B = color.getBlue() / 255.0F;
        BattleRoyale.LOGGER.debug("SpectatePlayerRenderer {} R{} G{} B{}", colorString, R, G, B);
    }
    private static boolean renderBeacon = true;
    public static void setRenderBeacon(boolean bool) { renderBeacon = bool; }
    private static boolean renderBoundingBox = true;
    public static void setRenderBoundingBox(boolean bool) { renderBoundingBox = bool; }
    private static float A = 0.5F;
    public static void setTransparency(float a) { A = Math.min(Math.max(0, a), 1); }

    private static final ClassUtils.ArraySet<UUID> cachedSpectatePlayerUUID = new ClassUtils.ArraySet<>();
    private static int scanFrequency = 20 * 3; // 3秒扫一次
    public static void setScanFrequency(int frequency) { scanFrequency = Math.max(frequency, 1); }
    public static int getScanFrequency() { return scanFrequency; }

    public void scanSpectatePlayers() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        ClientSpectateData spectateData = ClientGameDataManager.get().getGameData().getSpectateData();
        if (spectateData.uuidToColor.isEmpty()) {
            return;
        }

        cachedSpectatePlayerUUID.clear();
        for (UUID uuid : spectateData.uuidToColor.keySet()) {
            Player spectatePlayer = mc.level.getPlayerByUUID(uuid);
            if (spectatePlayer != null
                    && !spectatePlayer.getUUID().equals(mc.player.getUUID())) { // 不渲染自己，会挡住第一人称
                cachedSpectatePlayerUUID.add(uuid);
            }
        }
    }

    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!enableSpectateRender || event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !mc.player.isSpectator()) {
            return;
        }

        ClientSpectateData spectateData = ClientGameDataManager.get().getGameData().getSpectateData();
        if (spectateData.uuidToColor.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera().getPosition();
        float partialTicks = event.getPartialTick();

        VertexConsumer consumer = bufferSource.getBuffer(SPECTATE_PLAYER_RENDER_TYPE);

        int worldMaxBuildHeight = mc.level.getMaxBuildHeight();

        float r, g, b, a = A;
        for (UUID uuid : cachedSpectatePlayerUUID) {
            ClientSpectateData.UUIDrgb uuidRGB = spectateData.uuidToColor.mapGet(uuid);
            if (uuidRGB == null) { // 不立即更新spectateData
                continue;
            }
            Player spectatePlayer = mc.level.getPlayerByUUID(uuid);
            if (spectatePlayer == null) { // 不立即更新cachedSpectatePlayerUUID
                continue;
            }

            // 颜色
            if (useClientColor) {
                r = R;
                g = G;
                b = B;
            } else {
                r = uuidRGB.r();
                g = uuidRGB.g();
                b = uuidRGB.b();
            }

            // 渲染
            Vec3 lastTickPos = new Vec3(spectatePlayer.xOld, spectatePlayer.yOld, spectatePlayer.zOld);
            Vec3 currentTickPos = spectatePlayer.position();
            Vec3 interpolatedPos = lastTickPos.lerp(currentTickPos, partialTicks);
            AABB boundingBox = spectatePlayer.getBoundingBox();
            float playerHeight = (float) (boundingBox.maxY - boundingBox.minY);
            float baseWidth = (float) (boundingBox.maxX - boundingBox.minX);
            float baseDepth = (float) (boundingBox.maxZ - boundingBox.minZ);

            float cylinderHeight = (float) (worldMaxBuildHeight - interpolatedPos.y - playerHeight);

            poseStack.pushPose();
            try {
                // 将坐标系的原点平移到玩家的脚底中心
                poseStack.translate(interpolatedPos.x - cameraPos.x,
                        interpolatedPos.y - cameraPos.y,
                        interpolatedPos.z - cameraPos.z);
                if (renderBoundingBox) {
                    // 渲染长方体
                    poseStack.pushPose();
                    // 向上平移长方体高度的一半，使其中心与玩家身体中心对齐
                    poseStack.translate(0, playerHeight / 2.0F, 0);
                    Shape3D.drawFilledCuboid(poseStack, consumer, r, g, b, a,
                            baseWidth / 2.0F, playerHeight / 2.0F, baseDepth / 2.0F);
                    poseStack.popPose();
                }
                if (renderBeacon) {
                    // 渲染圆柱体
                    poseStack.pushPose();
                    // 向上平移到长方体的顶部
                    poseStack.translate(0, playerHeight, 0);
                    Shape2D.drawFilledPolygonCylinder(poseStack, consumer, r, g, b, a,
                            baseWidth / 2.0F, cylinderHeight, 16, 0);
                    poseStack.popPose();
                }
            } finally {
                poseStack.popPose();
            }
        }
        bufferSource.endBatch();
    }
}