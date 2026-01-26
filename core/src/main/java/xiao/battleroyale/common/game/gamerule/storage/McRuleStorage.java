package xiao.battleroyale.common.game.gamerule.storage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.config.common.game.gamerule.MinecraftEntryTag;
import xiao.battleroyale.api.game.gamerule.storage.IRuleStorage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McRuleStorage implements IRuleStorage {

    private RuleInfo currentRule;
    private RuleInfo backupRule;
    private record RuleInfo(
            boolean mobGriefing,
            boolean naturalRegeneration,
            boolean doMobSpawning,
            int doFireTick,
            boolean doDaylightCycle,
            boolean doWeatherCycle,
            boolean fallDamage,
            boolean tntExplosionDropDecay,
            boolean spectatorGenerateChunks,
            boolean keepInventory,
            boolean doTimeSet,
            long timeSet
    ) {}

    public McRuleStorage() {
        ;
    }

    @Override
    public void store(IGameruleEntry entry, ServerLevel serverLevel, List<GamePlayer> gamePlayerList) {
        if (!(entry instanceof MinecraftEntry mcEntry)) {
            BattleRoyale.LOGGER.error("Expected minecraftEntry for McRuleStorage");
            return;
        }

        this.currentRule = new RuleInfo(
                mcEntry.mobGriefing,
                mcEntry.naturalRegeneration,
                mcEntry.doMobSpawning,
                mcEntry.doFireTick ? serverLevel.getGameRules().get(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER) : 0, // 取当前火焰蔓延
                mcEntry.doDaylightCycle,
                mcEntry.doWeatherCycle,
                mcEntry.fallDamage,
                mcEntry.tntExplosionDropDecay,
                mcEntry.spectatorGenerateChunks,
                mcEntry.keepInventory,
                mcEntry.doTimeSet,
                mcEntry.timeSet
        );

        this.backupRule = new RuleInfo(

                serverLevel.getGameRules().get(GameRules.MOB_GRIEFING),
                serverLevel.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION),
                serverLevel.getGameRules().get(GameRules.SPAWN_MOBS),
                serverLevel.getGameRules().get(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER),
                serverLevel.getGameRules().get(GameRules.ADVANCE_TIME),
                serverLevel.getGameRules().get(GameRules.ADVANCE_WEATHER),
                serverLevel.getGameRules().get(GameRules.FALL_DAMAGE),
                serverLevel.getGameRules().get(GameRules.TNT_EXPLOSION_DROP_DECAY),
                serverLevel.getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS),
                serverLevel.getGameRules().get(GameRules.KEEP_INVENTORY),
                mcEntry.doTimeSet,
                serverLevel.getDayTime()
                );
    }

    @Override
    public void apply(ServerLevel serverLevel, List<GamePlayer> gamePlayerList) {
        if (this.currentRule == null) {
            BattleRoyale.LOGGER.warn("Skipped invalid currentRule to apply in McRuleStorage");
            return;
        }

        serverLevel.getGameRules().set(GameRules.MOB_GRIEFING, currentRule.mobGriefing(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, this.currentRule.naturalRegeneration(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.SPAWN_MOBS, this.currentRule.doMobSpawning(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, this.currentRule.doFireTick(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.ADVANCE_TIME, this.currentRule.doDaylightCycle(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.ADVANCE_WEATHER, this.currentRule.doWeatherCycle(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.FALL_DAMAGE, this.currentRule.fallDamage(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.TNT_EXPLOSION_DROP_DECAY, this.currentRule.tntExplosionDropDecay(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.SPECTATORS_GENERATE_CHUNKS, this.currentRule.spectatorGenerateChunks(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.KEEP_INVENTORY, this.currentRule.keepInventory(), serverLevel.getServer());
        if (this.currentRule.doTimeSet()) {
            BattleRoyale.LOGGER.info("Set {} game time from {} to {}", serverLevel, serverLevel.getGameTime(), this.currentRule.timeSet());
            serverLevel.setDayTime(this.currentRule.timeSet());
            BattleRoyale.LOGGER.info("{} current game time: {}", serverLevel, serverLevel.getGameTime());
        } else {
            BattleRoyale.LOGGER.info("Skipped game time apply, {} current game time: {}", serverLevel, serverLevel.getGameTime());
        }
    }

    @Override
    public void revert(@NotNull ServerLevel serverLevel) {
        if (this.backupRule == null) {
            BattleRoyale.LOGGER.warn("Skipped invalid backupRule to revert in McRuleStorage");
            return;
        }
        serverLevel.getGameRules().set(GameRules.MOB_GRIEFING, this.backupRule.mobGriefing(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, this.backupRule.naturalRegeneration(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.SPAWN_MOBS, this.backupRule.doMobSpawning(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, this.backupRule.doFireTick(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.ADVANCE_TIME, this.backupRule.doDaylightCycle(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.ADVANCE_WEATHER, this.backupRule.doWeatherCycle(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.FALL_DAMAGE, this.backupRule.fallDamage(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.TNT_EXPLOSION_DROP_DECAY, this.backupRule.tntExplosionDropDecay(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.SPECTATORS_GENERATE_CHUNKS, this.backupRule.spectatorGenerateChunks(), serverLevel.getServer());
        serverLevel.getGameRules().set(GameRules.KEEP_INVENTORY, this.backupRule.keepInventory(), serverLevel.getServer());
        if (this.backupRule.doTimeSet()) {
            BattleRoyale.LOGGER.info("Revert {} game time from {} to {}", serverLevel, serverLevel.getGameTime(), this.backupRule.timeSet());
            serverLevel.setDayTime(this.backupRule.timeSet());
            BattleRoyale.LOGGER.info("{} current game time: {}", serverLevel, serverLevel.getGameTime());
        } else {
            BattleRoyale.LOGGER.info("Skipped game time revert, {} current game time: {}", serverLevel, serverLevel.getGameTime());
        }
    }

    @Override
    public void clear() {
        this.currentRule = null;
        this.backupRule = null;
    }

    public Map<String, Integer> getIntWriter() {
        if (currentRule == null) {
            return new HashMap<>();
        }
        Map<String, Integer> intGamerule = new HashMap<>();
        if (currentRule.timeSet > Integer.MAX_VALUE) {
            BattleRoyale.LOGGER.info("McRuleStorage.currentRule.timeSet {} > Integer.MAX_VALUE ({}), cast to int", currentRule.timeSet, Integer.MAX_VALUE);
        }
        intGamerule.put(MinecraftEntryTag.TIME_SET, (int) currentRule.timeSet);
        return intGamerule;
    }
    public Map<String, Boolean> getBoolWriter() {
        if (currentRule == null) {
            return new HashMap<>();
        }
        Map<String, Boolean> boolGamerule = new HashMap<>();
        boolGamerule.put(MinecraftEntryTag.MOB_GRIEFING, currentRule.mobGriefing());
        boolGamerule.put(MinecraftEntryTag.NATURAL_REGENERATION, currentRule.naturalRegeneration());
        boolGamerule.put(MinecraftEntryTag.DO_MOB_SPAWNING, currentRule.doMobSpawning());
        boolGamerule.put(MinecraftEntryTag.DO_FIRE_TICK, currentRule.doFireTick() > 0);
        boolGamerule.put(MinecraftEntryTag.DO_DAYLIGHT_CYCLE, currentRule.doDaylightCycle());
        boolGamerule.put(MinecraftEntryTag.DO_WEATHER_CYCLE, currentRule.doWeatherCycle());
        boolGamerule.put(MinecraftEntryTag.FALL_DAMAGE, currentRule.fallDamage());
        boolGamerule.put(MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY, currentRule.tntExplosionDropDecay());
        boolGamerule.put(MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS, currentRule.spectatorGenerateChunks());
        boolGamerule.put(MinecraftEntryTag.KEEP_INVENTORY, currentRule.keepInventory());
        return boolGamerule;
    }
}
