package xiao.battleroyale.client.renderer.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.IRenderGuiEventPost;
import xiao.battleroyale.api.client.render.gui.IClientGameInfoRenderer;
import xiao.battleroyale.client.game.data.ClientGameData;
import xiao.battleroyale.util.ColorUtils;

public class GameInfoRenderer implements IClientGameInfoRenderer {

    private static class GameInfoRendererHolder {
        private static final GameInfoRenderer INSTANCE = new GameInfoRenderer();
    }

    public static GameInfoRenderer get() {
        return GameInfoRendererHolder.INSTANCE;
    }

    private GameInfoRenderer() {}

    private boolean displayAlive = true;
    public void setDisplayAlive(boolean shouldDisplay) { displayAlive = shouldDisplay;}

    private double alive_xRatio = 0.85;
    public void setAliveXRatio(double ratio) { alive_xRatio = ratio; }
    private double alive_yRatio = 0.9;
    public void setAliveYRatio(double ratio) { alive_yRatio = ratio; }

    private int ALIVE_COLOR = ColorUtils.parseColorToInt("#FFFFFFFF");
    public void setAliveColor(String colorString) { ALIVE_COLOR = ColorUtils.parseColorToInt(colorString); }
    private int ALIVE_COUNT_COLOR = ColorUtils.parseColorToInt("#00FFFFFF");
    public void setAliveCountColor(String colorString) { ALIVE_COUNT_COLOR = ColorUtils.parseColorToInt(colorString); }

    public String getRendererName() {
        return String.format("%s:GameInfoRenderer", BattleRoyale.MOD_ID);
    }

    /*
    右上角
    生存: {人数}
     */
    public void onRenderGuiEvent(IRenderGuiEventPost event) {
        Minecraft mc = Minecraft.getInstance();
        if (!displayAlive || mc.level == null || mc.player == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font fontRenderer = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int posX = (int) (screenWidth * (0.5 + alive_xRatio / 2));
        int posY = (int) (screenHeight * (0.5 - alive_yRatio / 2)); // 让配置项符合不旋转的直角坐标系

        // 生存: {人数}
        ClientGameData gameData = BattleRoyale.getClientGameDataManager().getGameData();
        if (gameData.inGame()) {
            renderAliveTotal(posX, posY, gameData.standingPlayerCount(), guiGraphics, fontRenderer);
        }
    }

    private void renderAliveTotal(int posX, int posY, int aliveTotal, GuiGraphics guiGraphics, Font fontRenderer) {
        String alive = Component.translatable("battleroyale.label.alive").getString() + ":";
        String total = Integer.toString(aliveTotal);
        guiGraphics.drawString(fontRenderer, alive, posX, posY, ALIVE_COLOR, true);
        guiGraphics.drawString(fontRenderer, total, posX + fontRenderer.width(alive) + 2, posY, ALIVE_COUNT_COLOR, true);
    }
}
