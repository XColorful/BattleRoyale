package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public final int timeSet;

    public MinecraftEntry(boolean adventureMode, boolean mobGriefing, boolean autoSaturation,
                          boolean naturalRegeneration, boolean doMobSpawning, boolean doFireTick,
                          boolean doDaylightCycle, boolean doWeatherCycle, boolean fallDamage,
                          boolean tntExplosionDropDecay, boolean spectatorGenerateChunks, int timeSet) {
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
        jsonObject.addProperty(MinecraftEntryTag.TIME_SET, timeSet);
        return jsonObject;
    }

    @NotNull
    public static MinecraftEntry fromJson(JsonObject jsonObject) {
        boolean adventureMode = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.ADVENTURE, true);
        boolean mobGriefing = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.MOB_GRIEFING, false);
        boolean autoSaturation = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.AUTO_SATURATION, true);
        boolean naturalRegeneration = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.NATURAL_REGENERATION, false);
        boolean mobSpawning = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.DO_MOB_SPAWNING, false);
        boolean doFireTick = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.DO_FIRE_TICK, false);
        boolean doDaylightCycle = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.DO_DAYLIGHT_CYCLE, false);
        boolean doWeatherCycle = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.DO_WEATHER_CYCLE, false);
        boolean fallDamage = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.FALL_DAMAGE, true);
        boolean tntExplodes = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY, false);
        boolean spectatorGenerateChunks = JsonUtils.getJsonBoolean(jsonObject, MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS, false);
        int timeSet = JsonUtils.getJsonInt(jsonObject, MinecraftEntryTag.TIME_SET, 5000);

        return new MinecraftEntry(adventureMode, mobGriefing, autoSaturation,
                naturalRegeneration, mobSpawning, doFireTick,
                doDaylightCycle, doWeatherCycle, fallDamage,
                tntExplodes, spectatorGenerateChunks, timeSet);
    }
}