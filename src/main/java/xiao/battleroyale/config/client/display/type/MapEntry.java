package xiao.battleroyale.config.client.display.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.client.display.DisplayConfigTag;
import xiao.battleroyale.api.client.render.IRenderEntry;
import xiao.battleroyale.compat.journeymap.JourneyMap;
import xiao.battleroyale.util.JsonUtils;

public class MapEntry implements IRenderEntry {

    public final boolean enableJourneyMap;

    public MapEntry() {
        this(true);
    }

    public MapEntry(boolean enableJourneyMap) {
        this.enableJourneyMap = enableJourneyMap;
    }

    @Override
    public String getType() {
        return "mapEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(DisplayConfigTag.ENABLE_JOURNEY_MAP, enableJourneyMap);
//        if (enableJourneyMap) {
//            ;
//        }
        return jsonObject;
    }

    public static MapEntry fromJson(JsonObject jsonObject) {
        if (jsonObject == null) {
            return new MapEntry();
        }

        boolean enableJourneyMap = JsonUtils.getJsonBool(jsonObject, DisplayConfigTag.ENABLE_JOURNEY_MAP, true);

        return new MapEntry(enableJourneyMap);
    }

    @Override
    public void applyDefault() {
        JourneyMap.setJourneyMapConfig(enableJourneyMap);
//        if (enableJourneyMap) {
//            ;
//        }
    }
}
