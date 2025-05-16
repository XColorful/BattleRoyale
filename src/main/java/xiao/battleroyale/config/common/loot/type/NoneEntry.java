package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NoneEntry<T> implements ILootEntry<T> {

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        return Collections.emptyList(); // 返回一个空的列表，表示不生成任何东西
    }

    @Override
    public String getType() {
        return "none";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        return jsonObject;
    }

    public static <T> NoneEntry<T> fromJson(JsonObject jsonObject) {
        return new NoneEntry<>();
    }
}