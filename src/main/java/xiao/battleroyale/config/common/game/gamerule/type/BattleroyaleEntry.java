package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.util.StringUtils;

public class BattleroyaleEntry implements IGameruleEntry {

    private int playerTotal;
    private int teamSize;
    private boolean aiTeammate;
    private boolean aiEnemy;
    private int maxGameTime;
    private Vec3 lobbyCenterPos;
    private Vec3 lobbyDimension;
    private boolean lobbyMuteki;
    private boolean recordGameStats;
    private boolean autoJoinGame;

    public BattleroyaleEntry(int playerTotal, int teamSize, boolean aiTeammate, boolean aiEnemy, int maxGameTime,
                             Vec3 lobbyCenterPos, Vec3 lobbyDimension, boolean lobbyMuteki,
                             boolean recordGameStats, boolean autoJoinGame) {
        this.playerTotal = playerTotal;
        this.teamSize = teamSize;
        this.aiTeammate = aiTeammate;
        this.aiEnemy = aiEnemy;
        this.maxGameTime = maxGameTime;
        this.lobbyCenterPos = lobbyCenterPos;
        this.lobbyDimension = lobbyDimension;
        this.lobbyMuteki = lobbyMuteki;
        this.recordGameStats = recordGameStats;
        this.autoJoinGame = autoJoinGame;
    }

    @Override
    public String getType() {
        return "Hyper muteki lobby~";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(BattleroyaleEntryTag.PLAYER_TOTAL, playerTotal);
        jsonObject.addProperty(BattleroyaleEntryTag.TEAM_SIZE, teamSize);
        jsonObject.addProperty(BattleroyaleEntryTag.AI_TEAMMATE, aiTeammate);
        jsonObject.addProperty(BattleroyaleEntryTag.AI_ENEMY, aiEnemy);
        jsonObject.addProperty(BattleroyaleEntryTag.MAX_GAME_TIME, maxGameTime);
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_CENTER, StringUtils.vectorToString(lobbyCenterPos));
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_DIMENSION, StringUtils.vectorToString(lobbyDimension)); // 临时使用字符串字面量
        jsonObject.addProperty(BattleroyaleEntryTag.LOBBY_MUTEKI, lobbyMuteki);
        jsonObject.addProperty(BattleroyaleEntryTag.RECORD_STATS, recordGameStats);
        jsonObject.addProperty(BattleroyaleEntryTag.AUTO_JOIN, autoJoinGame);
        return jsonObject;
    }

    @Nullable
    public static BattleroyaleEntry fromJson(JsonObject jsonObject) {
        int playerTotal = jsonObject.has(BattleroyaleEntryTag.PLAYER_TOTAL) ? jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.PLAYER_TOTAL).getAsInt() : 0;
        int teamSize = jsonObject.has(BattleroyaleEntryTag.TEAM_SIZE) ? jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.TEAM_SIZE).getAsInt() : 0;
        boolean aiTeammate = jsonObject.has(BattleroyaleEntryTag.AI_TEAMMATE) && jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.AI_TEAMMATE).getAsBoolean();
        boolean aiEnemy = jsonObject.has(BattleroyaleEntryTag.AI_ENEMY) && jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.AI_ENEMY).getAsBoolean();
        int maxGameTime = jsonObject.has(BattleroyaleEntryTag.MAX_GAME_TIME) ? jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.MAX_GAME_TIME).getAsInt() : 0;
        String lobbyCenterString = jsonObject.has(BattleroyaleEntryTag.LOBBY_CENTER) ? jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.LOBBY_CENTER).getAsString() : null;
        Vec3 lobbyCenterPos = StringUtils.parseVectorString(lobbyCenterString);
        String lobbyDimensionString = jsonObject.has(BattleroyaleEntryTag.LOBBY_DIMENSION) ? jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.LOBBY_DIMENSION).getAsString() : null;
        Vec3 lobbyDimension = StringUtils.parseVectorString(lobbyDimensionString);
        if (lobbyCenterPos == null || lobbyDimension == null) {
            BattleRoyale.LOGGER.info("Invalid lobbyCenter or lobbyDimension for BattleroyaleEntry, skipped");
            return null;
        }
        boolean lobbyMuteki = jsonObject.has(BattleroyaleEntryTag.LOBBY_MUTEKI) && jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.LOBBY_MUTEKI).getAsBoolean();
        boolean recordGameStats = jsonObject.has(BattleroyaleEntryTag.RECORD_STATS) && jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.RECORD_STATS).getAsBoolean();
        boolean autoJoinGame = jsonObject.has(BattleroyaleEntryTag.AUTO_JOIN) && jsonObject.getAsJsonPrimitive(BattleroyaleEntryTag.AUTO_JOIN).getAsBoolean();
        return new BattleroyaleEntry(playerTotal, teamSize, aiTeammate, aiEnemy, maxGameTime,
                lobbyCenterPos, lobbyDimension, lobbyMuteki,
                recordGameStats, autoJoinGame);
    }
}