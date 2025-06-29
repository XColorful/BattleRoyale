package xiao.battleroyale.config.client.render.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.api.client.render.RenderConfigTag;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.util.JsonUtils;

public class ZoneEntry implements IRenderEntry {

    public final boolean useClientColor;
    public final String fixedColor;

    public ZoneEntry(boolean useClientColor) {
        this(useClientColor, "#0000FF");
    }

    public ZoneEntry(boolean useClientColor, String fixedColor) {
        this.useClientColor = useClientColor;
        this.fixedColor = fixedColor;
    }

    @Override
    public String getType() {
        return "zoneEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RenderConfigTag.USE_CLIENT_COLOR, useClientColor);
        if (useClientColor) {
            jsonObject.addProperty(RenderConfigTag.FIXED_COLOR, fixedColor);
        }

        return jsonObject;
    }

    @NotNull
    public static ZoneEntry fromJson(JsonObject jsonObject) {
        boolean useClientColor = JsonUtils.getJsonBool(jsonObject, RenderConfigTag.USE_CLIENT_COLOR, false);
        String fixedColor = JsonUtils.getJsonString(jsonObject, RenderConfigTag.FIXED_COLOR, "");

        return new ZoneEntry(useClientColor, fixedColor);
    }

    @Override
    public void applyDefault() {
        ClientSingleZoneData.setUseClientColor(this.useClientColor);
        if (useClientColor) {
            ClientSingleZoneData.setClientColorString(this.fixedColor);
        }
    }
}
