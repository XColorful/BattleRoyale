package xiao.battleroyale.config.client.display.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.client.display.DisplayConfigTag;
import xiao.battleroyale.api.client.render.IRenderEntry;

public abstract class AbstractHudEntry implements IRenderEntry {

    public boolean display;
    public double xRatio;
    public double yRatio;

    public AbstractHudEntry(boolean display) {
        this(display, 0, 0);
    }

    public AbstractHudEntry(boolean display, double xRatio, double yRatio) {
        this.display = display;
        this.xRatio = xRatio;
        this.yRatio = yRatio;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DisplayConfigTag.SHOULD_DISPLAY, display);
        if (display) {
            jsonObject.addProperty(DisplayConfigTag.HUD_X_RATIO, xRatio);
            jsonObject.addProperty(DisplayConfigTag.HUD_Y_RATIO, yRatio);
        }
        return jsonObject;
    }
}
