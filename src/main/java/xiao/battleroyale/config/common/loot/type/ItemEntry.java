package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.loot.item.IItemLootEntry;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ItemEntry implements IItemLootEntry {
    private final ItemStack itemStack;

    public ItemEntry(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public List<ItemStack> generateLoot(Supplier<Float> random) {
        return Collections.singletonList(this.itemStack.copy());
    }

    @Override
    public String getType() {
        return "item";
    }

    public static ItemEntry fromJson(JsonObject jsonObject) {
        String itemName = jsonObject.getAsJsonPrimitive("item").getAsString();
        int count = jsonObject.has("count") ? jsonObject.getAsJsonPrimitive("count").getAsInt() : 1;
        ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(itemName)), count);
        if (jsonObject.has("nbt")) {
            try {
                CompoundTag nbt = TagParser.parseTag(jsonObject.getAsJsonPrimitive("nbt").getAsString());
                itemStack.setTag(nbt);
            } catch (Exception e) {
                BattleRoyale.LOGGER.warn("Failed to parse NBT for item {}: {}", itemName, e.getMessage());
            }
        }
        return new ItemEntry(itemStack);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()).toString());
        if (this.itemStack.getCount() > 1) {
            jsonObject.addProperty("count", this.itemStack.getCount());
        }
        if (this.itemStack.hasTag()) {
            jsonObject.addProperty("nbt", this.itemStack.getTag().toString());
        }
        return jsonObject;
    }
}