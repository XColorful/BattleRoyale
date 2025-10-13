package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.sub.IConfigAppliable;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class BattleroyaleEntry implements IGameruleEntry, IConfigAppliable {

    public static final String OVERWORLD_LEVEL_KEY = Level.OVERWORLD.location().toString();
    public String defaultLevelKey;
    public int playerTotal;
    public int teamSize;
    public boolean aiTeammate;
    public boolean aiEnemy;
    public int requiredTeamToStart;
    public int maxGameTime;
    public int winnerTeamTotal;
    public Vec3 lobbyCenterPos;
    public Vec3 lobbyDimension;
    public boolean lobbyMuteki;
    public boolean lobbyHeal;
    public boolean lobbyChangeGamemode;
    public boolean lobbyTeleportDropInventory;
    public boolean lobbyTeleportClearInventory;
    public boolean recordGameStats;
    public boolean autoJoinGame;

    public BattleroyaleEntry(String defaultLevelKey, int playerTotal, int teamSize, boolean aiTeammate, boolean aiEnemy,
                             int requiredTeamToStart, int maxGameTime, int winnerTeamTotal,
                             Vec3 lobbyCenterPos, Vec3 lobbyDimension, boolean lobbyMuteki, boolean lobbyHeal, boolean lobbyChangeGamemode, boolean lobbyTeleportDropInventory, boolean lobbyTeleportClearInventory,
                             boolean recordGameStats, boolean autoJoinGame) {
        this.defaultLevelKey = defaultLevelKey;
        this.playerTotal = playerTotal;
        this.teamSize = teamSize;
        this.aiTeammate = aiTeammate;
        this.aiEnemy = aiEnemy;
        this.requiredTeamToStart = requiredTeamToStart;
        this.maxGameTime = maxGameTime;
        this.winnerTeamTotal = winnerTeamTotal;
        this.lobbyCenterPos = lobbyCenterPos;
        this.lobbyDimension = lobbyDimension;
        this.lobbyMuteki = lobbyMuteki;
        this.lobbyHeal = lobbyHeal;
        this.lobbyChangeGamemode = lobbyChangeGamemode;
        this.lobbyTeleportDropInventory = lobbyTeleportDropInventory;
        this.lobbyTeleportClearInventory = lobbyTeleportClearInventory;
        this.recordGameStats = recordGameStats;
        this.autoJoinGame = autoJoinGame;
    }
    @Override public @NotNull BattleroyaleEntry copy() {
        return new BattleroyaleEntry(defaultLevelKey, playerTotal, teamSize, aiTeammate, aiEnemy,
                requiredTeamToStart, maxGameTime, winnerTeamTotal,
                lobbyCenterPos, lobbyDimension, lobbyMuteki, lobbyHeal, lobbyChangeGamemode, lobbyTeleportDropInventory, lobbyTeleportClearInventory,
                recordGameStats, autoJoinGame);
    }

    @Override
    public String getType() {
        return "battleroyaleEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(BattleroyaleEntryTag.DEFAULT_LEVEL_KEY, defaultLevelKey);
        jsonObject.addProperty(BattleroyaleEntryTag.PLAYER_TOTAL, playerTotal);
        jsonObject.addProperty(BattleroyaleEntryTag.TEAM_SIZE, teamSize);
        jsonObject.addProperty(BattleroyaleEntryTag.AI_TEAMMATE, aiTeammate);
        jsonObject.addProperty(BattleroyaleEntryTag.AI_ENEMY, aiEnemy);
        jsonObject.addProperty(BattleroyaleEntryTag.REQUIRED_TEAM_TO_START, requiredTeamToStart);
        jsonObject.addProperty(BattleroyaleEntryTag.MAX_GAME_TIME, maxGameTime);
        jsonObject.addProperty(BattleroyaleEntryTag.WINNER_TEAM_TOTAL, winnerTeamTotal);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_CENTER, StringUtils.vectorToString(lobbyCenterPos));
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_DIMENSION, StringUtils.vectorToString(lobbyDimension)); // 临时使用字符串字面量
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_MUTEKI, lobbyMuteki);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_HEAL, lobbyHeal);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_CHANGE_GAMEMODE, lobbyChangeGamemode);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_TELEPORT_DROP_INVENTORY, lobbyTeleportDropInventory);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_TELEPORT_CLEAR_INVENTORY, lobbyTeleportClearInventory);
        jsonObject.addProperty(BattleroyaleEntryTag.RECORD_STATS, recordGameStats);
        jsonObject.addProperty(BattleroyaleEntryTag.AUTO_JOIN, autoJoinGame);
        return jsonObject;
    }

    public static BattleroyaleEntry fromJson(JsonObject jsonObject) {
        String defaultLevelKey = JsonUtils.getJsonString(jsonObject, BattleroyaleEntryTag.DEFAULT_LEVEL_KEY, OVERWORLD_LEVEL_KEY);
        int playerTotal = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.PLAYER_TOTAL, 0);
        int teamSize = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.TEAM_SIZE, 0);
        boolean aiTeammate = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.AI_TEAMMATE, false);
        boolean aiEnemy = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.AI_ENEMY, false);
        int requiredTeamToStart = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.REQUIRED_TEAM_TO_START, 2);
        int maxGameTime = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.MAX_GAME_TIME, 0);
        int winnerTeamTotal = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.WINNER_TEAM_TOTAL, 1);
        Vec3 lobbyCenterPos = JsonUtils.getJsonVec(jsonObject, BattleroyaleEntryTag.LOBBY_CENTER, null);
        Vec3 lobbyDimension = JsonUtils.getJsonVec(jsonObject, BattleroyaleEntryTag.LOBBY_DIMENSION, null);
        if (lobbyCenterPos == null || lobbyDimension == null) {
            BattleRoyale.LOGGER.info("Invalid lobbyCenter or lobbyDimension for BattleroyaleEntry, skipped");
            return null;
        }
        boolean lobbyMuteki = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_MUTEKI, false);
        boolean lobbyHeal = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_HEAL, true);
        boolean lobbyChangeGamemode = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_CHANGE_GAMEMODE, true);
        boolean lobbyTeleportDropInventory = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_TELEPORT_DROP_INVENTORY, false);
        boolean lobbyTeleportClearInventory = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_TELEPORT_CLEAR_INVENTORY, false);
        boolean recordGameStats = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.RECORD_STATS, true);
        boolean autoJoinGame = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.AUTO_JOIN, false);
        return new BattleroyaleEntry(defaultLevelKey, playerTotal, teamSize, aiTeammate, aiEnemy,
                requiredTeamToStart, maxGameTime, winnerTeamTotal,
                lobbyCenterPos, lobbyDimension, lobbyMuteki, lobbyHeal, lobbyChangeGamemode, lobbyTeleportDropInventory, lobbyTeleportClearInventory,
                recordGameStats, autoJoinGame);
    }

    @Override
    public void applyDefault() {
        GameManager.get().setDefaultLevel(defaultLevelKey);
        SpawnManager.get().setLobby(lobbyCenterPos, lobbyDimension, lobbyMuteki, lobbyHeal, lobbyChangeGamemode, lobbyTeleportDropInventory, lobbyTeleportClearInventory);
    }
}