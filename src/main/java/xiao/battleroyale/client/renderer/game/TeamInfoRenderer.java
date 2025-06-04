package xiao.battleroyale.client.renderer.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientTeamData;

import java.awt.*;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class TeamInfoRenderer {

    private static double xRatio = -0.9;
    private static double yRatio = -0.9;

    private static final int LINE_HEIGHT = 11; // 名字+血条的高度
    private static final int HEALTH_BAR_LENGTH = 20; // 血条长度
    private static final int LINE_OFFSET = 12;
    private static final int HEALTH_BAR_HEIGHT = 1; // 血条厚度

    private static TeamInfoRenderer instance;

    private TeamInfoRenderer() {}

    public static TeamInfoRenderer get() {
        if (instance == null) {
            instance = new TeamInfoRenderer();
        }
        return instance;
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(TeamInfoRenderer.get());
    }

    public void onRenderGuiEvent(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            BattleRoyale.LOGGER.warn("In TeamInfoRenderer, mc.level == null || mc.player == null");
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font fontRenderer = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int posX = (int) (screenWidth * (0.5 + xRatio / 2));
        int posY = (int) (screenHeight * (0.5 - yRatio / 2)); // 让配置项符合不旋转的直角坐标系
        ClientTeamData teamData = ClientGameDataManager.get().getTeamData();
        if (!teamData.inTeam) {
            return;
        }

        Color teamColor = teamData.teamColor;
        int idColor = teamColor.getRGB();
        int nameColor;
        List<ClientTeamData.TeamMemberInfo> teamMemberInfos = teamData.teamMemberInfoList;
        int currentY = posY - LINE_OFFSET * teamMemberInfos.size(); // 单行左上角位置
        for (ClientTeamData.TeamMemberInfo memberInfo : teamMemberInfos) {
            String playerId = "[" + memberInfo.playerId() + "]";
            String playerName = memberInfo.name();
            double health = memberInfo.health();
            nameColor = getNameColor(health);
            /*
            [id] playerName
            ---------------（血条）
             */
            guiGraphics.drawString(fontRenderer, playerId, posX, currentY, idColor, false); // false表示不带阴影
            guiGraphics.drawString(fontRenderer, playerName, posX + fontRenderer.width(playerId) + 1, currentY, nameColor, true);
            if (health > 0) {
                int healthStartY = currentY + LINE_HEIGHT - HEALTH_BAR_HEIGHT; // 血条左上角
                int healthEndX = (int) (posX + HEALTH_BAR_LENGTH * (health / 20F));
                guiGraphics.fill(posX, healthStartY, healthEndX, healthStartY + HEALTH_BAR_HEIGHT, getHealthColor(health));
            } else {
                int healthStartY = currentY + LINE_HEIGHT - HEALTH_BAR_HEIGHT; // 血条左上角
                int healthEndX = posX + HEALTH_BAR_LENGTH;
                guiGraphics.fill(posX, healthStartY, healthEndX, healthStartY + HEALTH_BAR_HEIGHT, 0x777777);
            }
            currentY += LINE_OFFSET;
        }
    }

    private int getNameColor(double health) {
        if (health == ClientTeamData.ELIMINATED) {
            return 0x777777; // grey
        } else if (health == ClientTeamData.OFFLINE) {
            return 0x777777; // grey
        } else {
            return 0xFFFFFF; // white
        }
    }

    private int getHealthColor(double health) {
        if (health >= 20) {
            return 0xA5A5A5; // grey
        } else if (health >= 10) {
            return 0xF2F2F2; // white
        } else if (health >= 0.25) {
            return 0xF9E4A5; // yellow
        } else {
            return 0xDC564A; // red
        }
    }
}
