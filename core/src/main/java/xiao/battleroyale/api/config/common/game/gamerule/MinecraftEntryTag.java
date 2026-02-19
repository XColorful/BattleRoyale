package xiao.battleroyale.api.config.common.game.gamerule;

import xiao.battleroyale.api.config.sub.ConfigEntryTag;

public class MinecraftEntryTag extends ConfigEntryTag {

    public static final String ADVENTURE = "adventureMode";
    public static final String MOB_GRIEFING = "mobGriefing";
    public static final String AUTO_SATURATION = "autoSaturation";
    public static final String NATURAL_REGENERATION = "naturalRegeneration";
    public static final String DO_MOB_SPAWNING = "doMobSpawning";
    public static final String DO_FIRE_TICK = "doFireTick";
    public static final String DO_DAYLIGHT_CYCLE = "doDaylightCycle";
    public static final String DO_WEATHER_CYCLE = "doWeatherCycle";
    public static final String FALL_DAMAGE = "fallDamage";
    /**
     * @deprecated 这是一个历史误解，原版 tntExplosionDropDecay 并不是阻止TNT爆炸或破坏方块
     */
    @Deprecated(since = "0.4.8", forRemoval = false) public static final String TNT_EXPLOSION_DROP_DECAY_OLD = "tntExplodes";
    public static final String TNT_EXPLOSION_DROP_DECAY = "tntExplosionDropDecay";
    public static final String SPECTATOR_GENERATE_CHUNKS = "spectatorGenerateChunks";
    /**
     * @deprecated clearInventory 跟 keepInventory 放一起容易误解
     */
    @Deprecated(since = "0.5.0", forRemoval = false) public static final String CLEAR_INVENTORY = "clearInventory";
    public static final String CLEAR_INVENTORY_AT_START = "clearInventoryAtStart";
    public static final String KEEP_INVENTORY = "keepInventory";
    public static final String DO_IMMEDIATE_RESPAWN = "doImmediateRespawn";
    public static final String DO_TIME_SET = "doTimeSet";
    public static final String TIME_SET = "timeSet";

    private MinecraftEntryTag() {};
}