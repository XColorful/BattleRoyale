package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.loot.item.IItemLootEntry;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

public class EmptyEntry implements IItemLootEntry {

    @Override
    public List<ItemStack> generateLoot(Supplier<Float> random) {
        return Collections.emptyList(); // 返回一个空的物品堆叠列表，表示不生成任何物品
    }

    @Override
    public String getType() {
        return "empty";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        return jsonObject;
    }

    public static EmptyEntry fromJson(JsonObject jsonObject) {
        return new EmptyEntry();
    }
}