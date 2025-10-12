package xiao.battleroyale.api.game.zone.func;

import xiao.battleroyale.api.loot.LootConfigTag;

public final class ZoneFuncTag {
    public static final String TYPE_NAME = "zoneFuncType";

    public static final String NO_FUNC = "noFunc";
    public static final String SAFE = "safe";
    public static final String UNSAFE = "unsafe";
    public static final String DANGER = "danger";
    public static final String FIREWORK = "firework";
    public static final String MUTEKI = "muteki";
    public static final String BOOST = "boost";
    public static final String PARTICLE = "particle";
    public static final String EFFECT = "effect";
    public static final String MESSAGE = "message";
    public static final String INVENTORY = "inventory";
    public static final String ENTITY = "entity";
    public static final String EVENT = "event";
    public static final String AIRDROP = "airdrop";

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
    public static final String EFFECT_LEVEL = "serverLevel";

    // message
    public static final String SET_TITLE_ANIMATION = "setTitleAnimation";
    public static final String FADE_IN_TICKS = "fadeInTicks";
    public static final String STAY_TICKS = "stayTicks";
    public static final String FADE_OUT_TICKS = "fadeOutTicks";
    public static final String SEND_TITLES = "sendTitles";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String SEND_ACTION_BAR = "sendActionBar";
    public static final String ACTION_BAR = "actionBar";

    // inventory
    public static final String SKIP_NON_EMPTY_SLOT = "skipNonEmptySlot";
    public static final String DROP_BEFORE_REPLACE = "dropBeforeReplace";
    public static final String FIRST_SLOT_INDEX = "firstSlotIndex";
    public static final String LAST_SLOT_INDEX = "lastSlotIndex";
    public static final String INVENTORY_LOOT_ENTRY = "lootEntry";
    public static final String INVENTORY_LOOT_SPAWNER_LOOT_ID = "lootSpawnerLootId";

    // event
    public static final String PROTOCOL = "protocol";
    public static final String TAG = "tag";
    public static final String NBT = "nbt";
    public static final String LOOT_ID = LootConfigTag.LOOT_ID;

    // entity

    private ZoneFuncTag() {};
}
