package xiao.battleroyale.api.game.zone.shape;

public final class ZoneShapeTag {

    public static final String TYPE_NAME = "zoneShapeType";

    // 2D shape
    public static final String CIRCLE = "circle";
    public static final String SQUARE = "square";
    public static final String RECTANGLE = "rectangle";
    public static final String HEXAGON = "hexagon";
    public static final String POLYGON = "polygon";
    public static final String ELLIPSE = "ellipse";
    public static final String STAR = "star";
    // 3D shape
    public static final String SPHERE = "sphere";
    public static final String HEMI_SPHERE = "hemiSphere";
    public static final String CUBE = "cube";
    public static final String CUBOID = "cuboid";
    public static final String ELLIPSOID = "ellipsoid";

    public static final String START = "start";
    public static final String END = "end";
    public static final String FIXED = "fixed";
    public static final String PREVIOUS = "previous";
    public static final String PREVIOUS_ID = "previousZoneId";
    public static final String PREVIOUS_PROGRESS = "progress";
    public static final String RELATIVE = "relative";
    public static final String LOCK_PLAYER = "lockPlayer";
    public static final String PREVIOUS_SCALE = "scale";
    public static final String RANGE_AS_START_DIM_SCALE = "rangeAsStartDimScale";
    public static final String RANDOM_RANGE = "randomRange";
    public static final String PLAYER_ID = "playerId";
    public static final String SELECT_STANDING = "selectStanding";
    public static final String PLAYER_CENTER_LERP = "playerCenterLerp";
    public static final String SEGMENTS = "segments";
    public static final String BAD_SHAPE = "allowBadShape";

    public static final String CENTER = "center";
    public static final String DIMENSION = "dimension";
    public static final String ROTATION = "rotation";

    public static final String CENTER_TYPE = "centerType";
    public static final String DIMENSION_TYPE = "dimensionType";
    public static final String ROTATION_TYPE = "rotationType";

    private ZoneShapeTag() {};
}
