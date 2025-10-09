package xiao.battleroyale.config.common.game.spawn.type.detail;

import com.google.gson.JsonObject;

public abstract class AbstractDetailEntry<T extends AbstractDetailEntry<T>> {

    public AbstractDetailEntry() {
    }

    public abstract void toJson(JsonObject jsonObject, CommonDetailType detailType);

    public static <T extends AbstractDetailEntry<T>> T fromJson(JsonObject jsonObject) {
        return null;
    }
}
