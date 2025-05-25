package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.MinecraftEntryTag;

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

    @Nullable
    public static MinecraftEntry fromJson(JsonObject jsonObject) {
        boolean adventureMode = !jsonObject.has(MinecraftEntryTag.ADVENTURE) || jsonObject.getAsJsonPrimitive(MinecraftEntryTag.ADVENTURE).getAsBoolean();
        boolean mobGriefing = jsonObject.has(MinecraftEntryTag.MOB_GRIEFING) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.MOB_GRIEFING).getAsBoolean();
        boolean autoSaturation = !jsonObject.has(MinecraftEntryTag.AUTO_SATURATION) || jsonObject.getAsJsonPrimitive(MinecraftEntryTag.AUTO_SATURATION).getAsBoolean();
        boolean naturalRegeneration = jsonObject.has(MinecraftEntryTag.NATURAL_REGENERATION) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.NATURAL_REGENERATION).getAsBoolean();
        boolean mobSpawning = jsonObject.has(MinecraftEntryTag.DO_MOB_SPAWNING) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.DO_MOB_SPAWNING).getAsBoolean();
        boolean doFireTick = jsonObject.has(MinecraftEntryTag.DO_FIRE_TICK) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.DO_FIRE_TICK).getAsBoolean();
        boolean doDaylightCycle = jsonObject.has(MinecraftEntryTag.DO_DAYLIGHT_CYCLE) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.DO_DAYLIGHT_CYCLE).getAsBoolean();
        boolean doWeatherCycle = jsonObject.has(MinecraftEntryTag.DO_WEATHER_CYCLE) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.DO_WEATHER_CYCLE).getAsBoolean();
        boolean fallDamage = !jsonObject.has(MinecraftEntryTag.FALL_DAMAGE) || jsonObject.getAsJsonPrimitive(MinecraftEntryTag.FALL_DAMAGE).getAsBoolean();
        boolean tntExplodes = jsonObject.has(MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY).getAsBoolean();
        boolean spectatorGenerateChunks = jsonObject.has(MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS) && jsonObject.getAsJsonPrimitive(MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS).getAsBoolean();
        int timeSet = jsonObject.has(MinecraftEntryTag.TIME_SET) ? jsonObject.getAsJsonPrimitive(MinecraftEntryTag.TIME_SET).getAsInt() : 5000;

        return new MinecraftEntry(adventureMode, mobGriefing, autoSaturation,
                naturalRegeneration, mobSpawning, doFireTick,
                doDaylightCycle, doWeatherCycle, fallDamage,
                tntExplodes, spectatorGenerateChunks, timeSet);
    }
}