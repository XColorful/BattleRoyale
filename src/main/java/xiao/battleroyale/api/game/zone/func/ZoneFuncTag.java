package xiao.battleroyale.api.game.zone.func;

public final class ZoneFuncTag {
    public static final String TYPE_NAME = "zoneFuncType";

    public static final String NO_FUNC = "noFunc";
    public static final String SAFE = "safe";
    public static final String UNSAFE = "unsafe";
    public static final String DANGER = "danger";
    public static final String AIRDROP = "airdrop";
    public static final String FIREWORK = "firework";
    public static final String MUTEKI = "muteki";
    public static final String BOOST = "boost";
    public static final String PARTICLE = "particle";
    public static final String EFFECT = "effect";
    public static final String ENTITY = "entity";

    public static final String DAMAGE = "damage";
    public static final String MOVE_DELAY = "moveDelay";
    public static final String MOVE_TIME = "moveTime";
    public static final String TICK_FREQUENCY = "tickFrequency";
    public static final String TICK_OFFSET = "tickOffset";

    // firework
    public static final String FIREWORK_TRACK = "trackPlayer";
    public static final String FIREWORK_AMOUNT = "amount";
    public static final String FIREWORK_INTERVAL = "interval";
    public static final String FIREWORK_V_RANGE = "verticalRange";
    public static final String FIREWORK_H_RANGE = "horizontalRange";
    public static final String FIREWORK_OUTSIDE = "outside";

    // muteki
    public static final String MUTEKI_TIME = "mutekiTime";

    // particle
    public static final String PARTICLES = "particles";
    public static final String SELECT_COUNT = "selectCount";
    public static final String CHANNEL = "channel";
    public static final String COOLDOWN = "cooldown";

    // effect
    public static final String EFFECTS = "effects";
    public static final String EFFECT_TYPE = "type";
    public static final String EFFECT_DURATION = "duration";
    public static final String EFFECT_LEVEL = "level";

    // entity

    private ZoneFuncTag() {};
}
