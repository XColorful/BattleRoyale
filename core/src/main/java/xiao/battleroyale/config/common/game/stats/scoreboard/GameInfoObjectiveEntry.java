package xiao.battleroyale.config.common.game.stats.scoreboard;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.game.stats.ScoreboardEntryTag;
import xiao.battleroyale.util.JsonUtils;

public class GameInfoObjectiveEntry {

    public @NotNull String playerTotal;
    public @NotNull String alive;
    public @NotNull String gameTime;

    public GameInfoObjectiveEntry(@NotNull String playerTotal, @NotNull String alive, @NotNull String gameTime) {
        this.playerTotal = playerTotal;
        this.alive = alive;
        this.gameTime = gameTime;
    }

    public @NotNull GameInfoObjectiveEntry copy() {
        return new GameInfoObjectiveEntry(playerTotal, alive, gameTime);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_TOTAL, playerTotal);
        jsonObject.addProperty(ScoreboardEntryTag.ALIVE, alive);
        jsonObject.addProperty(ScoreboardEntryTag.GAME_TIME, gameTime);
        return jsonObject;
    }

    public static GameInfoObjectiveEntry fromJson(JsonObject jsonObject) {
        String playerTotal = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_TOTAL, "");
        String alive = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.ALIVE, "");
        String gameTime = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.GAME_TIME, "");

        return new GameInfoObjectiveEntry(playerTotal, alive, gameTime);
    }
}