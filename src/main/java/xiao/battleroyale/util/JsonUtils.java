package xiao.battleroyale.util;

import com.google.gson.*;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.config.common.game.spawn.type.SpawnEntryType;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.config.common.loot.type.LootEntryType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return GSON.fromJson(jsonString, clazz);
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize JSON to class {}: {}", clazz.getName(), e.getMessage());
            return null;
        }
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static ILootEntry deserializeLootEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.getAsJsonPrimitive(LootEntryTag.TYPE_NAME).getAsString();
            if (type == null) type = "";
            LootEntryType lootEntryType = LootEntryType.fromName(type);
            if (lootEntryType != null) {
                return lootEntryType.getDeserializer().apply(jsonObject);
            } else {
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize LootEntry: {}", e.getMessage());
            return null;
        }
    }

    public static IZoneFuncEntry deserializeZoneFuncEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.getAsJsonPrimitive(ZoneFuncTag.TYPE_NAME).getAsString();
            if (type == null) type = "";
            ZoneFuncType zoneFuncType = ZoneFuncType.fromName(type);
            if (zoneFuncType != null) {
                return zoneFuncType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.error("Unknown ZoneFuncEntry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize ZoneFuncEntry: {}", e.getMessage());
            return null;
        }
    }

    public static IZoneShapeEntry deserializeZoneShapeEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.getAsJsonPrimitive(ZoneShapeTag.TYPE_NAME).getAsString();
            if (type == null) type = "";
            ZoneShapeType zoneShapeType = ZoneShapeType.fromName(type);
            if (zoneShapeType != null) {
                return zoneShapeType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.error("Unknown ZoneShapeEntry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize ZoneShapeEntry: {}", e.getMessage());
            return null;
        }
    }

    public static ISpawnEntry deserializeSpawnEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.has(SpawnTypeTag.TYPE_NAME) ? jsonObject.getAsJsonPrimitive(SpawnTypeTag.TYPE_NAME).getAsString() : "";
            if (type == null) type = "";
            SpawnEntryType spawnEntryType = SpawnEntryType.fromNames(type);
            if (spawnEntryType != null) {
                return spawnEntryType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.warn("Skipped unknown spawn entry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize SpawnEntry: {}", e.getMessage());
            return null;
        }
    }

    public static BattleroyaleEntry deserializeBattleroyaleEntry(JsonObject jsonObject) {
        try {
            BattleroyaleEntry brEntry = BattleroyaleEntry.fromJson(jsonObject);
            if (brEntry != null) {
                return brEntry;
            } else {
                BattleRoyale.LOGGER.warn("Skipped invalid BattleroyaleEntry");
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize BattleroyaleEntry: {}", e.getMessage());
            return null;
        }
    }

    public static MinecraftEntry deserializeMinecraftEntry(JsonObject jsonObject) {
        try {
            MinecraftEntry mcEntry = MinecraftEntry.fromJson(jsonObject);
            if (mcEntry != null) {
                return mcEntry;
            } else {
                BattleRoyale.LOGGER.warn("Skipped invalid MinecraftEntry");
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize MinecraftEntry: {}", e.getMessage());
            return null;
        }
    }

    public static void writeJsonToFile(String filePath, JsonArray jsonArray) {
        Path path = Paths.get(filePath);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                BattleRoyale.LOGGER.warn("Failed to create default config directory: {}", e.getMessage());
                return;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(path, gson.toJson(jsonArray));
            BattleRoyale.LOGGER.info("Generated default config at: {}", path);
        } catch (IOException e) {
            BattleRoyale.LOGGER.warn("Failed to write default config: {}", e.getMessage());
        }
    }

    public static List<Vec3> readVec3ListFromJson(JsonArray jsonArray) {
        List<Vec3> vec3List = new ArrayList<>();

        try {
            for (JsonElement element : jsonArray) {
                String vec3String = element.getAsJsonPrimitive().getAsString();
                Vec3 v = StringUtils.parseVectorString(vec3String);
                if (v != null) {
                    vec3List.add(v);
                }
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.warn("Failed to read Vec3 list from json");
        }

        return vec3List;
    }

    public static JsonArray writeVec3ListToJson(List<Vec3> vec3List) {
        JsonArray jsonArray = new JsonArray();

        for (Vec3 v : vec3List) {
            jsonArray.add(StringUtils.vectorToString(v));
        }

        return jsonArray;
    }
}