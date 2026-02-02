package xiao.battleroyale.config.common.server.profile.config;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.server.profile.ConfigProfileEntryTag;
import xiao.battleroyale.util.JsonUtils;

public class ConfigEntry {
    public final int folderId;
    public final @NotNull String fileName;
    public final int id;

    public ConfigEntry(String fileName) {
        this(0, fileName, 0);
    }

    public ConfigEntry(String fileName, int id) {
        this(0, fileName, id);
    }

    public ConfigEntry(int folderId, @NotNull String fileName, int id) {
        this.folderId = folderId;
        this.fileName = fileName;
        this.id = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public static ConfigEntry fromJson(int folderId, JsonObject jsonObject) {
        String fileName = JsonUtils.getJsonString(jsonObject, ConfigProfileEntryTag.FILE_NAME, null);
        if (fileName == null) return null;
        int id = JsonUtils.getJsonInt(jsonObject, ConfigProfileEntryTag.ID, 0);
        return new ConfigEntry(folderId, fileName, id);
    }

    public void addToJson(JsonObject jsonObject) {
        JsonObject subObject = new JsonObject();
        subObject.addProperty(ConfigProfileEntryTag.FILE_NAME, fileName);
        subObject.addProperty(ConfigProfileEntryTag.ID, id);
        jsonObject.add(Integer.toString(folderId), subObject);
    }
}
