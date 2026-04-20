package xiao.battleroyale.config.common.game.gamerule.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.compat.murdermystery.MurderMystery;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.ExtraRuleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.util.JsonUtils;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry.OVERWORLD_LEVEL_KEY;
import static xiao.battleroyale.config.common.game.zone.defaultconfigs.MurderMystery256x256Zone.*;
import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class MurderMysteryGamerule {

    private static final String DEFAULT_FILE_NAME = "example_murdermystery.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray gameruleConfigJson = new JsonArray();
        addMurderMysterySolo(gameruleConfigJson);
        addMurderMysteryDual(gameruleConfigJson);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), gameruleConfigJson);
    }

    // 默认应该只玩单人模式，不然能看队友血量
    public static void addMurderMysterySolo(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addMurderMystery(0, 16, 1));
    }
    // 保留一个每2人互相可见血条/位置的玩法
    public static void addMurderMysteryDual(JsonArray gameruleConfigJson) {
        gameruleConfigJson.add(addMurderMystery(1, 32, 2));
    }

    public static String SURVIVOR_ITEM_TAG = "survivorItem";
    public static String MURDERER_ITEM_TAG = "murdererItem";
    private static JsonObject addMurderMystery(int gameId, int playerTotal, int teamSize) {
        // 10 秒后设置阵营
        int roleDelay = 10 * 20;
        // 倒计时 15 秒
        int countdown = 15;
        int countdownTick = countdown * 20;
        // 生存时长 10 分钟
        int surviveTime = 10 * 60 * 20;
        BattleroyaleEntry brEntry = new BattleroyaleEntry(OVERWORLD_LEVEL_KEY, playerTotal, teamSize, false, false, false,
                3, 99999, 1,
                new Vec3(0, 70, 0), new Vec3(128, 128, 128),
                true, true, true, true, true,
                true, true);
        MinecraftEntry mcEntry = new MinecraftEntry(true, false, true,
                false, false, false,
                false, true, true,
                false, false, true,
                false, false, true, true, 5000);
        GameEntry gameEntry = new GameEntry();
        // 开友伤
        gameEntry.friendlyFire = true;
        // 与 GameTeam 无关
        gameEntry.spectateAfterTeam = false;
        // 不回大厅
        gameEntry.teleportWinnerAfterGame = false;
        gameEntry.teleportAfterGame = false;

        // MurderMystery 0.5.5
        JsonObject jsonTag = new JsonObject();
        jsonTag.addProperty("gameStartTick", countdownTick);
        jsonTag.addProperty("countdownSeconds", countdown);
        jsonTag.addProperty("surviveTimeGoal", countdownTick + surviveTime);
        jsonTag.addProperty("sendProgressBar", true);
        jsonTag.addProperty("progressPrecision", 200);
        jsonTag.addProperty("progressBarColor", "green");
        jsonTag.addProperty("progressBarOverlay", "notched_10");
        jsonTag.addProperty("sendGamePlayerNotificationMessage", false);
        jsonTag.addProperty("filterItemPickup", true);
        jsonTag.addProperty("survivorItemTag", SURVIVOR_ITEM_TAG);
        jsonTag.addProperty("murdererItemTag", MURDERER_ITEM_TAG);
        jsonTag.addProperty("survivorDelay", roleDelay);
        jsonTag.add("survivorFuncs", JsonUtils.writeIntListToJson(Arrays.asList(SURVIVOR_ITEM_LOOT)));
        jsonTag.addProperty("detectiveDelay", roleDelay);
        jsonTag.add("detectiveFuncs", JsonUtils.writeIntListToJson(Arrays.asList(DETECTIVE_ITEM_LOOT)));
        jsonTag.addProperty("murdererDelay", roleDelay);
        jsonTag.add("murdererFuncs", JsonUtils.writeIntListToJson(Arrays.asList(MURDERER_ITEM_LOOT)));
        MurderMystery murderMystery = MurderMystery.get();
        jsonTag.addProperty("apiFunctionRegister", String.format("%s:register", murderMystery.getModId()));
        jsonTag.addProperty("apiFunctionUnregister", String.format("%s:unregister", murderMystery.getModId()));
        ExtraRuleEntry extraRuleEntry = new ExtraRuleEntry(String.format("%s:murdermystery", murderMystery.getModId()), jsonTag);

        GameruleConfigManager.GameruleConfig gameruleConfig = new GameruleConfigManager.GameruleConfig(gameId, "MurderMystery " + playerTotal + " " + teamSize, "#FFFFFFAA",
                brEntry, mcEntry, gameEntry, extraRuleEntry);
        return gameruleConfig.toJson();
    }
}
