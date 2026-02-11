package xiao.battleroyale.config.common.game.stats.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.common.game.stats.StatsConfigHelper.DefaultObjectiveName;
import xiao.battleroyale.config.common.game.stats.StatsConfigManager;
import xiao.battleroyale.config.common.game.stats.scoreboard.*;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class BattleRoyaleStats {

    private static final String DEFAULT_FILE_NAME = "example_battleroyale.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray statsConfigJson = new JsonArray();
        statsConfigJson.add(generateBattleRoyaleStatsConfig());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), statsConfigJson);
    }

    private static JsonObject generateBattleRoyaleStatsConfig() {
        ScoreboardEntry scoreboardEntry = new ScoreboardEntry(true, false, 20,
                100 / 20f, 1000,
                true, DefaultObjectiveName.GAME_INFO, new GameInfoObjectiveEntry(DefaultObjectiveName.PLAYER_TOTAL, DefaultObjectiveName.ALIVE, DefaultObjectiveName.GAME_TIME),
                20 * 3, Arrays.asList(DefaultObjectiveName.PLAYER_KNOCK_PLAYER, DefaultObjectiveName.PLAYER_KILL_PLAYER, DefaultObjectiveName.PLAYER_KD, DefaultObjectiveName.PLAYER_WIN_RATE),
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
                        true, 20 * 30,
                        DefaultObjectiveName.PLAYER_JOURNEY
                ),
                DefaultObjectiveName.PLAYER_WIN_RATE,
                DefaultObjectiveName.PLAYER_JOURNEY
        );

        StatsConfigManager.StatsConfig statsConfig = new StatsConfigManager.StatsConfig(0, "BattleRoyale scoreboard (Result-oriented)", "#FFFFFFAA",
                scoreboardEntry);

        return statsConfig.toJson();
    }
}
