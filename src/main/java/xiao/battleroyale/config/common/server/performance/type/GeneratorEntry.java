package xiao.battleroyale.config.common.server.performance.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.IConfigAppliable;
import xiao.battleroyale.api.server.performance.IPerformanceEntry;
import xiao.battleroyale.api.server.performance.GeneratorEntryTag;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.loot.CommonLootManager;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.util.JsonUtils;

import java.util.List;

public class GeneratorEntry implements IPerformanceEntry, IConfigAppliable {

    // common
    public final boolean lootVanillaChest;
    public final boolean removeLootTable;
    public final boolean clearPreviousContent;
    public final boolean removeInnocentEntity;
    public final List<String> whiteListRegex;
    public final List<String> blackListRegex;
    // normal
    public final int maxNormalTickLootChunk;
    // game
    public final int maxGameTickLootChunk;
    public final int maxGameLootDistance;
    public final int tolerantCenterDistance;
    public final int maxCachedCenter;
    public final int maxQueuedChunk;
    public final int bfsFrequency;
    public final boolean instantNextBfs;
    public final int maxCachedLootChunk;
    public final int cleanCachedChunk;

    public GeneratorEntry(boolean lootVanillaChest, boolean removeLootTable, boolean clearPreviousContent, boolean removeInnocentEntity, @NotNull List<String> whiteListRegex, @NotNull List<String> blackListRegex,
                          int maxNormalTickLootChunk,
                          int maxGameTickLootChunk, int maxGameLootDistance, int tolerantCenterDistance, int maxCachedCenter, int maxQueuedChunk, int bfsFrequency, boolean instantNextBfs, int maxCachedLootChunk, int cleanCachedChunk) {
        this.lootVanillaChest = lootVanillaChest;
        this.removeLootTable = removeLootTable;
        this.clearPreviousContent = clearPreviousContent;
        this.removeInnocentEntity = removeInnocentEntity;
        this.whiteListRegex = whiteListRegex;
        this.blackListRegex = blackListRegex;
        this.maxNormalTickLootChunk = maxNormalTickLootChunk;
        this.maxGameTickLootChunk = maxGameTickLootChunk;
        this.maxGameLootDistance = maxGameLootDistance;
        this.tolerantCenterDistance = tolerantCenterDistance;
        this.maxCachedCenter = maxCachedCenter;
        this.maxQueuedChunk = maxQueuedChunk;
        this.bfsFrequency = bfsFrequency;
        this.instantNextBfs = instantNextBfs;
        this.maxCachedLootChunk = maxCachedLootChunk;
        this.cleanCachedChunk = cleanCachedChunk;
    }

    @Override
    public String getType() {
        return "generatorEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(GeneratorEntryTag.COMMON, generateCommonJson());
        jsonObject.add(GeneratorEntryTag.NORMAL, generateNormalJson());
        jsonObject.add(GeneratorEntryTag.GAME, generateGameJson());
        return jsonObject;
    }

    @Nullable
    public static GeneratorEntry fromJson(JsonObject jsonObject) {
        JsonObject commonObject = JsonUtils.getJsonObject(jsonObject, GeneratorEntryTag.COMMON, null);
        JsonObject normalObject = JsonUtils.getJsonObject(jsonObject, GeneratorEntryTag.NORMAL, null);
        JsonObject gameObject = JsonUtils.getJsonObject(jsonObject, GeneratorEntryTag.GAME, null);
        if (commonObject == null || normalObject == null || gameObject == null) {
            return null;
        }
        // common
        boolean lootVanillaChest = JsonUtils.getJsonBool(commonObject, GeneratorEntryTag.LOOT_VANILLA_CHEST, false);
        boolean removeLootTable = JsonUtils.getJsonBool(commonObject, GeneratorEntryTag.REMOVE_LOOT_TABLE, false);
        boolean clearPreviousContent = JsonUtils.getJsonBool(commonObject, GeneratorEntryTag.CLEAR_PREVIOUS_CONTENT, true);
        boolean removeInnocentEntity = JsonUtils.getJsonBool(commonObject, GeneratorEntryTag.REMOVE_INNOCENT_ENTITY, false);
        List<String> vanillaWhiteList = JsonUtils.getJsonStringList(commonObject, GeneratorEntryTag.VANILLA_WHITE_LIST);
        List<String> vainllaBlackList = JsonUtils.getJsonStringList(commonObject, GeneratorEntryTag.VANILLA_BLACK_LIST);
        // normal
        int maxNormalTickLootChunk = JsonUtils.getJsonInt(normalObject, GeneratorEntryTag.MAX_NORMAL_TICK_LOOT_CHUNK, 5);
        // game
        int maxGameTickLootChunk = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.MAX_GAME_TICK_LOOT_CHUNK, 5);
        int maxGameLootDistance = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.MAX_GAME_LOOT_DISTANCE, 26);
        int tolerantCenterDistance = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.TOLERANT_CENTER_DISTANCE, 3);
        int maxCachedCenter = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.MAX_CACHED_CENTER, 500);
        int maxQueuedChunk = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.MAX_QUEUED_CHUNK, 512 * 50);
        int bfsFrequency = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.BFS_FREQUENCY, 20 * 5);
        boolean instantNextBfs = JsonUtils.getJsonBool(gameObject, GeneratorEntryTag.INSTANT_NEXT_BFS, false);
        int maxCachedLootChunk = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.MAX_CACHED_LOOT_CHUNK, 50000);
        int cleanCachedChunk = JsonUtils.getJsonInt(gameObject, GeneratorEntryTag.CLEAN_CACHED_CHUNK, 2000);

        return new GeneratorEntry(lootVanillaChest, removeLootTable, clearPreviousContent, removeInnocentEntity, vanillaWhiteList, vainllaBlackList,
                maxNormalTickLootChunk,
                maxGameTickLootChunk, maxGameLootDistance, tolerantCenterDistance, maxCachedCenter, maxQueuedChunk, bfsFrequency, instantNextBfs, maxCachedLootChunk, cleanCachedChunk);
    }

    @Override
    public void applyDefault() {
        LootGenerator.setLootVanillaChest(lootVanillaChest);
        LootGenerator.setRemoveLootTable(removeLootTable);
        LootGenerator.setClearPreviousContent(clearPreviousContent);
        LootGenerator.setRemoveInnocentEntity(removeInnocentEntity);
        LootGenerator.setLootBlockFilter(whiteListRegex, blackListRegex);
        CommonLootManager.setMaxChunksPerTick(maxNormalTickLootChunk);
        GameLootManager.get().applyConfig(this);
    }

    @NotNull
    private JsonObject generateCommonJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GeneratorEntryTag.LOOT_VANILLA_CHEST, lootVanillaChest);
        jsonObject.addProperty(GeneratorEntryTag.REMOVE_LOOT_TABLE, removeLootTable);
        jsonObject.addProperty(GeneratorEntryTag.CLEAN_CACHED_CHUNK, clearPreviousContent);
        jsonObject.addProperty(GeneratorEntryTag.REMOVE_INNOCENT_ENTITY, removeInnocentEntity);
        jsonObject.add(GeneratorEntryTag.VANILLA_WHITE_LIST, JsonUtils.writeStringListToJson(whiteListRegex));
        jsonObject.add(GeneratorEntryTag.VANILLA_BLACK_LIST, JsonUtils.writeStringListToJson(blackListRegex));
        return jsonObject;
    }

    @NotNull
    private JsonObject generateNormalJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GeneratorEntryTag.MAX_NORMAL_TICK_LOOT_CHUNK, maxNormalTickLootChunk);
        return jsonObject;
    }

    @NotNull
    private JsonObject generateGameJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GeneratorEntryTag.MAX_GAME_TICK_LOOT_CHUNK, maxGameTickLootChunk);
        jsonObject.addProperty(GeneratorEntryTag.MAX_GAME_LOOT_DISTANCE, maxGameLootDistance);
        jsonObject.addProperty(GeneratorEntryTag.TOLERANT_CENTER_DISTANCE, tolerantCenterDistance);
        jsonObject.addProperty(GeneratorEntryTag.MAX_CACHED_CENTER, maxCachedCenter);
        jsonObject.addProperty(GeneratorEntryTag.MAX_QUEUED_CHUNK, maxQueuedChunk);
        jsonObject.addProperty(GeneratorEntryTag.BFS_FREQUENCY, bfsFrequency);
        jsonObject.addProperty(GeneratorEntryTag.INSTANT_NEXT_BFS, instantNextBfs);
        jsonObject.addProperty(GeneratorEntryTag.MAX_CACHED_LOOT_CHUNK, maxCachedLootChunk);
        jsonObject.addProperty(GeneratorEntryTag.CLEAN_CACHED_CHUNK, cleanCachedChunk);
        return jsonObject;
    }
}