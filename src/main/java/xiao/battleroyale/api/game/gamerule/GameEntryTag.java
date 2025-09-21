package xiao.battleroyale.api.game.gamerule;

import xiao.battleroyale.api.ConfigEntryTag;

public class GameEntryTag extends ConfigEntryTag {

    public static final String TELEPORT_WHEN_INIT_GAME = "teleportWhenInitGame";
    // team
    public static final String TEAM_MSG_EXPIRE_SECONDS = "teamMessageExpireSeconds";
    public static final String TEAM_COLORS = "teamColors";
    public static final String BUILD_VANILLA_TEAM = "buildVanillaTeam";
    public static final String HIDE_VANILLA_TEAM_NAME = "hideVanillaTeamName";
    // game
    public static final String MAX_PLAYER_INVALID_TIME = "maxPlayerInvalidTime";
    public static final String MAX_BOT_INVALID_TIME = "maxBotInvalidTime";
    public static final String REMOVE_INVALID_TEAM = "removeInvalidTeam";
    public static final String HEAL_ALL_AT_START = "healAllAtStart";
    public static final String FRIENDLY_FIRE = "friendlyFire";
    public static final String DOWN_FIRE = "downFire";
    public static final String DOWN_DAMAGE_LIST = "downDamage";
    public static final String DOWN_DAMAGE_FREQUENCY = "downDamageFrequency";
    public static final String DOWN_SHOOT = "downShoot";
    public static final String DOWN_RELOAD = "downReload";
    public static final String DOWN_FIRE_SELECT = "downFireSelect";
    public static final String DOWN_MELEE = "downMelee";
    public static final String ONLY_GAME_PLAYER_SPECTATE = "onlyGamePlayerSpectate";
    public static final String SPECTATE_AFTER_TEAM = "spectateAfterTeamEliminated";
    public static final String SPECTATOR_SEE_ALL_TEAMS = "spectatorSeeAllTeams";
    public static final String TELEPORT_INTERFERER_TO_LOBBY = "teleportInterfererToLobby";
    public static final String FORCE_ELIMINATION_TELEPORT_TO_LOBBY = "forceEliminationTeleportToLobby";
    public static final String ALLOW_REMAINING_BOT = "allowRemainingBot";
    public static final String KEEP_TEAM_AFTER_GAME = "keepTeamAfterGame";
    public static final String TELEPORT_AFTER_GAME = "teleportAfterGame";
    public static final String TELEPORT_WINNER_AFTER_GAME = "teleportWinnerAfterGame";
    public static final String WINNER_FIREWORK_ID = "winnerFireworkId";
    public static final String WINNER_PARTICLE_ID = "winnerParticleId";
    public static final String INIT_GAME_AFTER_GAME = "initGameAfterGame";
    // message
    public static final String MESSAGE_CLEAN_FREQUENCY = "messageCleanFrequency";
    public static final String MESSAGE_EXPIRE_TIME = "messageExpireTime";
    public static final String MESSAGE_FORCE_SYNC_FREQUENCY = "messageSyncFrequency";

    private GameEntryTag() {}

}