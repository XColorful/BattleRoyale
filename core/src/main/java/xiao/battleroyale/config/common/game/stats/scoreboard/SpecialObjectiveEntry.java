package xiao.battleroyale.config.common.game.stats.scoreboard;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.game.stats.ScoreboardEntryTag;
import xiao.battleroyale.util.JsonUtils;

public class SpecialObjectiveEntry {

    public boolean enableJourneyStats;
    public int journeyStatsDelay;
    public @NotNull String player_journey;

    public SpecialObjectiveEntry(boolean enableJourneyStats, int journeyStatsDelay, @NotNull String player_journey) {
        this.enableJourneyStats = enableJourneyStats;
        this.journeyStatsDelay = journeyStatsDelay;
        this.player_journey = player_journey;
    }

    public @NotNull SpecialObjectiveEntry copy() {
        return new SpecialObjectiveEntry(enableJourneyStats, journeyStatsDelay, player_journey);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ScoreboardEntryTag.ENABLE_JOURNEY_STATS, enableJourneyStats);
        jsonObject.addProperty(ScoreboardEntryTag.JOURNEY_STATS_DELAY, journeyStatsDelay);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_JOURNEY, player_journey);
        return jsonObject;
    }

    public static SpecialObjectiveEntry fromJson(JsonObject jsonObject) {
        boolean enable = JsonUtils.getJsonBool(jsonObject, ScoreboardEntryTag.ENABLE_JOURNEY_STATS, true);
        int delay = JsonUtils.getJsonInt(jsonObject, ScoreboardEntryTag.JOURNEY_STATS_DELAY, 20 * 5);
        String journey = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_JOURNEY, "");

        return new SpecialObjectiveEntry(enable, delay, journey);
    }
}