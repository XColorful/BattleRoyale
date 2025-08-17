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
import xiao.battleroyale.client.game.data.TeamMemberInfo;
import xiao.battleroyale.common.effect.boost.BoostData;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class TeamInfoRenderer {

    private static class TeamInfoRendererHolder {
        private static final TeamInfoRenderer INSTANCE = new TeamInfoRenderer();
    }

    public static TeamInfoRenderer get() {
        return TeamInfoRendererHolder.INSTANCE;
    }

    private TeamInfoRenderer() {}

    private static boolean registered = false;
    public static boolean isRegistered() { return registered; }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(get());
        registered = true;
    }

    public static void unregister() {
        MinecraftForge.EVENT_BUS.unregister(get());
        registered = false;
    }

    private static boolean displayTeam = true;
    public static void setDisplayTeam(boolean shouldDisplay) { displayTeam = shouldDisplay; }

    private static long OFFLINE_TIME_LIMIT = ClientGameDataManager.TEAM_EXPIRE_TICK / 2;
    public static void setOfflineTimeLimit(int time) { OFFLINE_TIME_LIMIT = time; }
    public static int OFFLINE_COLOR = ColorUtils.parseColorToInt("#585858FF");
    public static int BLEEDING_COLOR = ColorUtils.parseColorToInt("#FF0000FF");
    public static int HEALTH_BACKGROUND_COLOR = ColorUtils.parseColorToInt("#777777FF");

    private static double xRatio = -0.9;
    public static void setXRatio(double ratio) { xRatio = ratio; }
    private static double yRatio = -0.9;
    public static void setYRatio(double ratio) { yRatio = ratio; }

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


    public void onRenderGuiEvent(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!displayTeam || mc.level == null || mc.player == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font fontRenderer = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int posX = (int) (screenWidth * (0.5 + xRatio / 2));
        int posY = (int) (screenHeight * (0.5 - yRatio / 2)); // 让配置项符合不旋转的直角坐标系
        ClientTeamData teamData = ClientGameDataManager.get().getTeamData();
        if (!teamData.inTeam()) {
            return;
        }

        Color teamColor = teamData.teamColor;
        int idColor = teamColor.getRGB(); // 0xAARRGGBB
        int nameColor;
        List<TeamMemberInfo> teamMemberInfos = teamData.teamMemberInfoList;
        int currentY = posY - LINE_OFFSET * teamMemberInfos.size(); // 单行左上角位置
        boolean offline = ClientGameDataManager.getCurrentTick() - teamData.getLastUpdateTick() > OFFLINE_TIME_LIMIT;
        for (TeamMemberInfo memberInfo : teamMemberInfos) {
            String playerId = "[" + memberInfo.playerId + "]";
            String playerName = memberInfo.name;
            double health = memberInfo.health;
            nameColor = offline ? OFFLINE_COLOR : getNameColor(health);
            int boost = memberInfo.boost;
            /*
            [id] playerName
            ---------------（血条）
             */
            guiGraphics.drawString(fontRenderer, playerId, posX, currentY, idColor, false); // false表示不带阴影
            guiGraphics.drawString(fontRenderer, playerName, posX + fontRenderer.width(playerId) + 1, currentY, nameColor, true);
            boolean alive = memberInfo.alive;
            int healthStartY = currentY + HEALTH_OFFSET; // 血条左上角
            renderHealthBar(health, posX, healthStartY, guiGraphics,
                    offline ? OFFLINE_COLOR : (alive ? getHealthColor(health) : BLEEDING_COLOR) );
            int boostStartY = currentY + BOOST_OFFSET;
            renderBoostBar(boost, posX, boostStartY, guiGraphics);
            currentY += LINE_OFFSET;
        }
    }

    private void renderHealthBar(double health, int posX, int posY, GuiGraphics guiGraphics, int healthColor) {
        int healthEndX = posX + HEALTH_BAR_LENGTH;
        guiGraphics.fill(posX, posY, healthEndX, posY + HEALTH_BAR_HEIGHT, HEALTH_BACKGROUND_COLOR);
        if (health > 0) { // 小于0为特殊标记，不用渲染血条
            healthEndX = (int) (posX + HEALTH_BAR_LENGTH * (health / 20F));
            guiGraphics.fill(posX, posY, healthEndX, posY + HEALTH_BAR_HEIGHT, healthColor);
        }
    }

    private void renderBoostBar(int boost, int posX, int posY, GuiGraphics guiGraphics) {
        int boostLevel = BoostData.getBoostLevel(boost);
        double boostPercentage = BoostData.getBoostPercentage(boost);
        switch (boostLevel) {
            case BoostData.BOOST_LV4: {
                int endX = posX + (int) Math.ceil(boostPercentage * BOOST_BAR_LENGTH);
                guiGraphics.fill((int) (posX + 0.9 * BOOST_BAR_LENGTH), posY, endX, posY + BOOST_BAR_HEIGHT, BoostData.COLOR_LV4);
            }
            case BoostData.BOOST_LV3: {
                int endX = posX + (int) Math.ceil(Math.min(0.9, boostPercentage) * BOOST_BAR_LENGTH);
                guiGraphics.fill((int) (posX + 0.6 * BOOST_BAR_LENGTH), posY, endX, posY + BOOST_BAR_HEIGHT, BoostData.COLOR_LV3);
            }
            case BoostData.BOOST_LV2: {
                int endX = posX + (int) Math.ceil(Math.min(0.6, boostPercentage) * BOOST_BAR_LENGTH);
                guiGraphics.fill((int) (posX + 0.2 * BOOST_BAR_LENGTH), posY, endX, posY + BOOST_BAR_HEIGHT, BoostData.COLOR_LV2);
            }
            case BoostData.BOOST_LV1: {
                int endX = posX + (int) Math.ceil(Math.min(0.2, boostPercentage) * BOOST_BAR_LENGTH);
                guiGraphics.fill(posX, posY, endX, posY + BOOST_BAR_HEIGHT, BoostData.COLOR_LV1);
            }
        }
    }

    // 0xAARRGGBB
    private int getNameColor(double health) {
        if (health == ClientTeamData.ELIMINATED) {
            return 0xFF323232; // grey
        } else if (health == ClientTeamData.OFFLINE) {
            return OFFLINE_COLOR; // dark grey
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
        } else if (health >= 5) {
            return 0xFFF9E4A5; // yellow
        } else {
            return 0xFFDC564A; // red
        }
    }
}
