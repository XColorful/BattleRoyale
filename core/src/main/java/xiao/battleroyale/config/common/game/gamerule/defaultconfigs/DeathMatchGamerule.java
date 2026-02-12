package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.config.common.game.zone.defaultconfigs.DeathMatch100x100Zone;

import java.nio.file.Paths;

import static xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry.OVERWORLD_LEVEL_KEY;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class DeathMatchGamerule {

    private static final String DEFAULT_FILE_NAME = "example_deathmatch.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray gameruleConfigJson = new JsonArray();
        addDeathMatchSolo(gameruleConfigJson);
        addDeathMatchDual(gameruleConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), gameruleConfigJson);
    }

    public static void addDeathMatchSolo(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addDeathMatch(0, 16, 1, DeathMatch100x100Zone.GAME_TIME,
                new Vec3(128, -60, 128), new Vec3(10, 10, 10), "DeathMatch solo", false));
    }

    public static void addDeathMatchDual(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addDeathMatch(1, 32, 2, DeathMatch100x100Zone.GAME_TIME,
                new Vec3(128, -60, 128), new Vec3(10, 10, 10), "DeathMatch dual", false));
    }

    private static JsonObject addDeathMatch(int gameId, int playerTotal, int teamSize, int GAME_TIME,
                                            Vec3 lobbyCenter, Vec3 lobbyDim, String name, boolean bot) {
        BattleroyaleEntry brEntry = new BattleroyaleEntry(OVERWORLD_LEVEL_KEY, playerTotal, teamSize, bot, bot,
                2, GAME_TIME, 1,
                lobbyCenter, lobbyDim,
                true, true, true, false, true,
                true, true);

        MinecraftEntry mcEntry = new MinecraftEntry(true, false, true,
                false, false, false,
                false, false, true,
                false, false, true,
                false, true, 5000);

        GameEntry gameEntry = new GameEntry();
        gameEntry.maxPlayerInvalidTime = 20 * 15; // 15秒掉线
        // 不回大厅
        gameEntry.teleportWinnerAfterGame = false;
        gameEntry.teleportAfterGame = false;
        // 光速重开
        gameEntry.restartAfterGame = true;
        gameEntry.restartDelay = 20 * 5; // 5秒后重开
        gameEntry.maxRestartRound = 15;

        GameruleConfigManager.GameruleConfig gameruleConfig = new GameruleConfigManager.GameruleConfig(gameId, name + " " + playerTotal + " " + teamSize, "#FFFFFFAA",
                brEntry, mcEntry, gameEntry);

        return gameruleConfig.toJson();
    }
}
