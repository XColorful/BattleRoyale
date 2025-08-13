package xiao.battleroyale.api.game.gamerule;

import xiao.battleroyale.api.ConfigEntryTag;

public class GameEntryTag extends ConfigEntryTag {

    // team
    public static final String TEAM_MSG_EXPIRE_SECONDS = "teamMessageExpireSeconds";
    public static final String TEAM_COLORS = "teamColors";
    // game
    public static final String MAX_PLAYER_INVALID_TIME = "maxPlayerInvalidTime";
    public static final String MAX_BOT_INVALID_TIME = "maxBotInvalidTime";
    public static final String REMOVE_INVALID_TEAM = "removeInvalidTeam";
    public static final String FRIENDLY_FIRE = "friendlyFire";
    public static final String DOWN_DAMAGE_LIST = "downDamage";
    public static final String DOWN_DAMAGE_FREQUENCY = "downDamageFrequency";
    public static final String ONLY_GAME_PLAYER_SPECTATE = "onlyGamePlayerSpectate";
    public static final String SPECTATE_AFTER_TEAM = "spectateAfterTeamEliminated";
    public static final String TELEPORT_INTERFERER_TO_LOBBY = "teleportInterfererToLobby";
    public static final String ALLOW_REMAINING_BOT = "allowRemainingBot";
    public static final String KEEP_TEAM_AFTER_GAME = "keepTeamAfterGame";
    public static final String TELEPORT_AFTER_GAME = "teleportAfterGame";
    public static final String TELEPORT_WINNER_AFTER_GAME = "teleportWinnerAfterGame";
    public static final String WINNER_FIREWORK_ID = "winnerFireworkId";
    public static final String WINNER_PARTICLE_ID = "winnerParticleId";
    // message
    public static final String MESSAGE_CLEAN_FREQUENCY = "messageCleanFrequency";
    public static final String MESSAGE_EXPIRE_TIME = "messageExpireTime";
    public static final String MESSAGE_FORCE_SYNC_FREQUENCY = "messageSyncFrequency";

    private GameEntryTag() {}

}