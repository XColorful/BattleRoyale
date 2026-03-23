package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;

import java.nio.file.Paths;

import static xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry.OVERWORLD_LEVEL_KEY;
import static xiao.battleroyale.config.common.game.gamerule.type.GameEntry.DEFAULT_DOWN_DAMAGE;
import static xiao.battleroyale.config.common.game.gamerule.type.GameEntry.DEFAULT_TEAM_COLORS;
import static xiao.battleroyale.config.common.game.zone.defaultconfigs.CFHC1000x1000Zone.MAX_GAME_TIME;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class CFHCGamerule {

    private static final String DEFAULT_FILE_NAME = "example_CustomFastHardcore_gamerule.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray gameruleConfigJson = new JsonArray();
        gameruleConfigJson.add(addCFHC(0, 32, 4, MAX_GAME_TIME,
                new Vec3(0, 80, 0), new Vec3(20, 80, 20), "CFHC"));
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), gameruleConfigJson);
    }

    public static JsonObject addCFHC(int gameId, int playerTotal, int teamSize, int GAME_TIME,
                                     Vec3 lobbyCenter, Vec3 lobbyDim, String name) {
        BattleroyaleEntry brEntry = new BattleroyaleEntry(OVERWORLD_LEVEL_KEY, playerTotal, teamSize, false, false, false,
                2, GAME_TIME, 1,
                lobbyCenter, lobbyDim,
                true, true, true, true, true,
                true, true);
        MinecraftEntry mcEntry = new MinecraftEntry(false, false, false,
                false, true, true,
                true, true, true,
                true, false, true,
                false, false, true, 0);
        GameEntry gameEntry = new GameEntry(true, 300, DEFAULT_TEAM_COLORS, true, GameTeam.DEFAULT_VANILLA_TEAM_FORMAT, true,
                20 * 60, 20 * 10, true,
                true, true, true, false, DEFAULT_DOWN_DAMAGE, 20,
                false, false, false, false,
                false, true, true, true, true, true,
                false, true, false, false, 0, 0,
                false, false, 20, 15,
                20 * 7, 20 * 5, 20 * 5);
        GameruleConfig gameruleConfig = new GameruleConfig(gameId, String.format("%s %s %s", name, playerTotal, teamSize), "#FFFFFFAA",
                brEntry, mcEntry, gameEntry, null);
        return gameruleConfig.toJson();
    }
}
