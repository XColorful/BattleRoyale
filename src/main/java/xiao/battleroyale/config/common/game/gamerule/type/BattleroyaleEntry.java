package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigAppliable;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class BattleroyaleEntry implements IGameruleEntry, IConfigAppliable {

    public static final String OVERWORLD_LEVEL_KEY = Level.OVERWORLD.location().toString();
    public final String defaultLevelKey;
    public final int playerTotal;
    public final int teamSize;
    public final boolean aiTeammate;
    public final boolean aiEnemy;
    public final int maxGameTime;
    public final Vec3 lobbyCenterPos;
    public final Vec3 lobbyDimension;
    public final boolean lobbyMuteki;
    public final boolean lobbyHeal;
    public final boolean lobbyChangeGamemode;
    public final boolean recordGameStats;
    public final boolean autoJoinGame;
    public final boolean clearInventory;

    public BattleroyaleEntry(String defaultLevelKey, int playerTotal, int teamSize, boolean aiTeammate, boolean aiEnemy, int maxGameTime,
                             Vec3 lobbyCenterPos, Vec3 lobbyDimension, boolean lobbyMuteki, boolean lobbyHeal, boolean lobbyChangeGamemode,
                             boolean recordGameStats, boolean autoJoinGame, boolean clearInventory) {
        this.defaultLevelKey = defaultLevelKey;
        this.playerTotal = playerTotal;
        this.teamSize = teamSize;
        this.aiTeammate = aiTeammate;
        this.aiEnemy = aiEnemy;
        this.maxGameTime = maxGameTime;
        this.lobbyCenterPos = lobbyCenterPos;
        this.lobbyDimension = lobbyDimension;
        this.lobbyMuteki = lobbyMuteki;
        this.lobbyHeal = lobbyHeal;
        this.lobbyChangeGamemode = lobbyChangeGamemode;
        this.recordGameStats = recordGameStats;
        this.autoJoinGame = autoJoinGame;
        this.clearInventory = clearInventory;
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
        jsonObject.addProperty(BattleroyaleEntryTag.MAX_GAME_TIME, maxGameTime);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_CENTER, StringUtils.vectorToString(lobbyCenterPos));
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_DIMENSION, StringUtils.vectorToString(lobbyDimension)); // 临时使用字符串字面量
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_MUTEKI, lobbyMuteki);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_HEAL, lobbyHeal);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_CHANGE_GAMEMODE, lobbyChangeGamemode);
        jsonObject.addProperty(BattleroyaleEntryTag.RECORD_STATS, recordGameStats);
        jsonObject.addProperty(BattleroyaleEntryTag.AUTO_JOIN, autoJoinGame);
        jsonObject.addProperty(BattleroyaleEntryTag.CLEAR_INVENTORY, clearInventory);
        return jsonObject;
    }

    public static BattleroyaleEntry fromJson(JsonObject jsonObject) {
        String defaultLevelKey = JsonUtils.getJsonString(jsonObject, BattleroyaleEntryTag.DEFAULT_LEVEL_KEY, OVERWORLD_LEVEL_KEY);
        int playerTotal = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.PLAYER_TOTAL, 0);
        int teamSize = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.TEAM_SIZE, 0);
        boolean aiTeammate = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.AI_TEAMMATE, false);
        boolean aiEnemy = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.AI_ENEMY, false);
        int maxGameTime = JsonUtils.getJsonInt(jsonObject, BattleroyaleEntryTag.MAX_GAME_TIME, 0);
        Vec3 lobbyCenterPos = JsonUtils.getJsonVec(jsonObject, BattleroyaleEntryTag.LOBBY_CENTER, null);
        Vec3 lobbyDimension = JsonUtils.getJsonVec(jsonObject, BattleroyaleEntryTag.LOBBY_DIMENSION, null);
        if (lobbyCenterPos == null || lobbyDimension == null) {
            BattleRoyale.LOGGER.info("Invalid lobbyCenter or lobbyDimension for BattleroyaleEntry, skipped");
            return null;
        }
        boolean lobbyMuteki = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_MUTEKI, false);
        boolean lobbyHeal = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_HEAL, true);
        boolean lobbyChangeGamemode = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.LOBBY_CHANGE_GAMEMODE, true);
        boolean recordGameStats = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.RECORD_STATS, false);
        boolean autoJoinGame = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.AUTO_JOIN, false);
        boolean clearInventory = JsonUtils.getJsonBool(jsonObject, BattleroyaleEntryTag.CLEAR_INVENTORY, false);
        return new BattleroyaleEntry(defaultLevelKey, playerTotal, teamSize, aiTeammate, aiEnemy, maxGameTime,
                lobbyCenterPos, lobbyDimension, lobbyMuteki, lobbyHeal, lobbyChangeGamemode,
                recordGameStats, autoJoinGame, clearInventory);
    }

    @Override
    public void applyDefault() {
        GameManager.get().setDefaultLevel(defaultLevelKey);
        SpawnManager.get().setLobby(lobbyCenterPos, lobbyDimension, lobbyMuteki, lobbyHeal, lobbyChangeGamemode);
    }
}