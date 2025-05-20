package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.ILootEntry;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class TimeEntry<T> implements ILootEntry<T> {
    private final int start;
    private final int end;

    public TimeEntry(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<T> generateLoot(Supplier<Float> random) {
        // TODO: 在实际刷新时根据游戏时间判断是否生成战利品
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return "time";
    }

    public static TimeEntry<?> fromJson(JsonObject jsonObject) {
        int start = jsonObject.has("start") ? jsonObject.getAsJsonPrimitive("start").getAsInt() : 0;
        int end = jsonObject.has("end") ? jsonObject.getAsJsonPrimitive("end").getAsInt() : 0;
        return new TimeEntry<>(start, end);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("start", this.start);
        jsonObject.addProperty("end", this.end);
        return jsonObject;
    }
}