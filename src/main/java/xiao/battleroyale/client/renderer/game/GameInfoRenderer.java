package xiao.battleroyale.client.renderer.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientGameData;
import xiao.battleroyale.util.ColorUtils;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = BattleRoyale.MOD_ID)
public class GameInfoRenderer {

    private static class GameInfoRendererHolder {
        private static final GameInfoRenderer INSTANCE = new GameInfoRenderer();
    }

    public static GameInfoRenderer get() {
        return GameInfoRendererHolder.INSTANCE;
    }

    private GameInfoRenderer() {}

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

    private static boolean displayAlive = true;
    public static void setDisplayAlive(boolean shouldDisplay) { displayAlive = shouldDisplay;}

    private static double alive_xRatio = 0.85;
    public static void setAliveXRatio(double ratio) { alive_xRatio = ratio; }
    private static double alive_yRatio = 0.9;
    public static void setAliveYRatio(double ratio) { alive_yRatio = ratio; }

    private static int ALIVE_COLOR = ColorUtils.parseColorToInt("#FFFFFFFF");
    public static void setAliveColor(String colorString) { ALIVE_COLOR = ColorUtils.parseColorToInt(colorString); }
    private static int ALIVE_COUNT_COLOR = ColorUtils.parseColorToInt("#00FFFFFF");
    public static void setAliveCountColor(String colorString) { ALIVE_COUNT_COLOR = ColorUtils.parseColorToInt(colorString); }

    /*
    右上角
    生存: {人数}
     */
    public void onRenderGuiEvent(RenderGuiEvent.Post event) {
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
        ClientGameData gameData = ClientGameDataManager.get().getGameData();
        if (gameData.standingPlayerCount > 0) {
            renderAliveTotal(posX, posY, gameData.standingPlayerCount, guiGraphics, fontRenderer);
        }
    }

    private void renderAliveTotal(int posX, int posY, int aliveTotal, GuiGraphics guiGraphics, Font fontRenderer) {
        String alive = Component.translatable("battleroyale.label.alive").getString() + ":";
        String total = Integer.toString(aliveTotal);
        guiGraphics.drawString(fontRenderer, alive, posX, posY, ALIVE_COLOR, true);
        guiGraphics.drawString(fontRenderer, total, posX + fontRenderer.width(alive) + 2, posY, ALIVE_COUNT_COLOR, true);
    }
}
