package xiao.battleroyale.config.client.display.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.client.display.DisplayConfigTag;
import xiao.battleroyale.client.renderer.game.GameInfoRenderer;
import xiao.battleroyale.util.JsonUtils;

public class GameEntry extends AbstractHudEntry {

    public String aliveColor;
    public String aliveCountColor;

    public GameEntry(boolean display) {
        this(display, 0, 0, "", "");
    }

    public GameEntry(boolean display, double xRatio, double yRatio, String aliveColor, String aliveCountColor) {
        super(display, xRatio, yRatio);
        this.aliveColor = aliveColor;
        this.aliveCountColor = aliveCountColor;
    }

    @Override
    public String getType() {
        return "gameEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        if (display) {
            jsonObject.addProperty(DisplayConfigTag.ALIVE_COLOR, aliveColor);
            jsonObject.addProperty(DisplayConfigTag.ALIVE_COUNT_COLOR, aliveCountColor);
        }
        return jsonObject;
    }

    public static GameEntry fromJson(JsonObject jsonObject) {
        boolean display = JsonUtils.getJsonBool(jsonObject, DisplayConfigTag.SHOULD_DISPLAY, false);
        if (!display) {
            return new GameEntry(display);
        }
        double xRatio = JsonUtils.getJsonDouble(jsonObject, DisplayConfigTag.HUD_X_RATIO, 0);
        double yRatio = JsonUtils.getJsonDouble(jsonObject, DisplayConfigTag.HUD_Y_RATIO, 0);

        String aliveColor = JsonUtils.getJsonString(jsonObject, DisplayConfigTag.ALIVE_COLOR, "");
        String aliveCountColor = JsonUtils.getJsonString(jsonObject, DisplayConfigTag.ALIVE_COUNT_COLOR, "");

        return new GameEntry(display, xRatio, yRatio, aliveColor, aliveCountColor);
    }

    @Override
    public void applyDefault() {
        GameInfoRenderer.setDisplayAlive(display);
        if (display) {
            GameInfoRenderer.setAliveXRatio(xRatio);
            GameInfoRenderer.setAliveYRatio(yRatio);
            GameInfoRenderer.setAliveColor(aliveColor);
            GameInfoRenderer.setAliveCountColor(aliveCountColor);
        }
    }
}
