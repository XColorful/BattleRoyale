package xiao.battleroyale.config.common.server.performance.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.IConfigAppliable;
import xiao.battleroyale.api.server.performance.IPerformanceEntry;
import xiao.battleroyale.api.server.performance.GeneratorEntryTag;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.loot.CommonLootManager;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.util.JsonUtils;

public class GeneratorEntry implements IPerformanceEntry, IConfigAppliable {

    // common
    public final boolean lootVanillaChest;
    public final boolean removeLootTable;
    public final boolean removeInnocentEntity;
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

    public GeneratorEntry(boolean lootVanillaChest, boolean removeLootTable, boolean removeInnocentEntity,
                          int maxNormalTickLootChunk,
                          int maxGameTickLootChunk, int maxGameLootDistance, int tolerantCenterDistance, int maxCachedCenter, int maxQueuedChunk, int bfsFrequency, boolean instantNextBfs, int maxCachedLootChunk, int cleanCachedChunk) {
        this.lootVanillaChest = lootVanillaChest;
        this.removeLootTable = removeLootTable;
        this.removeInnocentEntity = removeInnocentEntity;
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

    public static GeneratorEntry calculateRecommendedConfig(boolean lootVanillaChest, boolean removeLootTable, boolean removeInnocentEntity,
                                                            int maxNormalTickLootChunk,
                                                            int players, int simulationDistance, int bfsProcessTick, boolean instantNextBfs, double spaceFactor) {
        // 估算一个玩家周围距离为N的区块数：(2 * N + 1)^2
        int chunksPerPlayer = (int) Math.pow(2 * simulationDistance + 1, 2);
        int totalChunksToProcess = players * chunksPerPlayer;

        // 重新计算每Tick需要处理的区块数，基于新的bfsProcessTick
        int maxGameTickLootChunk = (int) Math.ceil(totalChunksToProcess / (double) bfsProcessTick);
        maxGameTickLootChunk = Math.max(5, maxGameTickLootChunk);

        // 扩大队列和缓存基数，以适应高配置和玩家数
        int maxQueuedChunk = (int) (80000 * spaceFactor);
        int maxCachedLootChunk = (int) (120000 * spaceFactor);
        int cleanCachedChunk = (int) (5000 * spaceFactor);
        int maxCachedCenter = (int) (2000 * spaceFactor);

        maxQueuedChunk = Math.max(100, maxQueuedChunk);
        maxCachedLootChunk = Math.max(100, maxCachedLootChunk);
        cleanCachedChunk = Math.max(10, cleanCachedChunk);
        maxCachedCenter = Math.max(players * 5, maxCachedCenter);

        int tolerantCenterDistance = 3;

        return new GeneratorEntry(lootVanillaChest, removeLootTable, removeInnocentEntity,
                maxNormalTickLootChunk,
                maxGameTickLootChunk, simulationDistance, tolerantCenterDistance, maxCachedCenter, maxQueuedChunk, bfsProcessTick, instantNextBfs, maxCachedLootChunk, cleanCachedChunk);
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
        boolean removeInnocentEntity = JsonUtils.getJsonBool(commonObject, GeneratorEntryTag.REMOVE_INNOCENT_ENTITY, false);
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

        return new GeneratorEntry(lootVanillaChest, removeLootTable, removeInnocentEntity,
                maxNormalTickLootChunk,
                maxGameTickLootChunk, maxGameLootDistance, tolerantCenterDistance, maxCachedCenter, maxQueuedChunk, bfsFrequency, instantNextBfs, maxCachedLootChunk, cleanCachedChunk);
    }

    @Override
    public void applyDefault() {
        LootGenerator.setLootVanillaChest(lootVanillaChest);
        LootGenerator.setRemoveLootTable(removeLootTable);
        LootGenerator.setRemoveInnocentEntity(removeInnocentEntity);
        CommonLootManager.setMaxChunksPerTick(maxNormalTickLootChunk);
        GameLootManager.get().applyConfig(this);
    }

    @NotNull
    private JsonObject generateCommonJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GeneratorEntryTag.LOOT_VANILLA_CHEST, lootVanillaChest);
        jsonObject.addProperty(GeneratorEntryTag.REMOVE_LOOT_TABLE, removeLootTable);
        jsonObject.addProperty(GeneratorEntryTag.REMOVE_INNOCENT_ENTITY, removeInnocentEntity);
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