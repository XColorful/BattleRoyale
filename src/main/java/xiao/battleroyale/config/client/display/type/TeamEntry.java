package xiao.battleroyale.config.client.display.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.client.display.DisplayConfigTag;
import xiao.battleroyale.client.renderer.game.TeamInfoRenderer;
import xiao.battleroyale.util.JsonUtils;

public class TeamEntry extends AbstractHudEntry {

    public final int offlineTime;

    public TeamEntry(boolean display) {
        this(display, 0, 0, 20 * 8);
    }

    public TeamEntry(boolean display, double xRatio, double yRatio, int offlineTime) {
        super(display, xRatio, yRatio);
        this.offlineTime = offlineTime;
    }

    @Override
    public String getType() {
        return "teamEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        if (display) {
            jsonObject.addProperty(DisplayConfigTag.OFFLINE_TIME, offlineTime);
        }
        return jsonObject;
    }

    public static TeamEntry fromJson(JsonObject jsonObject) {
        boolean display = JsonUtils.getJsonBool(jsonObject, DisplayConfigTag.SHOULD_DISPLAY, false);
        if (!display) {
            return new TeamEntry(display);
        }
        double xRatio = JsonUtils.getJsonDouble(jsonObject, DisplayConfigTag.HUD_X_RATIO, 0);
        double yRatio = JsonUtils.getJsonDouble(jsonObject, DisplayConfigTag.HUD_Y_RATIO, 0);

        int offlineTime = JsonUtils.getJsonInt(jsonObject, DisplayConfigTag.OFFLINE_TIME, 20 * 8);

        return new TeamEntry(display, xRatio, yRatio, offlineTime);
    }

    @Override
    public void applyDefault() {
        TeamInfoRenderer.setDisplayTeam(display);
        if (display) {
            TeamInfoRenderer.setXRatio(xRatio);
            TeamInfoRenderer.setYRatio(yRatio);
            TeamInfoRenderer.setOfflineTimeLimit(offlineTime);
        }
    }
}
