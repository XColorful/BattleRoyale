package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.config.common.game.zone.defaultconfigs.Pubg8000x8000Casual;

import java.nio.file.Paths;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class PubgGamerule {

    private static final String DEFAULT_FILE_NAME = "example_pubg_map.json";

    public static void generateDefaultConfigs() {
        JsonArray gameruleConfigJson = new JsonArray();
        addErangle100(gameruleConfigJson);
        addErangle64(gameruleConfigJson);
        addMiramar100(gameruleConfigJson);
        addMiramar64(gameruleConfigJson);
        writeJsonToFile(Paths.get(GameConfigManager.get().getGameruleConfigPath(), DEFAULT_FILE_NAME).toString(), gameruleConfigJson);
    }

    public static JsonObject addCasual(int gameId, int playerTotal, int teamSize, int GAME_TIME,
                                             Vec3 lobbyCenter, Vec3 lobbyDim, String name) {
        return addBattleRoyale(gameId, playerTotal, teamSize, GAME_TIME, lobbyCenter, lobbyDim, name, true);
    }

    public static JsonObject addCompetitive(int gameId, int playerTotal, int teamSize, int GAME_TIME,
                                             Vec3 lobbyCenter, Vec3 lobbyDim, String name) {
        return addBattleRoyale(gameId, playerTotal, teamSize, GAME_TIME, lobbyCenter, lobbyDim, name, false);
    }

    public static JsonObject addBattleRoyale(int gameId, int playerTotal, int teamSize, int GAME_TIME,
                                             Vec3 lobbyCenter, Vec3 lobbyDim, String name, boolean bot) {
        BattleroyaleEntry brEntry = new BattleroyaleEntry(playerTotal, teamSize, bot, bot, GAME_TIME,
                lobbyCenter, lobbyDim,
                true, true, true);

        MinecraftEntry mcEntry = new MinecraftEntry(true, false, true,
                false, false, false,
                false, false, true,
                false, false, 5000);

        GameruleConfigManager.GameruleConfig gameruleConfig = new GameruleConfigManager.GameruleConfig(gameId, name + " " + playerTotal + " " + teamSize, "#FFFFFFAA",
                brEntry, mcEntry, null);

        return gameruleConfig.toJson();
    }

    private static void addErangle100(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addCasual(0, 100, 4, Pubg8000x8000Casual.GAME_TIME,
                new Vec3(3816.0, 14, -3624.0), new Vec3(555, 555, 555), "Erangel")
        );
    }

    private static void addErangle64(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addCompetitive(1, 64, 4, Pubg8000x8000Casual.GAME_TIME,
                new Vec3(3816.0, 14, -3624.0), new Vec3(555, 555, 555), "Erangel")
        );
    }

    private static void addMiramar100(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addCasual(10, 100, 4, Pubg8000x8000Casual.GAME_TIME,
                new Vec3(2066.5, 34, -5097.5), new Vec3(555, 555, 555), "Miramar")
        );
    }

    private static void addMiramar64(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addCompetitive(11, 64, 4, Pubg8000x8000Casual.GAME_TIME,
                new Vec3(2066.5, 34, -5097.5), new Vec3(555, 555, 555), "Miramar")
        );
    }
}
