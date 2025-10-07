package xiao.battleroyale.client.renderer.game;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.client.game.data.TeamMemberInfo;
import xiao.battleroyale.client.renderer.CustomRenderType;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;

public class TeamMemberRenderer {

    private static class TeamMemberRendererHolder {
        private static final TeamMemberRenderer INSTANCE = new TeamMemberRenderer();
    }

    public static TeamMemberRenderer get() {
        return TeamMemberRendererHolder.INSTANCE;
    }

    private TeamMemberRenderer() {}

    private static final RenderType TEAM_MARKER_RENDER_TYPE = CustomRenderType.SolidTranslucentColor;

    private static boolean enableTeamZone = true;
    public static void setEnableTeamZone(boolean bool) { enableTeamZone = bool; }
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
        BattleRoyale.LOGGER.debug("TeamZoneRender {} R{} G{} B{}", colorString, R, G, B);
    }
    private static boolean renderBeacon = true;
    public static void setRenderBeacon(boolean bool) { renderBeacon = bool; }
    private static boolean renderBoundingBox = true;
    public static void setRenderBoundingBox(boolean bool) { renderBoundingBox = bool; }
    private static float A = 0.5f;
    public static void setTransparency(float a) { A = a; }

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

        ClientTeamData teamData = ClientGameDataManager.get().getTeamData();
        if (!teamData.inTeam() || teamData.teamMemberInfoList.isEmpty()) {
            return;
        }

        Matrix4f baseModelView = event.getModelViewMatrix();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 cameraPos = event.getCamera_getPosition();
        float partialTicks = event.getPartialTick();

        VertexConsumer consumer = bufferSource.getBuffer(TEAM_MARKER_RENDER_TYPE);

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

                Matrix4f matrix = new Matrix4f(baseModelView);
                // 将坐标系的原点平移到玩家的脚底中心
                matrix.translate(
                        (float) (interpolatedPos.x - cameraPos.x),
                        (float) (interpolatedPos.y - cameraPos.y),
                        (float) (interpolatedPos.z - cameraPos.z));

                if (renderBoundingBox) {
                    // 渲染长方体
                    Matrix4f boundingBoxMatrix = new Matrix4f(matrix);
                    // 向上平移长方体高度的一半，使其中心与玩家身体中心对齐
                    boundingBoxMatrix.translate(0, teammateHeight / 2.0F, 0);
                    Shape3D.drawFilledCuboid(boundingBoxMatrix, consumer, r, g, b, a,
                            baseWidth / 2.0F, teammateHeight / 2.0F, baseDepth / 2.0F);
                }
                if (renderBeacon) {
                    // 渲染圆柱体
                    Matrix4f beaconMatrix = new Matrix4f(matrix);
                    // 向上平移到长方体的顶部
                    beaconMatrix.translate(0, teammateHeight, 0);
                    Shape2D.drawFilledPolygonCylinder(beaconMatrix, consumer, r, g, b, a,
                            baseWidth / 2.0F, cylinderHeight, 16, 0);
                }
            }
        }
        bufferSource.endBatch();
    }
}