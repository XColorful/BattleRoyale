package xiao.battleroyale.config.client.display.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.client.display.DisplayConfigTag;
import xiao.battleroyale.api.config.client.render.IRenderEntry;
import xiao.battleroyale.compat.journeymap.JourneyMap;
import xiao.battleroyale.util.JsonUtils;

public class MapEntry implements IRenderEntry {

    public boolean enableJourneyMap;
    public float lineThickness;

    public MapEntry() {
        this(true, 4);
    }
    public MapEntry(boolean enableJourneyMap, float lineThickness) {
        this.enableJourneyMap = enableJourneyMap;
        this.lineThickness = lineThickness;
    }
    @Override public @NotNull MapEntry copy() {
        return new MapEntry(enableJourneyMap, lineThickness);
    }

    @Override
    public String getType() {
        return "mapEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(DisplayConfigTag.ENABLE_JOURNEY_MAP, enableJourneyMap);
        jsonObject.addProperty(DisplayConfigTag.LINE_THICKNESS, lineThickness);
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
        float lineThickness = (float) JsonUtils.getJsonDouble(jsonObject, DisplayConfigTag.LINE_THICKNESS, 4);

        return new MapEntry(enableJourneyMap, lineThickness);
    }

    @Override
    public void applyDefault() {
        JourneyMap.setJourneyMapConfig(enableJourneyMap, lineThickness);
//        if (enableJourneyMap) {
//            ;
//        }
    }
}
