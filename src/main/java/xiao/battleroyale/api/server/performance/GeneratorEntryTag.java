package xiao.battleroyale.api.server.performance;

import xiao.battleroyale.api.config.ConfigEntryTag;

public class GeneratorEntryTag extends ConfigEntryTag {

    public static final String COMMON = "common";
    public static final String LOOT_VANILLA_CHEST = "lootVanillaChest";
    public static final String REMOVE_LOOT_TABLE = "removeLootTable";
    public static final String CLEAR_PREVIOUS_CONTENT = "clearPreviousContent";
    public static final String REMOVE_INNOCENT_ENTITY = "removeNoGameidEntity";
    public static final String VANILLA_WHITE_LIST = "vanillaWhiteList";
    public static final String VANILLA_BLACK_LIST = "vanillaBlackList";

    public static final String NORMAL = "normal";
    public static final String MAX_NORMAL_TICK_LOOT_CHUNK = "maxNormalTickLootChunk";

    public static final String GAME = "game";
    public static final String MAX_GAME_TICK_LOOT_CHUNK = "maxGameTickLootChunk";
    public static final String MAX_GAME_LOOT_DISTANCE = "maxLootDistance";
    public static final String TOLERANT_CENTER_DISTANCE = "bfsTolerantCenterDistance";
    public static final String MAX_CACHED_CENTER = "maxCachedCenter";
    public static final String MAX_QUEUED_CHUNK = "maxQueuedChunk";
    public static final String BFS_FREQUENCY = "bfsFrequency";
    public static final String INSTANT_NEXT_BFS = "instantNextBfs";
    public static final String MAX_CACHED_LOOT_CHUNK = "maxCachedLootChunk";
    public static final String CLEAN_CACHED_CHUNK = "cleanCachedChunk";

    private GeneratorEntryTag() {}
}
