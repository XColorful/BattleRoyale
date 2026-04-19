package xiao.battleroyale.command;

import xiao.battleroyale.BattleRoyale;

public class CommandArg {

    public static final String MOD_ID = BattleRoyale.MOD_ID;
    public static final String MOD_NAME_SHORT = BattleRoyale.MOD_NAME_SHORT;

    public static final String CONFIG = "config";
    public static final String GAME = "game";
    public static final String LOOT = "loot";
    public static final String RELOAD = "reload";
    public static final String TEAM = "team";
    public static final String FIREWORK = "firework";
    public static final String MUTEKI = "muteki";
    public static final String EFFECT = "effect";
    public static final String PARTICLE = "particle";
    public static final String BOOST = "boost";
    public static final String CLIENT = "client";
    public static final String RENDER = "render";
    public static final String DISPLAY = "display";
    public static final String SERVER = "server";
    public static final String FUNCTION = "function";
    public static final String PERFORMANCE = "performance";
    public static final String PROFILE = "profile";
    public static final String UTILITY = "utility";
    public static final String EXAMPLE = "example";
    public static final String SAVE = "save";
    public static final String BACKUP = "backup";
    public static final String REGISTER = "register";
    public static final String TRIGGER = "trigger";
    public static final String TEMP = "temp";
    public static final String ENTITY_SELECTOR = "entitySelector";
    public static final String PUBGMC = "pubgmc";
    public static final String INIT_STACK_ZONE_CONFIG = "initStackZoneConfig";
    public static final String GAME_STEP = "gameStep";

    public static final String LOOT_SPAWNER = "loot_spawner";
    public static final String ENTITY_SPAWNER = "entity_spawner";
    public static final String AIRDROP = "airdrop";
    public static final String AIRDROP_SPECIAL = "airdrop_special";
    public static final String SECRET_ROOM = "secret_room";

    public static final String MANAGER = "manager";
    public static final String PROTOCOL = "protocol";
    public static final String BOT = "bot";
    public static final String GAMERULE = "gamerule";
    public static final String SPAWN = "spawn";
    public static final String STATS = "stats";
    public static final String ZONE = "zone";

    public static final String ID = "id";
    public static final String SWITCH = "switch";
    public static final String FILE = "fileName";

    public static final String GENERATE = "generate";
    public static final String RESET = "reset";
    
    public static final String JOIN = "join";
    public static final String LEAVE = "leave";
    public static final String KICK = "kick";
    public static final String INVITE = "invite";
    public static final String REQUEST = "request";
    public static final String ACCEPT = "accept";
    public static final String DECLINE = "decline";
    public static final String PLAYER = "player";
    public static final String SENDER = "senderName";
    public static final String REQUESTER = "requesterName";
    public static final String TEAM_ID = "teamId";
    public static final String ADD = "add";
    public static final String BUILD = "build";
    public static final String SIZE = "size";
    public static final String FORCE_REBUILD = "forceRebuild";
    public static final String REBUILD = "rebuild";
    public static final String FORMAT_STRING = "formatString";
    public static final String HIDE_NAME = "hideName";

    public static final String XYZ = "xyz";
    public static final String AMOUNT = "amount";
    public static final String INTERVAL = "interval";
    public static final String VERTICAL_RANGE = "vRange";
    public static final String HORIZONTAL_RANGE = "hRange";
    public static final String CLEAR = "clear";
    public static final String TIME = "time";
    public static final String COOLDOWN = "cooldown";
    public static final String ALL = "all";
    public static final String REMOVE = "remove";

    public static final String LOAD = "load";
    public static final String INIT = "init";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String LOBBY = "lobby";
    public static final String TO_LOBBY = "toLobby";
    public static final String OFFSET = "offset";
    public static final String SELECTED = "selected";
    public static final String SPECTATE = "spectate";
    public static final String GAME_ID = "gameId";

    public static final String SURVIVAL_LOBBY = "survivallobby";
    public static final String TO_SURVIVAL_LOBBY = "tosurvivallobby";
    public static final String LOOT_CONFIG = "lootconfig";
    public static final String TYPE = "type";
    public static final String SLOT = "slot";
    public static final String BLOCK = "block";
    public static final String CHUNK = "chunk";
    public static final String TO_LOOT_TABLE = "toLootTable";
    public static final String REPEAT = "repeat";
    public static final String BASE_WEIGHT = "baseWeight";
    public static final String CHUNK_RADIUS = "chunkRadius";
    public static final String AUTO_RELOAD = "autoReload";
    public static final String POS = "pos";
    public static final String ROTATION = "rotation";
    public static final String SKIP_NON_EMPTY = "skipNonEmpty";
    public static final String DROP_BEFORE_REPLACE = "dropBeforeReplace";
    public static final String FIRST_SLOT_INDEX = "firstSlotIndex";
    public static final String LAST_SLOT_INDEX = "lastSlotIndex";
    public static final String OVERWRITE = "overwrite";
    public static final String GAME_TEAM_ONLY = "gameTeamOnly";

    public static final String INT = "int";
    public static final String DOUBLE = "double";
    public static final String BOOL = "boolean";

    // --------API--------
    public static final String API = "api";

    public static final String RESOURCE_LOCATION = "resourceLocation";
    public static final String FILE_PATH = "filePath";
    public static final String STORAGE_PATH = "storagePath";
    public static final String BY_PLAYER = "byPlayer";
    public static final String BY_ID = "byId";
    public static final String WITH_MEMBERS = "withMembers";
    public static final String WITH_TEAM = "withTeam";
    public static final String DETAIL_LEVEL = "detailLevel";
    public static final String PROGRESS = "progress";
    public static final String GAME_PLAYER = "gamePlayer";
    public static final String GAME_PLAYERS = "gamePlayers";
    public static final String STANDING_GAME_PLAYERS = "standingGamePlayers";
    public static final String EVENT_TYPE = "eventType";
    public static final String CUSTOM_EVENT_TYPE = "customEventType";
    public static final String EVENT_CLASS = "eventClass";
    public static final String EVENT_NAME = "event";
    public static final String EVENT_PRIORITY = "eventPriority";
    public static final String RECEIVE_CANCELED = "receiveCanceled";

    // Algorithm
    public static final String ALGORITHM = "algorithm";
    public static final String RECTANGLE_GRID = "rectangleGrid";
    public static final String GOLDEN_SPIRAL = "goldenSpiral";
    public static final String CIRCLE_GRID = "circleGrid";
    public static final String SHUFFLE = "shuffle";
    public static final String RANDOM_RANGE = "randomRange";
    public static final String RANGE_TYPE = "rangeType";
    public static final String RANDOM_ADJUST_XYZ = "randomAdjustXYZ";
    public static final String RANDOM_ADJUST_XZ_EXPAND_Y = "randomAdjustXZExpandY";
    public static final String SCALE_XYZ = "scaleXYZ";
    public static final String RANDOM_CIRCLE_XZ_EXPAND_Y = "randomCircleXZExpandY";
    public static final String RANDOM_SPHERE_XYZ = "randomSphereXYZ";
    public static final String BOUND = "bound";
    public static final String MIN_POINT = "minPoint";
    public static final String MAX_POINT = "maxPoint";
    public static final String COUNT = "count";
    public static final String ALLOW_ON_BORDER = "allowOnBorder";
    public static final String GLOBAL_SHRINK_RATIO = "globalShrinkRatio";
    public static final String TELEPORT = "teleport";
    public static final String FIND_GROUND = "findGround";
    public static final String MAX_HANG_TIME = "maxHangTime";

    // GameManager
    public static final String GAME_MANAGER = "gameManager";
    public static final String GET_GAME_TIME = "getGameTime";
    public static final String IS_IN_GAME = "isInGame";
    public static final String GET_GLOBAL_CENTER_OFFSET = "getGlobalCenterOffset";
    public static final String GET_MAX_GAME_TIME = "getMaxGameTime";
    public static final String GET_WINNER_TEAM_TOTAL = "getWinnerTeamTotal";
    public static final String GET_REQUIRED_GAME_TEAM = "getRequiredGameTeam";
    public static final String HAS_WINNER = "hasWinner";
    public static final String GET_REMAIN_RESTART_TIME = "getRemainRestartTime";
    public static final String IS_WINNER = "isWinner";

    public static final String SEND_GAME_SPECTATE_MESSAGE = "sendGameSpectateMessage";
    public static final String FINISH_GAME = "finishGame";
    public static final String ADD_GAME_TIME_AND_TICK = "addGameTimeAndTick";

    public static final String ADD_FINISH_CHECK_AFTER_DEATH_EVENT = "addFinishCheckAfterDeathEvent";
    public static final String SET_HAS_WINNER = "setHasWinner";
    public static final String CLEAR_WINNER_GAME_PLAYERS = "clearWinnerGamePlayers";
    public static final String CLEAR_WINNER_GAME_TEAMS = "clearWinnerGameTeams";
    public static final String ADD_WINNER_GAME_PLAYER = "addWinnerGamePlayer";
    public static final String ADD_WINNER_GAME_TEAM = "addWinnerGameTeam";
    public static final String SET_REMAIN_RESTART_TIME = "setRemainRestartTime";

    public static final String GET_GAMERULE_CONFIG_ID = "getGameruleConfigId";
    public static final String GET_SPAWN_CONFIG_ID = "getSpawnConfigId";
    public static final String GET_STATS_CONFIG_ID = "getStatsConfigId";
    public static final String GET_BOT_CONFIG_ID = "getBotConfigId";

    public static final String SAVE_TELEPORT = "saveTeleport";

    // GameruleManager
    public static final String GAMERULE_MANAGER = "gameruleManager";
    public static final String GET_GAME_MODE = "getGameMode";

    // GameLobbyManager
    public static final String GAME_LOBBY_MANAGER = "gameLobbyManager";
    public static final String SEND_LOBBY_TELEPORT_MESSAGE = "sendLobbyTeleportMessage";
    public static final String IS_LOBBY_CREATED = "isLobbyCreated";
    public static final String LOBBY_MUTEKI = "lobbyMuteki";
    public static final String LOBBY_HEAL = "lobbyHeal";
    public static final String LOBBY_CHANGE_GAMEMODE = "lobbyChangeGameMode";
    public static final String TELEPORT_DROP_INVENTORY = "teleportDropInventory";
    public static final String TELEPORT_CLEAR_INVENTORY = "teleportClearInventory";
    public static final String IS_IN_LOBBY_RANGE = "isInLobbyRange";
    public static final String CAN_MUTEKI = "canMuteki";

    public static final String HEAL_PLAYER = "healPlayer";
    public static final String TELEPORT_TO_LOBBY = "teleportToLobby";
    public static final String SET_LOBBY = "setLobby";

    // GameLootManager
    public static final String GAME_LOOT_MANAGER = "gameLootManager";
    public static final String GET_MAX_LOOT_CHUNK_PER_TICK = "getMaxLootChunkPerTick";
    public static final String GET_MAX_LOOT_DISTANCE = "getMaxLootDistance";
    public static final String GET_TOLERANT_CENTER_DISTANCE = "getTolerantCenterDistance";
    public static final String GET_MAX_CACHED_CENTER = "getMaxCachedCenter";
    public static final String GET_MAX_QUEUED_CHUNK = "getMaxQueuedChunk";
    public static final String GET_BFS_FREQUENCY = "getBfsFrequency";
    public static final String IS_INSTANT_NEXT_BFS = "isInstantNextBfs";
    public static final String GET_MAX_CACHED_LOOT_CHUNK = "getMaxCachedLootChunk";
    public static final String GET_CLEAN_CACHED_CHUNK = "getCleanCachedChunk";
    public static final String GET_SIMULATION_DISTANCE = "getSimulationDistance";

    public static final String GET_LAST_BFS_TIME = "getLastBfsTime";
    public static final String GET_LAST_BFS_PROCESSED_LOOT = "getLastBfsProcessedLoot";
    public static final String QUEUED_CHUNKS_REF_SIZE = "queuedChunksRefSize";
    public static final String PROCESSED_CHUNK_CACHE_SIZE = "processedChunkCacheSize";
    public static final String CACHED_PLAYER_CENTER_CHUNKS_SIZE = "cachedPlayerCenterChunksSize";
    public static final String CACHED_CENTER_OFFSET_SIZE = "cachedCenterOffsetSize";

    public static final String IS_IN_QUEUED_CHUNKS_REF = "isInQueuedChunksRef";
    public static final String IS_IN_PROCESSED_CHUNK_CACHE = "isInProcessedChunkCache";
    public static final String IS_IN_CACHED_CENTER_OFFSET = "isInCachedCenterOffset";

    public static final String FORCE_CLEAR_QUEUED_CHUNK_REF = "forceClearQueuedChunkRef";
    public static final String FORCE_CLEAR_PROCESSED_CHUNK_CACHE = "forceClearProcessedChunkCache";
    public static final String FORCE_CLEAR_PLAYER_CENTER_CHUNKS = "forceClearPlayerCenterChunks";

    // GameProcessManager
    public static final String GAME_PROCESS_MANAGER = "gameProcessManager";
    public static final String CHECK_IF_GAME_SHOULD_END_AND_FINISH = "checkIfGameShouldEndAndFinish";
    public static final String FINISH_GAME_IF_SHOULD_END = "finishGameIfShouldEnd";

    public static final String CHECK_AND_UPDATE_INVALID_GAME_PLAYER = "checkAndUpdateInvalidGamePlayer";
    public static final String TELEPORT_TO_LOBBY_IN_GAME = "teleportToLobbyInGame";
    public static final String TELEPORT_AFTER_GAME = "teleportAfterGame";
    public static final String TELEPORT_WINNER = "teleportWinner";
    public static final String TELEPORT_NON_WINNER = "teleportNonWinner";
    public static final String SPECTATE_GAME = "spectateGame";
    public static final String HEAL_GAME_PLAYERS = "healGamePlayers";
    public static final String FINISH_GAME_ADD_WINNER = "finishGameAddWinner";

    public static final String SEND_WINNER_RESULT = "sendWinnerResult";
    public static final String NOTIFY_WINNER = "notifyWinner";
    public static final String ALLOW_SPECTATE = "allowSpectate";
    public static final String SEND_DOWN_MESSAGE = "sendDownMessage";
    public static final String SEND_REVIVE_MESSAGE = "sendReviveMessage";
    public static final String SEND_ELIMINATE_MESSAGE = "sendEliminateMessage";

    public static final String DEATH_MATCH = "deathMatch";
    public static final String GET_CURRENT_MAX_KILL = "getCurrentMaxKill";
    public static final String ADD_GAME_PLAYER_KILL = "addGamePlayerKill";
    public static final String ADD_GAME_TEAM_KILL = "addGameTeamKill";
    public static final String ADD_KILL = "addKill";
    public static final String ADD_AND_TRACK_RESTANDING_GAME_PLAYER = "addAndTrackRestandingGamePlayer";
    public static final String RESPAWN_GAME_PLAYER = "respawnGamePlayer";

    // SpawnManager
    public static final String SPAWN_MANAGER = "spawnManager";
    public static final String RESPAWN = "respawn";

    // StatsManager
    public static final String STATS_MANAGER = "statsManager";
    public static final String SHOULD_RECORD_STATS = "shouldRecordStats";
    public static final String IS_IN_RECORD_GAME_PLAYERS = "isInRecordGamePlayers";
    public static final String SAVE_STATS = "saveStats";

    // TeamManager
    public static final String TEAM_MANAGER = "teamManager";
    public static final String SHOULD_AUTO_JOIN = "shouldAutoJoin";
    public static final String FIND_NOT_FULL_TEAM_ID = "findNotFullTeamId";
    public static final String HAS_ENOUGH_PLAYER_TEAM_TO_START = "hasEnoughPlayerTeamToStart";

    public static final String GET_PLAYER_LIMIT = "getPlayerLimit";
    public static final String GET_GAME_PLAYER_ID = "getGamePlayerId";
    public static final String GET_GAME_PLAYER = "getGamePlayer";
    public static final String HAS_STANDING_GAMEPLAYER = "hasStandingGamePlayer";
    public static final String ONLY_REMAIN_BOT_TEAM = "onlyRemainBotTeam";
    public static final String GET_GAME_TEAM_ID = "getGameTeamId";
    public static final String GET_GAME_TEAM = "getGameTeam";
    public static final String GET_GAME_PLAYERS_TOTAL = "getGamePlayersTotal";
    public static final String GET_GAME_PLAYERS = "getGamePlayers";
    public static final String GET_GAME_TEAMS_TOTAL = "getGameTeamsTotal";
    public static final String GET_GAME_TEAMS = "getGameTeams";
    public static final String GET_STANDING_GAME_PLAYERS_TOTAL = "getStandingGamePlayersTotal";
    public static final String GET_STANDING_GAME_PLAYERS = "getStandingGamePlayers";
    public static final String GET_STANDING_GAME_TEAMS_TOTAL = "getStandingGameTeamsTotal";
    public static final String GET_STANDING_GAME_TEAMS = "getStandingGameTeams";
    public static final String GET_RANDOM_STANDING_GAME_PLAYER_ID = "getRandomStandingGamePlayerId";
    public static final String GET_NON_BOT_TEAM_COUNT = "getNonBotTeamCount";
    public static final String GET_STANDING_PLAYER_TEAM_COUNT = "getStandingPlayerTeamCount";

    public static final String FORCE_ELIMINATE_PLAYER_SILENCE = "forceEliminatePlayerSilence";
    public static final String FORCE_ELIMINATE_PLAYER_FROM_TEAM = "forceEliminatePlayerFromTeam";

    public static final String FORCE_JOIN_TEAM = "forceJoinTeam";
    public static final String REMOVE_PLAYER_FROM_TEAM = "removePlayerFromTeam";

    public static final String SEND_PLAYER_TEAM_ID = "sendPlayerTeamId";

    public static final String CLEAR_VANILLA_TEAM = "clearVanillaTeam";

    // ZoneManager
    public static final String ZONE_MANAGER = "zoneManager";
    public static final String HAS_ENOUGH_ZONE_TO_START = "hasEnoughZoneToStart";
    public static final String RANDOMIZE_ZONE_TICK_OFFSET = "randomizeZoneTickOffset";

    public static final String GET_GAME_ZONE = "getGameZone";
    public static final String GET_ZONE_DELAY = "getZoneDelay";
    public static final String IS_CREATED = "isCreated";
    public static final String IS_PRESENT = "isPresent";
    public static final String IS_FINISHED = "isFinished";
    public static final String TICKABLE_ZONE = "tickableZone";
    public static final String IS_READY = "isReady";
    public static final String GET_TICK_FREQUENCY = "getTickFrequency";
    public static final String SET_TICK_FREQUENCY = "setTickFrequency";
    public static final String TICK_FREQ = "tickFreq";
    public static final String GET_TICK_OFFSET = "getTickOffset";
    public static final String SET_TICK_OFFSET = "setTickOffset";
    public static final String TICK_OFFSET = "tickOffset";
    public static final String PLAYER_FUNC = "playerFunc";
    public static final String GET_SHAPE_MOVE_DELAY = "getShapeMoveDelay";
    public static final String GET_SHAPE_MOVE_TIME = "getShapeMoveTime";
    public static final String SPATIAL_ZONE = "spatialZone";
    public static final String IS_WITHIN_ZONE = "isWithinZone";
    public static final String IS_DETERMINED = "isDetermined";
    public static final String HAS_BAD_SHAPE = "hasBadShape";
    public static final String GET_SEGMENTS = "getSegments";

    // FunctionManager
    public static final String FUNCTION_MANAGER = "functionManager";
    public static final String CLEAR_CONFIG_FUNCTION = "clearConfigFunction";
    public static final String CLEAR_API_FUNCTION = "clearApiFunction";
    public static final String REGISTER_EVENT = "registerEvent";
    public static final String UNREGISTER_EVENT = "unregisterEvent";
    public static final String IS_TAG = "isTag";
}
