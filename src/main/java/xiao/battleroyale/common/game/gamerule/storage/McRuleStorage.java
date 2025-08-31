package xiao.battleroyale.common.game.gamerule.storage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.storage.IRuleStorage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.api.game.gamerule.MinecraftEntryTag;

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
            boolean doFireTick,
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
                mcEntry.doFireTick,
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
                serverLevel.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_DOFIRETICK).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_FALL_DAMAGE).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).get(),
                serverLevel.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get(),
                mcEntry.doTimeSet,
                serverLevel.getGameTime() // 当前总游戏刻
                );
    }

    @Override
    public void apply(ServerLevel serverLevel, List<GamePlayer> gamePlayerList) {
        if (this.currentRule == null) {
            BattleRoyale.LOGGER.warn("Skipped invalid currentRule to apply in McRuleStorage");
            return;
        }

        serverLevel.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(currentRule.mobGriefing, serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(this.currentRule.naturalRegeneration(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(this.currentRule.doMobSpawning(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(this.currentRule.doFireTick(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(this.currentRule.doDaylightCycle(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(this.currentRule.doWeatherCycle(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_FALL_DAMAGE).set(this.currentRule.fallDamage(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY).set(this.currentRule.tntExplosionDropDecay(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(this.currentRule.spectatorGenerateChunks(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(this.currentRule.keepInventory(), serverLevel.getServer());
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
        serverLevel.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(this.backupRule.mobGriefing(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(this.backupRule.naturalRegeneration(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(this.backupRule.doMobSpawning(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(this.backupRule.doFireTick(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(this.backupRule.doDaylightCycle(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(this.backupRule.doWeatherCycle(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_FALL_DAMAGE).set(this.backupRule.fallDamage(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY).set(this.backupRule.tntExplosionDropDecay(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(this.backupRule.spectatorGenerateChunks(), serverLevel.getServer());
        serverLevel.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(this.backupRule.keepInventory(), serverLevel.getServer());
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
        boolGamerule.put(MinecraftEntryTag.DO_FIRE_TICK, currentRule.doFireTick());
        boolGamerule.put(MinecraftEntryTag.DO_DAYLIGHT_CYCLE, currentRule.doDaylightCycle());
        boolGamerule.put(MinecraftEntryTag.DO_WEATHER_CYCLE, currentRule.doWeatherCycle());
        boolGamerule.put(MinecraftEntryTag.FALL_DAMAGE, currentRule.fallDamage());
        boolGamerule.put(MinecraftEntryTag.TNT_EXPLOSION_DROP_DECAY, currentRule.tntExplosionDropDecay());
        boolGamerule.put(MinecraftEntryTag.SPECTATOR_GENERATE_CHUNKS, currentRule.spectatorGenerateChunks());
        boolGamerule.put(MinecraftEntryTag.KEEP_INVENTORY, currentRule.keepInventory());
        return boolGamerule;
    }
}
