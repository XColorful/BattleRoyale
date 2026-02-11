package xiao.battleroyale.config.common.game.stats.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.common.game.stats.StatsConfigHelper.DeathMatchObjectiveName;
import xiao.battleroyale.config.common.game.stats.StatsConfigManager;
import xiao.battleroyale.config.common.game.stats.scoreboard.MainObjectiveEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.ScoreboardEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.SecondObjectiveEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.SpecialObjectiveEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DeathMatchStats {

    private static String DEFAULT_FILE_NAME = "example_deathmatch.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray statsConfigJson = new JsonArray();
        statsConfigJson.add(generateDeathMatchStatsConfig());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), statsConfigJson);
    }

    private static JsonObject generateDeathMatchStatsConfig() {
        ScoreboardEntry scoreboardEntry = new ScoreboardEntry(true, false, 20,
                100 / 20f, 1000,
                20 * 3, Arrays.asList(DeathMatchObjectiveName.PLAYER_TO_PLAYER_DAMAGE, DeathMatchObjectiveName.PLAYER_KILL_PLAYER, DeathMatchObjectiveName.PLAYER_KD),
                new MainObjectiveEntry(
                        DeathMatchObjectiveName.PLAYER_TO_PLAYER_DAMAGE, DeathMatchObjectiveName.OTHER_TO_PLAYER_DAMAGE, DeathMatchObjectiveName.PLAYER_DAMAGE_BY_PLAYER, DeathMatchObjectiveName.PLAYER_DAMAGE_BY_OTHER,
                        DeathMatchObjectiveName.PLAYER_KNOCK_PLAYER, DeathMatchObjectiveName.OTHER_KNOCK_PLAYER,
                        DeathMatchObjectiveName.PLAYER_DOWN_BY_PLAYER, DeathMatchObjectiveName.PLAYER_DOWN_BY_OTHER,
                        DeathMatchObjectiveName.PLAYER_REVIVE,
                        DeathMatchObjectiveName.PLAYER_KILL_PLAYER, DeathMatchObjectiveName.OTHER_KILL_PLAYER,
                        DeathMatchObjectiveName.PLAYER_DEATH_BY_PLAYER, DeathMatchObjectiveName.PLAYER_DEATH_BY_OTHER,
                        DeathMatchObjectiveName.PLAYER_WIN, DeathMatchObjectiveName.PLAYER_LOSE
                ),
                new SecondObjectiveEntry(
                        DeathMatchObjectiveName.PLAYER_ATTACK_RATE,
                        DeathMatchObjectiveName.PLAYER_KD,
                        DeathMatchObjectiveName.PLAYER_WIN_RATE
                ),
                new SpecialObjectiveEntry(
                        true, 20 * 5,
                        DeathMatchObjectiveName.PLAYER_JOURNEY
                ),
                DeathMatchObjectiveName.PLAYER_KD,
                DeathMatchObjectiveName.PLAYER_ATTACK_RATE
        );

        StatsConfigManager.StatsConfig statsConfig = new StatsConfigManager.StatsConfig(0, "DeathMatch scoreboard (Process-oriented)", "#FFFFFFAA",
                scoreboardEntry);

        return statsConfig.toJson();
    }
}
