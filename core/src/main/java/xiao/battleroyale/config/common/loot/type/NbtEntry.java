package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.NBTUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO 做成新模组？NbtEdit
public class NbtEntry implements ILootEntry {
    public boolean overwrite;
    public JsonObject nbtJson;
    public @NotNull CompoundTag nbt;
    public final List<String> keyDelete;

    public NbtEntry(boolean overwrite, JsonObject nbtJson, List<String> keyDelete) {
        this.overwrite = overwrite;
        this.nbtJson = nbtJson;
        this.nbt = NBTUtils.JsonToNBT(nbtJson);
        this.keyDelete = keyDelete;
    }
    @Override public @NotNull NbtEntry copy() {
        return new NbtEntry(overwrite, nbtJson, new ArrayList<>(keyDelete));
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        ;
        return Collections.emptyList();
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_NBT;
    }

    @NotNull static NbtEntry fromJson(JsonObject jsonObject) {
        boolean overwrite = JsonUtils.getJsonBool(jsonObject, LootEntryTag.OVERWRITE, false);
        JsonObject nbtJson = JsonUtils.getJsonObject(jsonObject, LootEntryTag.NBT, new JsonObject());
        List<String> keyDelete = JsonUtils.getJsonStringList(jsonObject, LootEntryTag.KEY_DELETE);
        return new NbtEntry(overwrite, nbtJson, keyDelete);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LootEntryTag.TYPE_NAME, getType());
        jsonObject.addProperty(LootEntryTag.OVERWRITE, overwrite);
        jsonObject.add(LootEntryTag.NBT, nbtJson);
        jsonObject.add(LootEntryTag.KEY_DELETE, JsonUtils.writeStringListToJson(keyDelete));
        return jsonObject;
    }
}
