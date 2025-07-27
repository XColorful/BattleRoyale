package xiao.battleroyale.developer.debug.command;

import static xiao.battleroyale.command.CommandArg.MOD_ID;
import static xiao.battleroyale.command.CommandArg.MOD_NAME_SHORT;

public class CommandArg {

    public static final String DEBUG_MOD = MOD_ID;
    public static final String DEBUG_MOD_SHORT = MOD_NAME_SHORT;
    public static final String DEBUG_MOD_LOCAL = "l" + DEBUG_MOD;
    public static final String DEBUG_MOD_LOCAL_SHORT = "l" + DEBUG_MOD_SHORT;

    // debug
    public static final String DEBUG = "debug";
    public static final String DEBUG_SHORT = "db";
    public static final String DEBUG_LOCAL = "localdebug";

    // operation
    public static final String GET = "get";
    public static final String CLEAR = "clear";

    public static final String SINGLE_ID = "id";
    public static final String NAME = "name";
    public static final String ALL = "all";
    public static final String ID_MIN = "min";
    public static final String ID_MAX = "max";
    public static final String XYZ = "xyz";
    public static final String ENTITY = "entity";

    // GamePlayer & GameTeam
    public static final String GAME_PLAYERS = "gameplayers";
    public static final String GAME_PLAYERS_SHORT = "gps";
    public static final String GAME_PLAYER = "gameplayer";
    public static final String GAME_PLAYER_SHORT = "gp";
    public static final String GAME_TEAMS = "gameteams";
    public static final String GAME_TEAMS_SHORT = "gts";
    public static final String GAME_TEAM = "gameteam";
    public static final String GAME_TEAM_SHORT = "gt";
    // GameZone
    public static final String GAME_ZONES = "gamezones";
    public static final String GAME_ZONES_SHORT = "gzs";
    public static final String GAME_ZONE = "gamezone";
    public static final String GAME_ZONE_SHORT = "gz";
    // Loot Manager
    public static final String COMMON_LOOT = "commonloot";
    public static final String COMMON_LOOT_SHORT = "cl";
    public static final String GAME_LOOT = "gameloot";
    public static final String GAME_LOOT_SHORT = "gl";
    // BR gamerule
    public static final String BACKUP_PLAYER_MODES = "backupplayermodes";
    public static final String BACKUP_PLAYER_MODES_SHORT = "bpms";
    public static final String BACKUP_PLAYER_MODE = "backupplayermode";
    public static final String BACKUP_PLAYER_MODE_SHORT = "bpm";
    public static final String BACKUP_GAMERULE = "backupgamerule";
    public static final String BACKUP_GAMERULE_SHORT = "bgr";
    // BlockEntity
    public static final String BLOCK_ENTITIES_NBT = "blockentitiesnbt";
    public static final String BLOCK_ENTITIES_NBT_SHORT = "besnbt";
    public static final String BLOCK_ENTITY_NBT = "blockentitynbt";
    public static final String BLOCK_ENTITY_NBT_SHORT = "benbt";
    // Biome & Structure
    public static final String BIOME = "biome";
    public static final String BIOME_SHORT = "bi";
    public static final String STRUCTURES = "structures";
    public static final String STRUCTURES_SHORT = "ss";
    // Messages
    public static final String MESSAGES = "messages";
    public static final String MESSAGES_SHORT = "msgs";
    public static final String ZONE_MESSAGES = "zonemessages";
    public static final String ZONE_MESSAGES_SHORT = "zmsgs";
    public static final String ZONE_MESSAGE = "zonemessage";
    public static final String ZONE_MESSAGE_SHORT = "zmsg";
    public static final String TEAM_MESSAGES = "teammessages";
    public static final String TEAM_MESSAGES_SHORT = "tmsgs";
    public static final String TEAM_MESSAGE = "teammessage";
    public static final String TEAM_MESSAGE_SHORT = "tmsg";
    public static final String GAME_MESSAGES = "gamemessages";
    public static final String GAME_MESSAGES_SHORT = "gmsgs";
    public static final String GAME_MESSAGE = "gamemessage";
    public static final String GAME_MESSAGE_SHORT = "gmsg";
    // Effect
    public static final String CHANNEL = "channel";
    public static final String PARTICLES = "particles";
    public static final String PARTICLES_SHORT = "pts";
    public static final String PARTICLE = "particle";
    public static final String PARTICLE_SHORT = "pt";
    public static final String FIREWORKS = "fireworks";
    public static final String FIREWORKS_SHORT = "fws";
    public static final String FIREWORK = "firework";
    public static final String FIREWORK_SHORT = "fw";
    public static final String MUTEKIS = "mutekis";
    public static final String MUTEKIS_SHORT = "mts";
    public static final String MUTEKI = "muteki";
    public static final String MUTEKI_SHORT = "mt";
    public static final String BOOSTS = "boosts";
    public static final String BOOSTS_SHORT = "bos";
    public static final String BOOST = "boost";
    public static final String BOOST_SHORT = "bo";
}
