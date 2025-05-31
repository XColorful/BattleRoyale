package xiao.battleroyale.api.game.spawn.type.detail;

public class SpawnDetailTag {

    public static final String TYPE_NAME = "detailType";

    public static final String FIXED = "fixed";
    public static final String RANDOM = "random";

    // ground
    public static final String GROUND_FIXED_POS = "fixedPos";
    public static final String GROUND_TEAM_TOGETHER = "teamTogether";
    public static final String GROUND_FIND_GROUND = "findGround";
    public static final String GROUND_RANDOM_RANGE = "randomRange";

    // plane
    public static final String PLANE_HEIGHT = "planeHeight";
    public static final String PLANE_SPEED = "planeSpeed";
    public static final String PLANE_FIXED_TIME = "fixedFlightTime";

    // plane (vulnerable)
    public static final String PLANE_AIR_CRASH = "airCrash";
    public static final String PLANE_CRASH_TIME = "crashTime";
    public static final String PLANE_TIME_RANGE = "timeRange";
    public static final String PLANE_CRASH_YAW = "crashYawTurn";
    public static final String PLANE_CRASH_PITCH = "crashPitchTurn";

    private SpawnDetailTag() {};
}
