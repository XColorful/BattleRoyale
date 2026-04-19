package xiao.battleroyale.common.game.gamerule.storage;

import net.minecraft.server.MinecraftServer;
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
            boolean showDeathMessages,
            boolean keepInventory,
            boolean doImmediateRespawn,
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
                mcEntry.showDeathMessages,
                mcEntry.keepInventory,
                mcEntry.doImmediateRespawn,
                mcEntry.doTimeSet,
                mcEntry.timeSet
        );

        GameRules gameRules = serverLevel.getGameRules();
        this.backupRule = new RuleInfo(
                gameRules.get(GameRules.MOB_GRIEFING),
                gameRules.get(GameRules.NATURAL_HEALTH_REGENERATION),
                gameRules.get(GameRules.SPAWN_MOBS),
                gameRules.get(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER),
                gameRules.get(GameRules.ADVANCE_TIME),
                gameRules.get(GameRules.ADVANCE_WEATHER),
                gameRules.get(GameRules.FALL_DAMAGE),
                gameRules.get(GameRules.TNT_EXPLOSION_DROP_DECAY),
                gameRules.get(GameRules.SPECTATORS_GENERATE_CHUNKS),
                gameRules.get(GameRules.SHOW_DEATH_MESSAGES),
                gameRules.get(GameRules.KEEP_INVENTORY),
                gameRules.get(GameRules.IMMEDIATE_RESPAWN),
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

        GameRules gameRules = serverLevel.getGameRules();
        MinecraftServer mcServer = serverLevel.getServer();
        gameRules.set(GameRules.MOB_GRIEFING, currentRule.mobGriefing(), mcServer);
        gameRules.set(GameRules.NATURAL_HEALTH_REGENERATION, this.currentRule.naturalRegeneration(), mcServer);
        gameRules.set(GameRules.SPAWN_MOBS, this.currentRule.doMobSpawning(), mcServer);
        gameRules.set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, this.currentRule.doFireTick(), mcServer);
        gameRules.set(GameRules.ADVANCE_TIME, this.currentRule.doDaylightCycle(), mcServer);
        gameRules.set(GameRules.ADVANCE_WEATHER, this.currentRule.doWeatherCycle(), mcServer);
        gameRules.set(GameRules.FALL_DAMAGE, this.currentRule.fallDamage(), mcServer);
        gameRules.set(GameRules.TNT_EXPLOSION_DROP_DECAY, this.currentRule.tntExplosionDropDecay(), mcServer);
        gameRules.set(GameRules.SPECTATORS_GENERATE_CHUNKS, this.currentRule.spectatorGenerateChunks(), mcServer);
        gameRules.set(GameRules.SHOW_DEATH_MESSAGES, this.currentRule.showDeathMessages(), mcServer);
        gameRules.set(GameRules.KEEP_INVENTORY, this.currentRule.keepInventory(), mcServer);
        gameRules.set(GameRules.IMMEDIATE_RESPAWN, this.currentRule.doImmediateRespawn(), mcServer);
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
        GameRules gameRules = serverLevel.getGameRules();
        MinecraftServer mcServer = serverLevel.getServer();
        gameRules.set(GameRules.MOB_GRIEFING, this.backupRule.mobGriefing(), mcServer);
        gameRules.set(GameRules.NATURAL_HEALTH_REGENERATION, this.backupRule.naturalRegeneration(), mcServer);
        gameRules.set(GameRules.SPAWN_MOBS, this.backupRule.doMobSpawning(), mcServer);
        gameRules.set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, this.backupRule.doFireTick(), mcServer);
        gameRules.set(GameRules.ADVANCE_TIME, this.backupRule.doDaylightCycle(), mcServer);
        gameRules.set(GameRules.ADVANCE_WEATHER, this.backupRule.doWeatherCycle(), mcServer);
        gameRules.set(GameRules.FALL_DAMAGE, this.backupRule.fallDamage(), mcServer);
        gameRules.set(GameRules.TNT_EXPLOSION_DROP_DECAY, this.backupRule.tntExplosionDropDecay(), mcServer);
        gameRules.set(GameRules.SPECTATORS_GENERATE_CHUNKS, this.backupRule.spectatorGenerateChunks(), mcServer);
        gameRules.set(GameRules.SHOW_DEATH_MESSAGES, this.backupRule.spectatorGenerateChunks(), mcServer);
        gameRules.set(GameRules.KEEP_INVENTORY, this.backupRule.keepInventory(), mcServer);
        gameRules.set(GameRules.IMMEDIATE_RESPAWN, this.backupRule.doImmediateRespawn(), mcServer);
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
        boolGamerule.put(MinecraftEntryTag.SHOW_DEATH_MESSAGES, currentRule.showDeathMessages());
        boolGamerule.put(MinecraftEntryTag.KEEP_INVENTORY, currentRule.keepInventory());
        return boolGamerule;
    }
}
