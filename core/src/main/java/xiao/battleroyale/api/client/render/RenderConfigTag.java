package xiao.battleroyale.api.client.render;

import xiao.battleroyale.api.config.sub.ConfigEntryTag;

public class RenderConfigTag extends ConfigEntryTag {

    public static String BLOCK_ENTRY = "block";
    public static String ZONE_ENTRY = "zone";
    public static String TEAM_ENTRY = "teamEntry";
    public static String SPECTATE_ENTRY = "spectateEntry";

    public static String ITEM_RENDER_DISTANCE = "itemRenderDistance";
    public static String RENDER_ITEM_BLOCK_IF_EMPTY = "renderItemBlockIfEmpty";
    public static String ENTITY_RENDER_DISTANCE = "entityRenderDistance";

    public static String USE_CLIENT_COLOR = "useClientColor";
    public static String FIXED_COLOR = "fixedColor";
    public static String CIRCLE_SEGMENTS = "circleSegments";
    public static String ELLIPSE_SEGMENTS = "ellipseSegments";
    public static String SPHERE_SEGMENTS = "sphereSegments";
    public static String ELLIPSOID_SEGMENTS = "ellispoidSegments";

    public static String ENABLE_TEAM_ZONE = "enableTeamZone";
    public static String ENABLE_SPECTATE_ZONE = "enableSpectateZone";
    public static String RENDER_BEACON = "renderBeacon";
    public static String RENDER_BOUNDING_BOX = "renderBoundingBox";
    public static String TRANSPARENCY = "transparency";
    public static String SCAN_FREQUENCY = "scanFrequency";
    
    private RenderConfigTag() {}
}