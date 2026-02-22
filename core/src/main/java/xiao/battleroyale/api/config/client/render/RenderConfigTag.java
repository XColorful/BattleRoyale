package xiao.battleroyale.api.config.client.render;

import xiao.battleroyale.api.config.sub.ConfigEntryTag;

public class RenderConfigTag extends ConfigEntryTag {

    public static String BLOCK_ENTRY = "block";
    public static String ZONE_ENTRY = "zone";
    public static String TEAM_ENTRY = "teamEntry";
    public static String SPECTATE_ENTRY = "spectateEntry";

    public static String ITEM_RENDER_DISTANCE = "itemRenderDistance";
    public static String RENDER_ITEM_BLOCK_IF_EMPTY = "renderItemBlockIfEmpty";
    public static String ITEM_RENDER_SCALE = "itemRenderScale";
    public static String ITEM_RENDER_HEIGHT = "itemRenderHeight";
    public static String DO_ITEM_BOBBING = "doItemBobbing";
    public static String DO_ITEM_SPINNING = "doItemSpinning";
    public static String ITEM_BOB_PHASE=  "itemBobPhase";
    public static String ITEM_BOB_SPEED = "itemBobSpeed";
    public static String ITEM_BOB_HEIGHT = "itemBobHeight";
    public static String ITEM_SPIN_SPEED = "itemSpinSpeed";
    public static String ENTITY_RENDER_DISTANCE = "entityRenderDistance";
    public static String FRUSTUM_CULLING = "enableFrustumCulling";
    public static String OCCLUSION_CULLING = "enableOcclusionCulling";
    public static String OCCLUSION_CHECK_INTERVAL = "occlusionCheckInterval";

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

    /**
     * 原版物品在 GROUND 模式下的重心偏移量
     * 由于 GROUND 模式渲染中心在模型正中，此值用于将模型底部校准至坐标原点
     * 计算: 0.25 (模型半高) * 0.5 (标准缩放修正) = 0.125
     */
    public static final float VANILLA_GROUND_OFFSET = 0.125F;
    /**
     * 原版掉落物标准缩放比例
     */
    public static float VANILLA_ITEM_RENDER_SCALE = 1.0F;
    /**
     * 原版掉落物标准静态悬浮高度
     */
    public static float VANILLA_ITEM_RENDER_HEIGHT = 0F;
    /**
     * 物品浮动相位差
     * 0.0: 完全共振 (步调一致)
     * 0.5: 原版掉落物平滑错落感
     */
    public static float VANILLA_BOB_PHASE = 0.5F;
    /**
     * 原版掉落物标准浮动频率系数
     * 基于正弦函数频率 (1/10.0F)
     */
    public static final float VANILLA_BOB_SPEED = 0.1F;
    /**
     * 原版掉落物标准浮动总幅度 (波峰到波谷的距离)
     * 配合渲染器的 (sin+1)*0.5 算法，产生 0.2 的动态位移跨度
     */
    public static final float VANILLA_BOB_HEIGHT = 0.2F;
    /**
     * 原版掉落物标准旋转速度 (度/tick)
     * 计算公式: (1 / 20 rad/tick) * (180 / PI) ≈ 2.864789
     */
    public static final float VANILLA_SPIN_SPEED = 2.864789F;
}