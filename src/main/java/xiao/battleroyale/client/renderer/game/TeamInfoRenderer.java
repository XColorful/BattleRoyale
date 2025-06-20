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
import xiao.battleroyale.common.game.team.GamePlayer;

import java.awt.*;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class TeamInfoRenderer {

    private static double xRatio = -0.9; // TODO 可配置的队伍HUD位置比例
    private static double yRatio = -0.9;

    /*
    左上角
    [id] playerName
    ---------------（血条）
     */
    private static final int HEALTH_OFFSET = 7; // 左上角往下的距离，终点为血条左上角
    private static final int HEALTH_BAR_LENGTH = 75; // 血条长度
    private static final int HEALTH_BAR_HEIGHT = 1; // 血条厚度
    private static final int BOOST_OFFSET = 8;
    private static final int BOOST_BAR_LENGTH = 75;
    private static final int BOOST_BAR_HEIGHT = 1;
    private static final int LINE_OFFSET = 9;

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
        int idColor = teamColor.getRGB(); // 0xAARRGGBB
        int nameColor;
        List<ClientTeamData.TeamMemberInfo> teamMemberInfos = teamData.teamMemberInfoList;
        int currentY = posY - LINE_OFFSET * teamMemberInfos.size(); // 单行左上角位置
        for (ClientTeamData.TeamMemberInfo memberInfo : teamMemberInfos) {
            String playerId = "[" + memberInfo.playerId() + "]";
            String playerName = memberInfo.name();
            double health = memberInfo.health();
            nameColor = getNameColor(health);
            int boost = memberInfo.boost();
            /*
            [id] playerName
            ---------------（血条）
             */
            guiGraphics.drawString(fontRenderer, playerId, posX, currentY, idColor, false); // false表示不带阴影
            guiGraphics.drawString(fontRenderer, playerName, posX + fontRenderer.width(playerId) + 1, currentY, nameColor, true);
            int healthStartY = currentY + HEALTH_OFFSET; // 血条左上角
            renderHealthBar(health, posX, healthStartY, guiGraphics);
            int boostStartY = currentY + BOOST_OFFSET;
            renderBoostBar(boost, posX, boostStartY, guiGraphics);
            currentY += LINE_OFFSET;
        }
    }

    private void renderHealthBar(double health, int posX, int posY, GuiGraphics guiGraphics) {
        if (health > 0) {
            int healthEndX = (int) (posX + HEALTH_BAR_LENGTH * (health / 20F));
            guiGraphics.fill(posX, posY, healthEndX, posY + HEALTH_BAR_HEIGHT, getHealthColor(health));
        } else {
            int healthEndX = posX + HEALTH_BAR_LENGTH;
            guiGraphics.fill(posX, posY, healthEndX, posY + HEALTH_BAR_HEIGHT, 0xFF777777);
        }
    }

    private void renderBoostBar(int boost, int posX, int posY, GuiGraphics guiGraphics) {
        int boostLevel = GamePlayer.getBoostLevel(boost);
        double boostPercentage = GamePlayer.getBoostPercentage(boost);
        switch (boostLevel) {
            case 4: {
                int endX = (int) (posX + boostPercentage * BOOST_BAR_LENGTH + 0.99); // 全部向上取整，显示满能量条
                guiGraphics.fill((int) (posX + 0.9 * BOOST_BAR_LENGTH), posY, endX, posY + BOOST_BAR_HEIGHT, GamePlayer.BOOST_LEVEL_4);
            }
            case 3: {
                int endX = (int) (posX + Math.min(0.9, boostPercentage) * BOOST_BAR_LENGTH + 0.99);
                guiGraphics.fill((int) (posX + 0.6 * BOOST_BAR_LENGTH), posY, endX, posY + BOOST_BAR_HEIGHT, GamePlayer.BOOST_LEVEL_3);
            }
            case 2: {
                int endX = (int) (posX + Math.min(0.6, boostPercentage) * BOOST_BAR_LENGTH + 0.99);
                guiGraphics.fill((int) (posX + 0.2 * BOOST_BAR_LENGTH), posY, endX, posY + BOOST_BAR_HEIGHT, GamePlayer.BOOST_LEVEL_2);
            }
            case 1: {
                int endX = (int) (posX + Math.min(0.2, boostPercentage) * BOOST_BAR_LENGTH + 0.99); // 全部向上取整，显示有能量
                guiGraphics.fill(posX, posY, endX, posY + BOOST_BAR_HEIGHT, GamePlayer.BOOST_LEVEL_1);
                break;
            }
            case 0: {
                break;
            }
        }
    }

    // 0xAARRGGBB
    private int getNameColor(double health) {
        if (health == ClientTeamData.ELIMINATED) {
            return 0xFF323232; // grey
        } else if (health == ClientTeamData.OFFLINE) {
            return 0xFF585858; // dark grey
        } else {
            return 0xFFFFFFFF; // white
        }
    }

    // 0xAARRGGBB
    private int getHealthColor(double health) {
        if (health >= 20) {
            return 0xFFA5A5A5; // grey
        } else if (health >= 10) {
            return 0xFFF2F2F2; // white
        } else if (health >= 0.25) {
            return 0xFFF9E4A5; // yellow
        } else {
            return 0xFFDC564A; // red
        }
    }
}
