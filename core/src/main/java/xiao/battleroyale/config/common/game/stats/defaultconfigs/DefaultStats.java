package xiao.battleroyale.config.common.game.stats.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.common.game.stats.StatsConfigHelper.DefaultObjectiveName;
import xiao.battleroyale.config.common.game.stats.StatsConfigManager.StatsConfig;
import xiao.battleroyale.config.common.game.stats.scoreboard.*;

import java.nio.file.Paths;
import java.util.List;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DefaultStats {

    private static final String DEFAULT_FILE_NAME = "example.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray statsConfigJson = new JsonArray();
        statsConfigJson.add(generateDefaultStatsConfig0());
        statsConfigJson.add(generateDefaultStatsConfig1());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), statsConfigJson);
    }

    private static JsonObject generateDefaultStatsConfig0() {
        ScoreboardEntry scoreboardEntry = new ScoreboardEntry(false, false, 20,
                100 / 20f, 1000,
                false, "", null,
                20 * 3, null,
                null, null, null,
                "", "");

        StatsConfig statsConfig = new StatsConfig(0, "Disable scoreboard", "#FFFFFFAA",
                scoreboardEntry);

        return statsConfig.toJson();
    }

    private static JsonObject generateDefaultStatsConfig1() {
        ScoreboardEntry scoreboardEntry = new ScoreboardEntry(true, false, 20,
                100 / 20f, 1000,
                true, DefaultObjectiveName.GAME_INFO, new GameInfoObjectiveEntry(DefaultObjectiveName.PLAYER_TOTAL, DefaultObjectiveName.ALIVE, DefaultObjectiveName.GAME_TIME),
                20 * 3, List.of(DefaultObjectiveName.GAME_INFO),
                new MainObjectiveEntry(
                        DefaultObjectiveName.PLAYER_TO_PLAYER_DAMAGE, DefaultObjectiveName.OTHER_TO_PLAYER_DAMAGE, DefaultObjectiveName.PLAYER_DAMAGE_BY_PLAYER, DefaultObjectiveName.PLAYER_DAMAGE_BY_OTHER,
                        DefaultObjectiveName.PLAYER_KNOCK_PLAYER, DefaultObjectiveName.OTHER_KNOCK_PLAYER,
                        DefaultObjectiveName.PLAYER_DOWN_BY_PLAYER, DefaultObjectiveName.PLAYER_DOWN_BY_OTHER,
                        DefaultObjectiveName.PLAYER_REVIVE,
                        DefaultObjectiveName.PLAYER_KILL_PLAYER, DefaultObjectiveName.OTHER_KILL_PLAYER,
                        DefaultObjectiveName.PLAYER_DEATH_BY_PLAYER, DefaultObjectiveName.PLAYER_DEATH_BY_OTHER,
                        DefaultObjectiveName.PLAYER_WIN, DefaultObjectiveName.PLAYER_LOSE
                ),
                new SecondObjectiveEntry(
                        DefaultObjectiveName.PLAYER_ATTACK_RATE,
                        DefaultObjectiveName.PLAYER_KD,
                        DefaultObjectiveName.PLAYER_GAME_TOTAL,
                        DefaultObjectiveName.PLAYER_WIN_RATE
                ),
                new SpecialObjectiveEntry(
                        true, 20 * 5,
                        DefaultObjectiveName.PLAYER_JOURNEY
                ),
                DefaultObjectiveName.PLAYER_KD,
                DefaultObjectiveName.PLAYER_KD
                );

        StatsConfig statsConfig = new StatsConfig(1, "Enable scoreboard", "#FFFFFFAA",
                scoreboardEntry);

        return statsConfig.toJson();
    }
}
