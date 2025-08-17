package xiao.battleroyale.api.message.team;

import xiao.battleroyale.api.ConfigEntryTag;

public class GameTeamTag extends ConfigEntryTag {
    
    public static final String TEAM_ID = "id";
    public static final String TEAM_COLOR = "color";
    public static final String TEAM_MEMBER = "members";

    public static final String MEMBER_NAME = "name";
    public static final String MEMBER_HEALTH = "health";
    public static final String MEMBER_BOOST = "boost";
    public static final String MEMBER_UUID = "uuid";
    public static final String MEMBER_ALIVE = "alive";

    private GameTeamTag() {}
}
