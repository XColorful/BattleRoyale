package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.MinecraftEntryTag;
import xiao.battleroyale.util.JsonUtils;

public class MinecraftEntry implements IGameruleEntry {

    public final boolean adventureMode;
    public final boolean mobGriefing;
    public final boolean autoSaturation;
    public final boolean naturalRegeneration;
    public final boolean doMobSpawning;
    public final boolean doFireTick;
    public final boolean doDaylightCycle;
    public final boolean doWeatherCycle;
    public final boolean fallDamage;
    public final boolean tntExplosionDropDecay;
    public final boolean spectatorGenerateChunks;
    public final boolean clearInventory;
    public final boolean keepInventory;
    public final boolean doTimeSet;
    public final int timeSet;

    public MinecraftEntry(boolean adventureMode, boolean mobGriefing, boolean autoSaturation,
                          boolean naturalRegeneration, boolean doMobSpawning, boolean doFireTick,
                          boolean doDaylightCycle, boolean doWeatherCycle, boolean fallDamage,
                          boolean tntExplosionDropDecay, boolean spectatorGenerateChunks, boolean clearInventory,
                          boolean keepInventory, boolean doTimeSet, int timeSet) {
        this.adventureMode = adventureMode;
        this.mobGriefing = mobGriefing;
        this.autoSaturation = autoSaturation;
        this.naturalRegeneration = naturalRegeneration;
        this.doMobSpawning = doMobSpawning;
        this.doFireTick = doFireTick;
        this.doDaylightCycle = doDaylightCycle;
        this.doWeatherCycle = doWeatherCycle;
        this.fallDamage = fallDamage;
        this.tntExplosionDropDecay = tntExplosionDropDecay;
        this.spectatorGenerateChunks = spectatorGenerateChunks;
        this.clearInventory = clearInventory;
        this.keepInventory = keepInventory;
        this.doTimeSet = doTimeSet;
        this.timeSet = timeSet;
    }

    @Override
    public String getType() {
        return "minecraftEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(MinecraftEntryTag.ADVENTURE, adventureMode);
        jsonObject.addProperty(MinecraftEntryTag.MOB_GRIEFING, mobGriefing);
        jsonObject.addProperty(MinecraftEntryTag.AUTO_SATURATION, autoSaturation);
        jsonObject.addProperty(MinecraftEntryTag.NATURAL_REGENERATION, naturalRegeneration);
        jsonObject.addProperty(MinecraftEntryTag.DO_MOB_SPAWNING, doMobSpawning);
        jsonObject.addProperty(MinecraftEntryTag.DO_FIRE_TICK, doFireTick);
        jsonObject.addProperty(MinecraftEntryTag.DO_DAYLIGHT_CYCLE, doDaylightCycle);
        jsonObject.addProperty(MinecraftEntryTag.DO_WEATHER_CYCLE, doWeatherCycle);
        jsonObject.addProperty(MinecraftEntryTag.FALL_DAMAGE, fallDamage);
        jsonObject.addProperty(MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY, tntExplosionDropDecay);
        jsonObject.addProperty(MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS, spectatorGenerateChunks);
        jsonObject.addProperty(MinecraftEntryTag.CLEAR_INVENTORY, clearInventory);
        jsonObject.addProperty(MinecraftEntryTag.KEEP_INVENTORY, keepInventory);
        jsonObject.addProperty(MinecraftEntryTag.DO_TIME_SET, doTimeSet);
        jsonObject.addProperty(MinecraftEntryTag.TIME_SET, timeSet);
        return jsonObject;
    }

    @NotNull
    public static MinecraftEntry fromJson(JsonObject jsonObject) {
        boolean adventureMode = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.ADVENTURE, true);
        boolean mobGriefing = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.MOB_GRIEFING, false);
        boolean autoSaturation = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.AUTO_SATURATION, true);
        boolean naturalRegeneration = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.NATURAL_REGENERATION, false);
        boolean mobSpawning = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.DO_MOB_SPAWNING, false);
        boolean doFireTick = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.DO_FIRE_TICK, false);
        boolean doDaylightCycle = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.DO_DAYLIGHT_CYCLE, false);
        boolean doWeatherCycle = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.DO_WEATHER_CYCLE, false);
        boolean fallDamage = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.FALL_DAMAGE, true);
        boolean tntExplodes = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY, false);
        boolean spectatorGenerateChunks = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS, false);
        boolean clearInventory = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.CLEAR_INVENTORY, true);
        boolean keepInventory = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.KEEP_INVENTORY, false);
        boolean doTimeSet = JsonUtils.getJsonBool(jsonObject, MinecraftEntryTag.DO_TIME_SET, true);
        int timeSet = JsonUtils.getJsonInt(jsonObject, MinecraftEntryTag.TIME_SET, 5000);

        return new MinecraftEntry(adventureMode, mobGriefing, autoSaturation,
                naturalRegeneration, mobSpawning, doFireTick,
                doDaylightCycle, doWeatherCycle, fallDamage,
                tntExplodes, spectatorGenerateChunks, clearInventory,
                keepInventory, doTimeSet, timeSet);
    }
}