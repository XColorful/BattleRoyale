package xiao.battleroyale.config.common.server.utility.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.IConfigAppliable;
import xiao.battleroyale.api.server.utility.IUtilityEntry;

public class SurvivalEntry implements IUtilityEntry, IConfigAppliable {

    public SurvivalEntry() {
        ;
    }

    @Override
    public String getType() {
        return "SurvivalEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        return jsonObject;
    }

    @Nullable
    public static SurvivalEntry fromJson(JsonObject jsonObject) {

        return new SurvivalEntry();
    }

    @Override
    public void applyDefault() {
        ;
    }
}
