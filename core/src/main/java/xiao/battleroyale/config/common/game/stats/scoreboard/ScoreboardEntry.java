package xiao.battleroyale.config.common.game.stats.scoreboard;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.game.stats.IStatsEntry;
import xiao.battleroyale.api.config.common.game.stats.ScoreboardEntryTag;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardEntry implements IStatsEntry {

    public boolean recordScoreboard;
    public boolean resetScoreboardAtStart;
    public int mcMaxHealth;
    public float damageMultiplier;
    public float ratioBase;
    public int scoreboardCycleInterval;
    public @NotNull List<String> cycleObjectiveNames;

    public @Nullable MainObjectiveEntry mainObjective;
    public @Nullable SecondObjectiveEntry secondObjective;
    public @Nullable SpecialObjectiveEntry specialObjective;

    public @NotNull String listObjectiveAfterGame;
    public @NotNull String sidebarObjectiveAfterGame;

    public ScoreboardEntry(boolean recordScoreboard, boolean resetScoreboardAtStart, int mcMaxHealth,
                           float damageMultiplier, float ratioBase,
                           int scoreboardCycleInterval, @Nullable List<String> cycleObjectiveNames,
                           @Nullable MainObjectiveEntry mainObjective, @Nullable SecondObjectiveEntry secondObjective, @Nullable SpecialObjectiveEntry specialObjective,
                           @NotNull String listObjectiveAfterGame, @NotNull String sidebarObjectiveAfterGame) {
        this.recordScoreboard = recordScoreboard;
        this.resetScoreboardAtStart = resetScoreboardAtStart;
        this.mcMaxHealth = mcMaxHealth;
        this.damageMultiplier = damageMultiplier;
        this.ratioBase = ratioBase;
        this.scoreboardCycleInterval = scoreboardCycleInterval;
        this.cycleObjectiveNames = cycleObjectiveNames != null ? cycleObjectiveNames : new ArrayList<>();
        this.mainObjective = mainObjective;
        this.secondObjective = secondObjective;
        this.specialObjective = specialObjective;
        this.listObjectiveAfterGame = listObjectiveAfterGame;
        this.sidebarObjectiveAfterGame = sidebarObjectiveAfterGame;
    }

    @Override
    public @NotNull ScoreboardEntry copy() {
        return new ScoreboardEntry(
                recordScoreboard,
                resetScoreboardAtStart,
                mcMaxHealth,
                damageMultiplier,
                ratioBase,
                scoreboardCycleInterval,
                new ArrayList<>(cycleObjectiveNames),
                mainObjective != null ? mainObjective.copy() : null,
                secondObjective != null ? secondObjective.copy() : null,
                specialObjective != null ? specialObjective.copy() : null,
                listObjectiveAfterGame,
                sidebarObjectiveAfterGame
        );
    }

    @Override
    public String getType() {
        return "scoreboardEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ScoreboardEntryTag.RECORD_SCOREBOARD, recordScoreboard);
        jsonObject.addProperty(ScoreboardEntryTag.RESET_SCOREBOARD_AT_START, resetScoreboardAtStart);
        jsonObject.addProperty(ScoreboardEntryTag.MC_MAX_HEALTH, mcMaxHealth);
        jsonObject.addProperty(ScoreboardEntryTag.DAMAGE_MULTIPLIER, damageMultiplier);
        jsonObject.addProperty(ScoreboardEntryTag.RATIO_BASE, ratioBase);
        jsonObject.addProperty(ScoreboardEntryTag.SCOREBOARD_CYCLE_INTERVAL, scoreboardCycleInterval);
        jsonObject.add(ScoreboardEntryTag.CYCLE_OBJECTIVE_NAME, JsonUtils.writeStringListToJson(cycleObjectiveNames));

        if (mainObjective != null) {
            jsonObject.add(ScoreboardEntryTag.MAIN_OBJECTIVE, mainObjective.toJson());
        }
        if (secondObjective != null) {
            jsonObject.add(ScoreboardEntryTag.SECOND_OBJECTIVE, secondObjective.toJson());
        }
        if (specialObjective != null) {
            jsonObject.add(ScoreboardEntryTag.SPECIAL_OBJECTIVE, specialObjective.toJson());
        }

        jsonObject.addProperty(ScoreboardEntryTag.LIST_OBJECTIVE_AFTER_GAME, listObjectiveAfterGame);
        jsonObject.addProperty(ScoreboardEntryTag.SIDEBAR_OBJECTIVE_AFTER_GAME, sidebarObjectiveAfterGame);
        return jsonObject;
    }

    @Nullable
    public static ScoreboardEntry fromJson(JsonObject jsonObject) {
        boolean recordScoreboard = JsonUtils.getJsonBool(jsonObject, ScoreboardEntryTag.RECORD_SCOREBOARD, true);
        boolean resetScoreboardAtStart = JsonUtils.getJsonBool(jsonObject, ScoreboardEntryTag.RESET_SCOREBOARD_AT_START, false);
        int mcMaxHealth = JsonUtils.getJsonInt(jsonObject, ScoreboardEntryTag.MC_MAX_HEALTH, 20);
        float damageMultiplier = (float) JsonUtils.getJsonDouble(jsonObject, ScoreboardEntryTag.DAMAGE_MULTIPLIER, 100 / 20f);
        float ratioBase = (float) JsonUtils.getJsonDouble(jsonObject, ScoreboardEntryTag.RATIO_BASE, 1000.0f);
        int scoreboardCycleInterval = JsonUtils.getJsonInt(jsonObject, ScoreboardEntryTag.SCOREBOARD_CYCLE_INTERVAL, 20 * 3);
        List<String> cycleObjectiveNames = JsonUtils.getJsonStringList(jsonObject, ScoreboardEntryTag.CYCLE_OBJECTIVE_NAME);

        JsonObject mainObj = JsonUtils.getJsonObject(jsonObject, ScoreboardEntryTag.MAIN_OBJECTIVE, null);
        MainObjectiveEntry mainEntry = mainObj != null ? MainObjectiveEntry.fromJson(mainObj) : null;

        JsonObject secondObj = JsonUtils.getJsonObject(jsonObject, ScoreboardEntryTag.SECOND_OBJECTIVE, null);
        SecondObjectiveEntry secondEntry = secondObj != null ? SecondObjectiveEntry.fromJson(secondObj) : null;

        JsonObject specialObj = JsonUtils.getJsonObject(jsonObject, ScoreboardEntryTag.SPECIAL_OBJECTIVE, null);
        SpecialObjectiveEntry specialEntry = specialObj != null ? SpecialObjectiveEntry.fromJson(specialObj) : null;

        String listAfter = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.LIST_OBJECTIVE_AFTER_GAME, "");
        String sidebarAfter = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.SIDEBAR_OBJECTIVE_AFTER_GAME, "");

        return new ScoreboardEntry(recordScoreboard, resetScoreboardAtStart, mcMaxHealth,
                damageMultiplier, ratioBase, scoreboardCycleInterval,
                cycleObjectiveNames, mainEntry, secondEntry, specialEntry,
                listAfter, sidebarAfter);
    }
}