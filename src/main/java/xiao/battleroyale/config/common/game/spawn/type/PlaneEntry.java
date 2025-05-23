package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;

public class PlaneEntry implements ISpawnEntry {

    @Override
    public String getType() {
        return "plane";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("spawnType", getType());
        return jsonObject;
    }

    public static PlaneEntry fromJson(JsonObject jsonObject) {
        return new PlaneEntry();
    }
}
