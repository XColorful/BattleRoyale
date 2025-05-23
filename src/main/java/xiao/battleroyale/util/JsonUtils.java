package xiao.battleroyale.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.config.common.loot.type.LootEntryType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            String type = jsonObject.get(LootEntryTag.TYPE_NAME).getAsString();
            LootEntryType lootEntryType = LootEntryType.fromName(type);
            if (lootEntryType != null) {
                return lootEntryType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.error("Unknown LootEntry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize LootEntry: {}", e.getMessage());
            return null;
        }
    }

    public static IZoneFuncEntry deserializeZoneFuncEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.get(ZoneFuncTag.TYPE_NAME).getAsString();
            ZoneFuncType zoneFuncType = ZoneFuncType.fromName(type);
            if (zoneFuncType != null) {
                return zoneFuncType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.error("Unknown ZoneFuncEntry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize ZoneEntry: {}", e.getMessage());
            return null;
        }
    }

    public static IZoneShapeEntry deserializeZoneShapeEntry(JsonObject jsonObject) {
        try {
            String type = jsonObject.get(ZoneShapeTag.TYPE_NAME).getAsString();
            ZoneShapeType zoneShapeType = ZoneShapeType.fromName(type);
            if (zoneShapeType != null) {
                return zoneShapeType.getDeserializer().apply(jsonObject);
            } else {
                BattleRoyale.LOGGER.error("Unknown ZoneShapeEntry type: {}", type);
                return null;
            }
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("Failed to deserialize ZoneEntry: {}", e.getMessage());
            return null;
        }
    }

    public static void writeJsonToFile(String filePath, JsonArray jsonArray) {
        Path path = Paths.get(filePath);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                System.err.println("Failed to create default config directory: " + e.getMessage());
                return;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.write(path, gson.toJson(jsonArray).getBytes(StandardCharsets.UTF_8));
            System.out.println("Generated default config at: " + path);
        } catch (IOException e) {
            System.err.println("Failed to write default config: " + e.getMessage());
        }
    }
}