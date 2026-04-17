package xiao.battleroyale.api.config.common.game.gamerule.custom;

import xiao.battleroyale.api.config.sub.ConfigEntryTag;

public class DeathMatchConfigTag extends ConfigEntryTag {

    public static final String PROTOCOL_NAME = "deathmatch";
    public static final String TARGET_KILL = "targetKill";
    public static final String KILL_FUNCS = "killFuncs";
    public static final String RESPAWN_TRACK_DELAY = "respawnTrackDelay";
    public static final String RETICK_ZONES = "retickZones";
    public static final String SEND_PROGRESS_BAR = "sendProgressBar";
    public static final String PROGRESS_BAR_COLOR = "progressBarColor";
    public static final String PROGRESS_BAR_OVERLAY = "progressBarOverlay";
    public static final String ALLOW_ALL_WIN = "allowAllWin";

    private DeathMatchConfigTag() {}
}
