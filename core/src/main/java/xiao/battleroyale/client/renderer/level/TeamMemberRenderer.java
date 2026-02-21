package xiao.battleroyale.client.renderer.level;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.event.RenderLevelStage;
import xiao.battleroyale.api.client.render.level.IClientTeamRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.client.game.data.TeamMemberInfo;
import xiao.battleroyale.client.renderer.CustomRenderType;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;

public class TeamMemberRenderer implements IClientTeamRenderer, IEventHandler {

    private static class TeamMemberRendererHolder {
        private static final TeamMemberRenderer INSTANCE = new TeamMemberRenderer();
    }

    public static TeamMemberRenderer get() {
        return TeamMemberRendererHolder.INSTANCE;
    }

    private TeamMemberRenderer() {}

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            BattleRoyale.LOGGER.debug("TeamMemberRenderer skipped init() at {}", mcSide.toString());
            return;
        }
    }

    private static final RenderType TEAM_MARKER_RENDER_TYPE = CustomRenderType.SolidTranslucentColor;

    private boolean enableTeamZone = true;
    public void setEnableTeamZone(boolean bool) { enableTeamZone = bool; }
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
        BattleRoyale.LOGGER.debug("TeamZoneRender {} R{} G{} B{}", colorString, R, G, B);
    }
    private boolean renderBeacon = true;
    public void setRenderBeacon(boolean bool) { renderBeacon = bool; }
    private boolean renderBoundingBox = true;
    public void setRenderBoundingBox(boolean bool) { renderBoundingBox = bool; }
    private float A = 0.5f;
    public void setTransparency(float a) { A = a; }

    private @Nullable Matrix4f currentZoneMatrix;
    private @Nullable VertexConsumer consumer;
    public @Nullable Matrix4f getCurrentZoneMatrix() {
        return currentZoneMatrix;
    }
    public @Nullable VertexConsumer getVertexConsumer() {
        return consumer;
    }

    public String getRendererName() {
        return String.format("%s:TeamMemberRenderer", BattleRoyale.MOD_ID);
    }

    @Override
    public boolean registerRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), EventType.RENDER_TRANSLUCENT_EVENT, EventPriority.NORMAL, false);
        return true;
    }

    @Override
    public boolean unregisterRenderEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), EventType.RENDER_TRANSLUCENT_EVENT, EventPriority.NORMAL, false);
        return true;
    }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:TeamMemberRenderer", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.RENDER_TRANSLUCENT_EVENT) {
            onAfterTranslucentBlocks((IRenderLevelStageEvent) event);
        } else {
            onReceiveWrongEvent(eventType);
        }
    }

    public void onRenderLevelStage(IRenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        onAfterTranslucentBlocks(event);
    }

    public void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {
        if (!enableTeamZone) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        ClientTeamData teamData = BattleRoyale.getClientGameDataManager().getTeamData();
        if (!teamData.inTeam() || teamData.teamMemberInfoList.isEmpty()) {
            return;
        }

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera_getPosition();
        float partialTicks = event.getPartialTick();

        VertexConsumer currentConsumer = bufferSource.getBuffer(TEAM_MARKER_RENDER_TYPE);

        int worldMaxBuildHeight = mc.level.dimensionType().minY() + mc.level.dimensionType().height();

        float r, g, b, a = A;
        if (useClientColor) {
            r = R;
            g = G;
            b = B;
            // a = A;
        } else {
            Color teamColor = teamData.teamColor;
            r = teamColor.getRed() / 255.0f;
            g = teamColor.getGreen() / 255.0f;
            b = teamColor.getBlue() / 255.0f;
            // a = teamColor.getAlpha() / 255.0f;
        }

        for (TeamMemberInfo member : teamData.teamMemberInfoList) {
            if (member.uuid == null || member.uuid.equals(mc.player.getUUID())) { // 不渲染自己
                continue;
            }

            Player teammatePlayer = mc.level.getPlayerByUUID(member.uuid);
            if (teammatePlayer != null) {
                // 渲染
                Vec3 lastTickPos = new Vec3(teammatePlayer.xOld, teammatePlayer.yOld, teammatePlayer.zOld);
                Vec3 currentTickPos = teammatePlayer.position();
                Vec3 interpolatedPos = lastTickPos.lerp(currentTickPos, partialTicks);
                AABB boundingBox = teammatePlayer.getBoundingBox();
                float teammateHeight = (float) (boundingBox.maxY - boundingBox.minY);
                float baseWidth = (float) (boundingBox.maxX - boundingBox.minX);
                float baseDepth = (float) (boundingBox.maxZ - boundingBox.minZ);

                float cylinderHeight = (float) (worldMaxBuildHeight - interpolatedPos.y - teammateHeight);

                // 将坐标系的原点平移到玩家的脚底中心
                currentZoneMatrix = ZoneRenderer.createCenterOffsetMatrix(event, interpolatedPos, cameraPos);
                consumer = currentConsumer;

                if (renderBoundingBox) {
                    // 渲染长方体
                    Matrix4f boundingBoxMatrix = new Matrix4f(currentZoneMatrix);
                    // 向上平移长方体高度的一半，使其中心与玩家身体中心对齐
                    boundingBoxMatrix.translate(0, teammateHeight / 2.0F, 0);
                    Shape3D.drawFilledCuboid(boundingBoxMatrix, consumer, r, g, b, a,
                            baseWidth / 2.0F, teammateHeight / 2.0F, baseDepth / 2.0F);
                }
                if (renderBeacon) {
                    // 渲染圆柱体
                    Matrix4f beaconMatrix = new Matrix4f(currentZoneMatrix);
                    // 向上平移到长方体的顶部
                    beaconMatrix.translate(0, teammateHeight, 0);
                    Shape2D.drawFilledPolygonCylinder(beaconMatrix, consumer, r, g, b, a,
                            baseWidth / 2.0F, cylinderHeight, 16, 0);
                }

                currentZoneMatrix = null;
                consumer = null;
            }
        }
        bufferSource.endBatch();
    }
}