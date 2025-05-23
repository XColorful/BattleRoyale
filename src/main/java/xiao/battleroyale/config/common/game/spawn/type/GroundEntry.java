package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;

public class GroundEntry implements ISpawnEntry {

    @Override
    public String getType() {
        return "ground";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("spawnType", getType());
        return jsonObject;
    }

    public static GroundEntry fromJson(JsonObject jsonObject) {
        return new GroundEntry();
    }
}
