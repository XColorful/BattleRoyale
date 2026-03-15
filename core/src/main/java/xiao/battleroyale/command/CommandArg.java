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
    public static final String PERFORMANCE = "performance";
    public static final String PROFILE = "profile";
    public static final String UTILITY = "utility";
    public static final String EXAMPLE = "example";
    public static final String SAVE = "save";
    public static final String BACKUP = "backup";
    public static final String REGISTER = "register";
    public static final String TEMP = "temp";
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
    public static final String SKIP_NON_EMPTY = "skipNonEmpty";
    public static final String DROP_BEFORE_REPLACE = "dropBeforeReplace";
    public static final String FIRST_SLOT_INDEX = "firstSlotIndex";
    public static final String LAST_SLOT_INDEX = "lastSlotIndex";
    public static final String OVERWRITE = "overwrite";
    public static final String GAME_TEAM_ONLY = "gameTeamOnly";

    public static final String BOOL = "boolean";

    // --------API--------
    public static final String API = "api";

    public static final String NAMESPACE = "namespace";
    public static final String PATH = "path";
    public static final String STORAGE_PATH = "storagePath";
    public static final String BY_PLAYER = "byPlayer";
    public static final String BY_ID = "byId";
    public static final String WITH_MEMBERS = "withMembers";
    public static final String WITH_TEAM = "withTeam";
    public static final String DETAIL_LEVEL = "detailLevel";

    // GameManager
    public static final String GAME_MANAGER = "gameMamager";
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

    // TeamManager
    public static final String TEAM_MANAGER = "teamMamager";
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
}
