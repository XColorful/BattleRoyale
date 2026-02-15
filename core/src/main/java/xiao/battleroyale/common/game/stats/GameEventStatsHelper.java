package xiao.battleroyale.common.game.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.stats.StatsTag;

import java.util.List;

public class GameEventStatsHelper {

    protected static void addTeamStats(StatsManager statsManager, @NotNull JsonArray jsonArray) {
        JsonObject statsObject = new JsonObject();
        statsObject.addProperty(StatsTag.STATS_TAG, StatsTag.TEAM_TAG);

        jsonArray.add(statsObject);
    }

    protected static void addPlayerStats(StatsManager statsManager, @NotNull JsonArray jsonArray) {
        JsonObject statsObject = new JsonObject();
        statsObject.addProperty(StatsTag.STATS_TAG, StatsTag.PLAYER_TAG);

        jsonArray.add(statsObject);
    }

    protected static void addTimelineStats(StatsManager statsManager, @NotNull JsonArray jsonArray, List<GamePlayerStats> gamePlayerStatsList) {
        JsonObject statsObject = new JsonObject();
        statsObject.addProperty(StatsTag.STATS_TAG, StatsTag.TIMELINE_TAG);

        jsonArray.add(statsObject);
    }
}
