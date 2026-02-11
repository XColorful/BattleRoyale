package xiao.battleroyale.config.common.game.stats.scoreboard;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.game.stats.ScoreboardEntryTag;
import xiao.battleroyale.util.JsonUtils;

public class SecondObjectiveEntry {

    public @NotNull String player_attack_rate;
    public @NotNull String player_kd;
    public @NotNull String player_game_total;
    public @NotNull String player_win_rate;

    public SecondObjectiveEntry(@NotNull String player_attack_rate, @NotNull String player_kd, @NotNull String player_game_total, @NotNull String player_win_rate) {
        this.player_attack_rate = player_attack_rate;
        this.player_kd = player_kd;
        this.player_game_total = player_game_total;
        this.player_win_rate = player_win_rate;
    }

    public @NotNull SecondObjectiveEntry copy() {
        return new SecondObjectiveEntry(player_attack_rate, player_kd, player_game_total, player_win_rate);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_ATTACK_RATE, player_attack_rate);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_KD, player_kd);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_GAME_TOTAL, player_game_total);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_WIN_RATE, player_win_rate);
        return jsonObject;
    }

    public static SecondObjectiveEntry fromJson(JsonObject jsonObject) {
        String attack_rate = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_ATTACK_RATE, "");
        String kd = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_KD, "");
        String game_total = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_GAME_TOTAL, "");
        String win_rate = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_WIN_RATE, "");

        return new SecondObjectiveEntry(attack_rate, kd, game_total, win_rate);
    }
}