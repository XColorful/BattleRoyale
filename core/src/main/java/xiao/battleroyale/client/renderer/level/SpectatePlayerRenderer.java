package xiao.battleroyale.client.renderer.level;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.event.RenderLevelStage;
import xiao.battleroyale.api.client.render.level.IClientSpectateRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.client.game.data.ClientGameData.ClientSpectateData;
import xiao.battleroyale.client.renderer.CustomRenderType;
import xiao.battleroyale.util.ClassUtils;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;
import java.util.UUID;

public class SpectatePlayerRenderer implements IClientSpectateRenderer, IEventHandler {

    private static class SpectatePlayerRendererHolder {
        private static final SpectatePlayerRenderer INSTANCE = new SpectatePlayerRenderer();
    }

    public static SpectatePlayerRenderer get() {
        return SpectatePlayerRendererHolder.INSTANCE;
    }

    protected SpectatePlayerRenderer() {}

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            BattleRoyale.LOGGER.debug("SpectatePlayerRenderer skipped init() at {}", mcSide.toString());
            return;
        }
    }

    public static final RenderType SPECTATE_PLAYER_RENDER_TYPE = CustomRenderType.SolidTranslucentColor;

    private boolean enableSpectateRender = true;
    public void setEnableSpectateRender(boolean bool) { enableSpectateRender = bool; }
    private boolean useClientColor = false;
    public void setUseClientColor(boolean use) { useClientColor = use; }
    private float R = 0f;
    private float G = 1f;
    private float B = 1f;
    public void setClientColorString(String colorString) {
        Color color = ColorUtils.parseColorFromString(colorString);
        R = color.getRed() / 255.0F;
        G = color.getGreen() / 255.0F;
        B = color.getBlue() / 255.0F;
        BattleRoyale.LOGGER.debug("SpectatePlayerRenderer {} R{} G{} B{}", colorString, R, G, B);
    }
    private boolean renderBeacon = true;
    public void setRenderBeacon(boolean bool) { renderBeacon = bool; }
    private boolean renderBoundingBox = true;
    public void setRenderBoundingBox(boolean bool) { renderBoundingBox = bool; }
    private float A = 0.5F;
    public void setTransparency(float a) { A = Math.min(Math.max(0, a), 1); }

    private static final ClassUtils.ArraySet<UUID> cachedSpectatePlayerUUID = new ClassUtils.ArraySet<>();
    private int scanFrequency = 20 * 3; // 3秒扫一次
    public void setScanFrequency(int frequency) { scanFrequency = Math.max(frequency, 1); }
    public int getScanFrequency() { return scanFrequency; }

    private @Nullable Matrix4f currentZoneMatrix;
    private @Nullable VertexConsumer consumer;
    public @Nullable Matrix4f getCurrentZoneMatrix() {
        return currentZoneMatrix;
    }
    public @Nullable VertexConsumer getVertexConsumer() {
        return consumer;
    }

    public String getRendererName() {
        return String.format("%s:SpectatePlayerRenderer", BattleRoyale.MOD_ID);
    }

    @Override
    public boolean registerRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), EventType.RENDER_TRANSLUCENT_EVENT, EventPriority.LOW, false);
        return true;
    }

    @Override
    public boolean unregisterRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), EventType.RENDER_TRANSLUCENT_EVENT, EventPriority.LOW, false);
        return true;
    }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:SpectatePlayerRenderer", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.RENDER_TRANSLUCENT_EVENT) {
            onAfterTranslucentBlocks((IRenderLevelStageEvent) event);
        } else {
            onReceiveWrongEvent(eventType);
        }
    }

    public void scanSpectatePlayers() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        ClientSpectateData spectateData = BattleRoyale.getClientGameDataManager().getGameData().getSpectateData();
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

    public void onRenderLevelStage(IRenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        onAfterTranslucentBlocks(event);
    }

    public void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {
        if (!enableSpectateRender) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !mc.player.isSpectator()) {
            return;
        }

        ClientSpectateData spectateData = BattleRoyale.getClientGameDataManager().getGameData().getSpectateData();
        if (spectateData.uuidToColor.isEmpty()) {
            return;
        }

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera_getPosition();
        float partialTicks = event.getPartialTick();

        VertexConsumer currentConsumer = bufferSource.getBuffer(SPECTATE_PLAYER_RENDER_TYPE);

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
            Vec3 currentPos = spectatePlayer.position();
            double posX = Mth.lerp(partialTicks, spectatePlayer.xOld, currentPos.x);
            double posY = Mth.lerp(partialTicks, spectatePlayer.yOld, currentPos.y);
            double posZ = Mth.lerp(partialTicks, spectatePlayer.zOld, currentPos.z);
            AABB boundingBox = spectatePlayer.getBoundingBox();
            float playerHeight = (float) (boundingBox.maxY - boundingBox.minY);
            float baseWidth = (float) (boundingBox.maxX - boundingBox.minX);
            float baseDepth = (float) (boundingBox.maxZ - boundingBox.minZ);

            float cylinderHeight = (float) (worldMaxBuildHeight - posY - playerHeight);

            // 将坐标系的原点平移到玩家的脚底中心
            currentZoneMatrix = ZoneRenderer.createCenterOffsetMatrix(event, posX, posY, posZ, cameraPos);
            consumer = currentConsumer;

            if (renderBoundingBox) {
                // 渲染长方体
                Matrix4f boundingBoxMatrix = new Matrix4f(currentZoneMatrix);
                // 向上平移长方体高度的一半，使其中心与玩家身体中心对齐
                boundingBoxMatrix.translate(0, playerHeight / 2.0F, 0);
                Shape3D.drawFilledCuboid(boundingBoxMatrix, consumer, r, g, b, a,
                        baseWidth / 2.0F, playerHeight / 2.0F, baseDepth / 2.0F);
            }
            if (renderBeacon) {
                // 渲染圆柱体
                Matrix4f beaconMatrix = new Matrix4f(currentZoneMatrix);
                // 向上平移到长方体的顶部
                beaconMatrix.translate(0, playerHeight, 0);
                Shape2D.drawFilledPolygonCylinder(beaconMatrix, consumer, r, g, b, a,
                        baseWidth / 2.0F, cylinderHeight, 16, 0);
            }

            currentZoneMatrix = null;
            consumer = null;
        }
        bufferSource.endBatch();
    }
}