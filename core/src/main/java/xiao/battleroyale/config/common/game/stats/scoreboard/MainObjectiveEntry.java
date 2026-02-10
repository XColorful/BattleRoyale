package xiao.battleroyale.config.common.game.stats.scoreboard;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.common.game.stats.ScoreboardEntryTag;
import xiao.battleroyale.util.JsonUtils;

public class MainObjectiveEntry {

    public @NotNull String player_to_player_damage;
    public @NotNull String other_to_player_damage;
    public @NotNull String player_damage_by_player;
    public @NotNull String player_damage_by_other;
    public @NotNull String player_knock_player;
    public @NotNull String other_knock_player;
    public @NotNull String player_down_by_player;
    public @NotNull String player_down_by_other;
    public @NotNull String player_revive;
    public @NotNull String player_kill_player;
    public @NotNull String other_kill_player;
    public @NotNull String player_death_by_player;
    public @NotNull String player_death_by_other;
    public @NotNull String player_win;
    public @NotNull String player_lose;

    public MainObjectiveEntry(@NotNull String player_to_player_damage, @NotNull String other_to_player_damage, @NotNull String player_damage_by_player, @NotNull String player_damage_by_other,
                              @NotNull String player_knock_player, @NotNull String other_knock_player,
                              @NotNull String player_down_by_player, @NotNull String player_down_by_other,
                              @NotNull String player_revive,
                              @NotNull String player_kill_player, @NotNull String other_kill_player,
                              @NotNull String player_death_by_player, @NotNull String player_death_by_other,
                              @NotNull String player_win, @NotNull String player_lose) {
        this.player_to_player_damage = player_to_player_damage;
        this.other_to_player_damage = other_to_player_damage;
        this.player_damage_by_player = player_damage_by_player;
        this.player_damage_by_other = player_damage_by_other;
        this.player_knock_player = player_knock_player;
        this.other_knock_player = other_knock_player;
        this.player_down_by_player = player_down_by_player;
        this.player_down_by_other = player_down_by_other;
        this.player_revive = player_revive;
        this.player_kill_player = player_kill_player;
        this.other_kill_player = other_kill_player;
        this.player_death_by_player = player_death_by_player;
        this.player_death_by_other = player_death_by_other;
        this.player_win = player_win;
        this.player_lose = player_lose;
    }

    public @NotNull MainObjectiveEntry copy() {
        return new MainObjectiveEntry(
                player_to_player_damage, other_to_player_damage, player_damage_by_player, player_damage_by_other,
                player_knock_player, other_knock_player,
                player_down_by_player, player_down_by_other,
                player_revive,
                player_kill_player, other_kill_player,
                player_death_by_player, player_death_by_other,
                player_win, player_lose
        );
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_TO_PLAYER_DAMAGE, player_to_player_damage);
        jsonObject.addProperty(ScoreboardEntryTag.OTHER_TO_PLAYER_DAMAGE, other_to_player_damage);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_DAMAGE_BY_PLAYER, player_damage_by_player);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_DAMAGE_BY_OTHER, player_damage_by_other);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_KNOCK_PLAYER, player_knock_player);
        jsonObject.addProperty(ScoreboardEntryTag.OTHER_KNOCK_PLAYER, other_knock_player);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_DOWN_BY_PLAYER, player_down_by_player);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_DOWN_BY_OTHER, player_down_by_other);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_REVIVE, player_revive);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_KILL_PLAYER, player_kill_player);
        jsonObject.addProperty(ScoreboardEntryTag.OTHER_KILL_PLAYER, other_kill_player);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_DEATH_BY_PLAYER, player_death_by_player);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_DEATH_BY_OTHER, player_death_by_other);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_WIN, player_win);
        jsonObject.addProperty(ScoreboardEntryTag.PLAYER_LOSE, player_lose);
        return jsonObject;
    }

    public static MainObjectiveEntry fromJson(JsonObject jsonObject) {
        String p2p_dmg = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_TO_PLAYER_DAMAGE, "");
        String o2p_dmg = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.OTHER_TO_PLAYER_DAMAGE, "");
        String p_dmg_by_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_DAMAGE_BY_PLAYER, "");
        String p_dmg_by_o = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_DAMAGE_BY_OTHER, "");

        String p_knock_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_KNOCK_PLAYER, "");
        String o_knock_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.OTHER_KNOCK_PLAYER, "");
        String p_down_by_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_DOWN_BY_PLAYER, "");
        String p_down_by_o = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_DOWN_BY_OTHER, "");

        String p_revive = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_REVIVE, "");

        String p_kill_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_KILL_PLAYER, "");
        String o_kill_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.OTHER_KILL_PLAYER, "");
        String p_death_by_p = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_DEATH_BY_PLAYER, "");
        String p_death_by_o = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_DEATH_BY_OTHER, "");

        String p_win = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_WIN, "");
        String p_lose = JsonUtils.getJsonString(jsonObject, ScoreboardEntryTag.PLAYER_LOSE, "");

        return new MainObjectiveEntry(
                p2p_dmg, o2p_dmg, p_dmg_by_p, p_dmg_by_o,
                p_knock_p, o_knock_p,
                p_down_by_p, p_down_by_o,
                p_revive,
                p_kill_p, o_kill_p,
                p_death_by_p, p_death_by_o,
                p_win, p_lose
        );
    }
}