package xiao.battleroyale.config.common.game.zone.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.minecraft.InventoryIndex;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.config.common.game.zone.zonefunc.*;
import xiao.battleroyale.config.common.game.zone.zonefunc.event.EntityFuncEntry;
import xiao.battleroyale.config.common.game.zone.zoneshape.*;
import xiao.battleroyale.config.common.loot.type.ItemEntry;
import xiao.battleroyale.config.common.loot.type.MultiEntry;
import xiao.battleroyale.util.StringUtils;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

// Custom Fast Hardcore
public class CFHC1000x1000Zone {

    private static final String DEFAULT_FILE_NAME = "example_CustomFastHardcore_1000x1000_25minutes.json";

    // 初始阶段5分钟+2/3/4阶段各5分钟+最终阶段5分钟
    // 1/2/3/4/5阶段每秒扣血0.2/0.4/0.6/0.8/1血
    public static final int TOTAL_GAME_PHASE = 5;
    public static final int _5_minutes = 5 * 60 * 20;
    public static final int MAX_GAME_TIME = _5_minutes * (TOTAL_GAME_PHASE + 1); // 上限半小时

    public static final double INITIAL_BORDER_RADIUS = 500; // 直径1000

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray zoneConfigJson = new JsonArray();
        addPhase1(zoneConfigJson, _5_minutes, INITIAL_BORDER_RADIUS, TOTAL_GAME_PHASE, 0.2F, MAX_GAME_TIME);
        for (int i = 2; i <= 4; i++) {
            addCommonPhase(zoneConfigJson, i, _5_minutes, INITIAL_BORDER_RADIUS, TOTAL_GAME_PHASE, 0.2F * i);
        }
        addFinalPhase(zoneConfigJson, 5, _5_minutes, INITIAL_BORDER_RADIUS, TOTAL_GAME_PHASE, 1F);
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), zoneConfigJson);
    }

    // (开局5秒传送悬挂)5秒后刷装备+30秒无敌+5秒开始游戏消息
    // 往中心缩小
    public static void addPhase1(JsonArray zoneConfigJson, int phaseTime, double initRadius, int totalGamePhase, float damage, int maxGameTime) {
        int border_zoneId = 0;
        int message_zoneId = 1;
        int inventory_zoneId = 2;
        int muteki_zoneId = 3;
        int phase = 1;
        int shrink_zoneId = phase * 10;

        // 白色圆形边界
        // SafeFuncEntry border_safeFuncEntry = new SafeFuncEntry(0, 0, 20 * 10, -1, 666); // 圈基于玩家中心移动, 可能超出边界, 因此不用毒圈
        StartEntry startEntry = new StartEntry()
                .addFixedCenter(new Vec3(0, -64, 0))
                .addFixedDimension(new Vec3(initRadius, 384, initRadius));
        EndEntry endEntry = new EndEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0);
        CircleEntry border_circleEntry = new CircleEntry(startEntry, endEntry, false);
        ZoneConfig border_zoneConfig = new ZoneConfig(border_zoneId, "Game Border", "#FFFFFF44",
                0, maxGameTime, new NoFuncEntry(0, 0), border_circleEntry);
        zoneConfigJson.add(border_zoneConfig.toJson());

        // 蓝色圆形毒圈
        double shrinkRadius = initRadius / totalGamePhase;
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, phaseTime, 20, 0, damage);
        CircleEntry circleEntry = new CircleEntry(
                new StartEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0),
                new EndEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0)
                        .addRelativeDimension(new Vec3(-shrinkRadius, 0, -shrinkRadius)),
                false);
        int zoneColorInt = (int) (0xFF / 2.0 / totalGamePhase * phase);
        String zoneColor = "#0000FF" + String.format("%02X", zoneColorInt); // #RRGGBBAA
        ZoneConfig shrink_zoneConfig = new ZoneConfig(shrink_zoneId, "Phase 1 Shrink", zoneColor,
                0, phaseTime, safeFuncEntry, circleEntry);
        zoneConfigJson.add(shrink_zoneConfig.toJson());

        // 透明消息区
        MessageFuncEntry messageFuncEntry = new MessageFuncEntry(0, 40, 25, 10,
                true, 10, 80, 20,
                true, Component.literal("§6Game Start").withStyle(ChatFormatting.BOLD), Component.literal("Custom Fast Hardcore").withStyle(ChatFormatting.AQUA),
                true, Component.literal("Good luck").withStyle(ChatFormatting.GREEN));
        SquareEntry squareEntry = new SquareEntry(
                new StartEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0),
                new EndEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0),
                false);
        ZoneConfig message_zoneConfig = new ZoneConfig(message_zoneId, "Game Start Message", "#FFFFFF00",
                0, 80, messageFuncEntry, squareEntry);
        zoneConfigJson.add(message_zoneConfig.toJson());

        // 透明装备区
        ILootEntry lootEntry = new MultiEntry(Arrays.asList(
                new ItemEntry("minecraft:wooden_axe", "{components:{\"minecraft:enchantments\":{levels:{\"minecraft:efficiency\":10\"minecraft:unbreaking\":3}}}}" , 1), // We are 伐木不累!
                new ItemEntry("minecraft:iron_pickaxe", "{components:{\"minecraft:enchantments\":{levels:{\"minecraft:efficiency\":10,\"minecraft:unbreaking\":3}}}}", 1), // 哇! 神稿
                new ItemEntry("minecraft:cherry_planks", "" , 64), // 樱花木板
                new ItemEntry("minecraft:basalt", "", 64), // 玄武岩
                new ItemEntry("minecraft:ender_pearl", "", 1), // 末影珍珠
                new ItemEntry("minecraft:crafting_table", "" , 1), // 工作台
                new ItemEntry("minecraft:furnace", "" , 1), // 熔炉
                new ItemEntry("minecraft:torch", "", 16), // 火把
                new ItemEntry("minecraft:campfire", "", 3) // 营火
        ));
        InventoryFuncEntry inventoryFuncEntry = new InventoryFuncEntry(0, 0, 20, 1,
                false, true, InventoryIndex.HOTBAR_START, InventoryIndex.HOTBAR_END,
                lootEntry, -1);
        ZoneConfig inventory_zoneConfig = new ZoneConfig(inventory_zoneId, "Initial Inventory", "#FFAA0000",
                20 * 5, 20,
                inventoryFuncEntry, squareEntry);
        zoneConfigJson.add(inventory_zoneConfig.toJson());

        // 黄色无敌区
        int muteki_zoneTime = 20 * 30;
        MutekiFuncEntry mutekiFuncEntry = new MutekiFuncEntry(0, muteki_zoneTime, 20, -1,
                30);
        CircleEntry inner_circleEntry = new CircleEntry(
                new StartEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0).addRelativeDimension(new Vec3(-1, 0, -1)),
                new EndEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0).addRelativeDimension(new Vec3(-1, 64-384, -1)),
                false);
        ZoneConfig muteki_zoneConfig = new ZoneConfig(muteki_zoneId, "30s Muteki Time", "#FFD70077",
                0, muteki_zoneTime,
                mutekiFuncEntry, inner_circleEntry);
        zoneConfigJson.add(muteki_zoneConfig.toJson());
    }

    // 30s逐渐扩大至半径为阶段初始半径的0.1倍的轰炸区
    // 60s半径为轰炸区最大半径0.5倍逐渐缩小至0的实体刷新区（空投区）+卫道士刷新
    // 取中心点半径为阶段初始半径0.5倍的圆范围内随机取点作为点1, 全体存活玩家中心作为点2, 往点1点2中点缩小
    public static void addCommonPhase(JsonArray zoneConfigJson, int phase, int phaseTime, double initRadius, int totalGamePhase, float damage) {
        int prev_zoneId = (phase - 1) * 10;
        int forecast_zoneId = phase * 10;
        int shrink_zoneId = forecast_zoneId + 1;
        int message_zoneId = shrink_zoneId + 1;
        int bomb_zoneId = message_zoneId + 1;
        int entity_zoneId = bomb_zoneId + 1;

        // 绿色预告圈
        double shrinkRadius = initRadius / totalGamePhase;
        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(prev_zoneId, 1)
                .addPreviousDimension(prev_zoneId, 1);
        EndEntry endEntry = new EndEntry()
                .addPreviousCenter(forecast_zoneId, 0)
                .addCenterRange(shrinkRadius, false, true) // 圈缩小shrinkRadius, 随机偏移范围也为shrinkRadius
                .addPlayerCenterLerp(0.5) // 当玩家都在圈边, 避免将玩家强制拉进圈内(而是让圈往玩家中心去)
                .addPreviousDimension(forecast_zoneId, 0)
                .addRelativeDimension(new Vec3(-shrinkRadius, 0, -shrinkRadius));
        CircleEntry forecast_circleEntry = new CircleEntry(startEntry, endEntry ,false);
        String forecast_zoneColor = "#00FF00" + String.format("%02X", (int) (0xFF / 2.0 / totalGamePhase)); // #RRGGBBAA
        ZoneConfig forecast_zoneConfig = new ZoneConfig(forecast_zoneId, String.format("Phase %s Forecast", phase), forecast_zoneColor,
                prev_zoneId, phaseTime, phaseTime,
                new NoFuncEntry(0, 20), forecast_circleEntry);
        zoneConfigJson.add(forecast_zoneConfig.toJson());
        // 蓝色圆形毒圈
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, phaseTime, 20, 0, damage);
        CircleEntry circleEntry = new CircleEntry(
                new StartEntry().addPreviousCenter(forecast_zoneId, 0).addPreviousDimension(forecast_zoneId, 0),
                new EndEntry().addPreviousCenter(forecast_zoneId, 1).addPreviousDimension(forecast_zoneId, 1),
                false);
        int zoneColorInt = (int) (0xFF / 2.0 / totalGamePhase * phase);
        String zoneColor = "#0000FF" + String.format("%02X", zoneColorInt); // #RRGGBBAA
        ZoneConfig shrink_zoneConfig = new ZoneConfig(shrink_zoneId, String.format("Phase %s Shrink", phase), zoneColor,
                forecast_zoneId, 0, phaseTime,
                safeFuncEntry, circleEntry);
        zoneConfigJson.add(shrink_zoneConfig.toJson());

        // 透明消息区
        MessageFuncEntry messageFuncEntry = new MessageFuncEntry(0, 0, 25, 10,
                true, 10, 80, 20,
                true, Component.literal(""), Component.literal(String.format("§9Phase§b %s", phase)),
                false, Component.literal(""));
        SquareEntry squareEntry = new SquareEntry(
                new StartEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0),
                new EndEntry().addPreviousCenter(0, 0).addPreviousDimension(0,0),
                false);
        ZoneConfig message_zoneConfig = new ZoneConfig(message_zoneId, String.format("Phase %s message", phase), "#00000000",
                forecast_zoneId, 0, 20 * 4,
                messageFuncEntry, squareEntry);
        zoneConfigJson.add(message_zoneConfig.toJson());

        // 红色轰炸区
        JsonObject bomb_jsonTag = new JsonObject();
        bomb_jsonTag.addProperty("distributionType", "CircleGrid");
        bomb_jsonTag.addProperty("lootFactorContribution", 1);
        bomb_jsonTag.addProperty("fixedSimulation", 100);
        bomb_jsonTag.addProperty("allowOnBorder", true);
        bomb_jsonTag.addProperty("globalShrinkRatio", 1);
        bomb_jsonTag.addProperty("needShuffle", true);
        bomb_jsonTag.addProperty("randomRange", StringUtils.vectorToString(new Vec3(5, 0, 5)));
        bomb_jsonTag.addProperty("findGround", true);
        bomb_jsonTag.addProperty("limitToBottom", false);
        bomb_jsonTag.addProperty("limitToTop", true);
        bomb_jsonTag.addProperty("additionalOffset", StringUtils.vectorToString(new Vec3(0, 30, 0)));
        bomb_jsonTag.addProperty("ignoreOutside", true);
        bomb_jsonTag.addProperty("relativeMovementRandomRange", StringUtils.vectorToString(new Vec3(0.5, 3, 0.5)));
        int bomb_zoneTime = phaseTime / 10;
        EntityFuncEntry bomb_entityFuncEntry = new EntityFuncEntry(0, bomb_zoneTime, 10, 0,
                "cbra:0.4.4", bomb_jsonTag, 1, "{Motion:[0d,-5d,0d]}");
        CircleEntry bomb_circleEntry = new CircleEntry(
                new StartEntry().addPreviousCenter(forecast_zoneId, 0).addCenterRange(shrinkRadius / 2).addPlayerCenterLerp(0.5)
                        .addFixedDimension(new Vec3(0, 384, 0)),
                new EndEntry().addPreviousCenter(bomb_zoneId, 0)
                        .addFixedDimension(new Vec3(shrinkRadius * (totalGamePhase - phase + 1) * 0.2, 384, shrinkRadius * (totalGamePhase - phase + 1) * 0.2)),
                false);
        String bomb_color = "#FF0000" + String.format("%02X", (int) (0xFF / 2.0 / totalGamePhase)); // #RRGGBBAA
        ZoneConfig bomb_zoneConfig = new ZoneConfig(bomb_zoneId, String.format("Phase %s bomb zone", phase), bomb_color,
                message_zoneId, 20 * 5, bomb_zoneTime,
                bomb_entityFuncEntry, bomb_circleEntry);
        zoneConfigJson.add(bomb_zoneConfig.toJson());

        // 青色实体刷新区
        JsonObject jsonTag = new JsonObject();
        jsonTag.addProperty("distributionType", "GoldenSpiral");
        jsonTag.addProperty("lootFactorContribution", 0);
        jsonTag.addProperty("fixedSimulation", 25);
        jsonTag.addProperty("allowOnBorder", true);
        jsonTag.addProperty("globalShrinkRatio", 1);
        jsonTag.addProperty("needShuffle", true);
        jsonTag.addProperty("randomRange", StringUtils.vectorToString(new Vec3(0, 0, 0)));
        jsonTag.addProperty("findGround", true);
        jsonTag.addProperty("limitToBottom", false);
        jsonTag.addProperty("limitToTop", true);
        jsonTag.addProperty("additionalOffset", StringUtils.vectorToString(new Vec3(0, 0.5, 0)));
        jsonTag.addProperty("ignoreOutside", true);
        jsonTag.addProperty("relativeMovementRandomRange", StringUtils.vectorToString(Vec3.ZERO));
        int entity_zoneTime = bomb_zoneTime * 2;
        EntityFuncEntry entityFuncEntry = new EntityFuncEntry(0, entity_zoneTime, 20 * 10, 0,
                "cbra:0.4.4", jsonTag, 2, "{Motion:[0d,0.5d,0d]}");
        CircleEntry entity_circleEntry = new CircleEntry(
                new StartEntry().addPreviousCenter(bomb_zoneId, 1)
                        .addFixedDimension(new Vec3(shrinkRadius * (totalGamePhase - phase + 1) * 0.1, 384, shrinkRadius * (totalGamePhase - phase + 1) * 0.1)),
                new EndEntry().addPreviousCenter(entity_zoneId, 0)
                        .addFixedDimension(new Vec3(0, 384, 0)),
                false);
        String entity_color = "#00FFFF" + String.format("%02X", (int) (0xFF / 2.0 / totalGamePhase)); // #RRGGBBAA
        ZoneConfig entity_zoneConfig = new ZoneConfig(entity_zoneId, String.format("Phase %s entity zone", phase), entity_color,
                bomb_zoneId, bomb_zoneTime, entity_zoneTime,
                entityFuncEntry, entity_circleEntry);
        zoneConfigJson.add(entity_zoneConfig.toJson());
    }

    // 以全体玩家中心为初始点, 往(0,0,0)缩小至半径为0.5的红色球体
    // 缩圈时间为phaseTime, 持续时间为phaseTime * 2
    public static void addFinalPhase(JsonArray zoneConfigJson, int phase, int phaseTime, double initRadius, int totalGamePhase, float damage) {
        int prev_zoneId = (phase - 1) * 10;
        int shrink_zoneId = phase * 10;
        int message_zoneId = shrink_zoneId + 1;

        // 红色球体毒圈
        double shrinkRadius = initRadius / totalGamePhase;
        SafeFuncEntry safeFuncEntry = new SafeFuncEntry(0, phaseTime, 20, 0, damage);
        StartEntry startEntry = new StartEntry()
                .addPreviousCenter(prev_zoneId, 1)
                .addPlayerCenterLerp(1) // 以玩家中心为起点
                .addFixedDimension(new Vec3(shrinkRadius, shrinkRadius, shrinkRadius));
        EndEntry endEntry = new EndEntry()
                .addFixedCenter(new Vec3(0, 64, 0)) // 缩到(0,64,0), 把地下的赶上来
                .addFixedDimension(new Vec3(0.5, 0.5, 0.5)); // 只能站一个人
        SphereEntry sphereEntry = new SphereEntry(startEntry, endEntry, false);
        int zoneColorInt = (int) (0xFF / 2.0 / totalGamePhase * phase);
        String zoneColor = "#5555FF" + String.format("%02X", zoneColorInt); // 红球看着不舒服, 取§9
        ZoneConfig shrink_zoneConfig = new ZoneConfig(shrink_zoneId, String.format("Phase %s (Final)", phase), zoneColor,
                prev_zoneId, phaseTime, phaseTime * 2,
                safeFuncEntry, sphereEntry);
        zoneConfigJson.add(shrink_zoneConfig.toJson());

        // 透明消息区
        MessageFuncEntry messageFuncEntry = new MessageFuncEntry(0, 0, 25, 10,
                true, 10, 80, 20,
                true, Component.literal(""), Component.literal("§cFinal Phase"),
                false, Component.literal(""));
        SquareEntry squareEntry = new SquareEntry(
                new StartEntry().addPreviousCenter(0, 0).addPreviousDimension(0, 0),
                new EndEntry().addPreviousCenter(0, 0).addPreviousDimension(0,0),
                false);
        ZoneConfig message_zoneConfig = new ZoneConfig(message_zoneId, String.format("Phase %s message", phase), "#00000000",
                shrink_zoneId, 0, 20 * 4,
                messageFuncEntry, squareEntry);
        zoneConfigJson.add(message_zoneConfig.toJson());
    }
}
