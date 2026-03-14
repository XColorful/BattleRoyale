package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.loot.LootEntryTag;
import xiao.battleroyale.api.config.common.loot.item.IItemLootEntry;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.api.minecraft.ComponentsTag;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.data.ItemData;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.NBTUtils;

import java.util.Collections;
import java.util.List;

public class ItemEntry extends AbstractLootEntry implements IItemLootEntry {
    public String itemString;
    public @Nullable String nbtString;
    public @NotNull CompoundTag nbt;
    public int count;

    public ItemEntry(String rl, @Nullable String nbtString, int count) {
        this.itemString = rl;
        this.nbtString = nbtString;
        this.nbt = NBTUtils.stringToNBT(nbtString);
        this.count = count;
    }
    @Override public @NotNull ItemEntry copy() {
        return new ItemEntry(itemString, nbtString, count);
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        return Collections.singletonList(new ItemData(this.itemString, this.nbt.copy(), this.count));
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_ITEM;
    }

    @NotNull
    public static ItemEntry fromJson(JsonObject jsonObject) {
        String itemName = JsonUtils.getJsonString(jsonObject, LootEntryTag.ITEM, "");
        int count = JsonUtils.getJsonInt(jsonObject, LootEntryTag.COUNT, 1);
        String nbtString = JsonUtils.getJsonString(jsonObject, LootEntryTag.NBT, null);
        return new ItemEntry(itemName, nbtString, count);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.ITEM, this.itemString);
        if (this.count >= 0) {
            jsonObject.addProperty(LootEntryTag.COUNT, this.count);
        }
        if (this.nbtString != null) {
            jsonObject.addProperty(LootEntryTag.NBT, this.nbtString);
        }
        return jsonObject;
    }

    public EntityEntry toEntityEntry() {
        String validNbtString = "components:{}";
        if (nbtString != null && !nbtString.isEmpty() && !nbtString.equals("{}") && nbtString.length() > 2) {
            validNbtString = nbtString.substring(1, nbtString.length() - 1); // 去掉最外层的{}
        }
        return new EntityEntry("minecraft:item", String.format("{Item:{%s,count:%s,id:\"%s\"}}", validNbtString, count, itemString), 1, 0);
    }

    @Override
    public JsonObject toLootTable() {
        /*
            {
                "type": "minecraft:item",
                "name": "minecraft:grass_block",
                "functions": [
                    {
                        "function": "minecraft:set_count",
                        "count": 64
                    },
                    {
                        "function": "minecraft:set_components",
                        "components": {
                            "minecraft:custom_data": "{...}"
                        }
                    }
                ]
            }
        */

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", this.itemString);

        JsonArray functions = new JsonArray();

        // 处理物品数量
        if (this.count > 0) {
            JsonObject setCount = new JsonObject();
            setCount.addProperty("function", "minecraft:set_count");
            setCount.addProperty("count", this.count);
            functions.add(setCount);
        }

        // 处理 Data Components (参考 ItemData 实现逻辑)
        if (this.nbtString != null && !this.nbtString.isEmpty() && !this.nbtString.equals("{}")) {
            JsonObject setComponents = new JsonObject();
            setComponents.addProperty("function", "minecraft:set_components");

            JsonObject componentsObj = new JsonObject();
            CompoundTag tempNbt = this.nbt.copy();

            // 如果包含 components 字段，则将其内部的 key 抽离
            if (tempNbt.contains(ComponentsTag.COMPONENTS)) {
                CompoundTag componentsNbt = tempNbt.getCompound(ComponentsTag.COMPONENTS);
                tempNbt.remove(ComponentsTag.COMPONENTS);

                // 将 components 里的所有组件 key 放入 JsonObject
                for (String key : componentsNbt.getAllKeys()) {
                    componentsObj.add(key, new JsonObject());
                }

                // 剩余部分移到 minecraft:custom_data
                if (!tempNbt.isEmpty()) {
                    componentsObj.addProperty(ComponentsTag.CUSTOM_DATA, tempNbt.toString());
                }
            } else {
                // 全部放进 custom_data
                componentsObj.addProperty(ComponentsTag.CUSTOM_DATA, this.nbtString);
            }

            setComponents.add("components", componentsObj);
            functions.add(setComponents);
        }

        if (!functions.isEmpty()) {
            entry.add("functions", functions);
        }

        return entry;
    }
}