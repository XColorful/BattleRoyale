package xiao.battleroyale.config.common.game.spawn.type.detail;

import com.google.gson.JsonObject;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;

public abstract class AbstractDetailEntry<T extends AbstractDetailEntry<T>> {

    public AbstractDetailEntry() {
    }

    public abstract T copy();

    public abstract void toJson(JsonObject jsonObject, SpawnShapeType shapeType, CommonDetailType detailType);

    public static <T extends AbstractDetailEntry<T>> T fromJson(JsonObject jsonObject) {
        return null;
    }
}
