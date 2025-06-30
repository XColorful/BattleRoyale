package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.IConfigAppliable;
import xiao.battleroyale.api.game.gamerule.GameEntryTag;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.util.JsonUtils;

import java.util.Arrays;
import java.util.List;

public class GameEntry implements IGameruleEntry, IConfigAppliable {

    public final int teamMsgExpireTimeSeconds;
    public final List<String> teamColors;
    public static final List<String> DEFAULT_TEAM_COLORS = Arrays.asList(
            "#E9ECEC", "#F07613", "#BD44B3", "#3AAFD9", "#F8C627", "#70B919", "#ED8DAC", "#8E8E86",
            "#A0A0A0", "#158991", "#792AAC", "#35399D", "#724728", "#546D1B", "#A02722", "#141519");

    public final int maxPlayerInvalidTime;
    public final int maxBotInvalidTime;
    public final boolean removeInvalidTeam;

    public final boolean allowRemainingBot;
    public final boolean keepTeamAfterGame;
    public final boolean teleportAfterGame;
    public final boolean teleportWinnerAfterGame;
    public final int winnerFireworkId;
    public final int winnerParticleId;

    public final int messageCleanFreq;
    public final int messageExpireTime;
    public final int messageSyncFreq;

    public GameEntry() {
        this(300 * 1000, DEFAULT_TEAM_COLORS,
                20 * 60, 20 * 10, false,
                true, true, true, false, 0, 0,
                20 * 7, 20 * 5, 20 * 5);
    }

    public GameEntry(int teamMsgExpireTimeSeconds, List<String> teamColors,
                     int maxPlayerInvalidTime, int maxBotInvalidTime, boolean removeInvalidTeam,
                     boolean allowRemainingBot, boolean keepTeamAfterGame, boolean teleportAfterGame, boolean teleportWinnerAfterGame, int winnerFireworkId, int winnerParticleId,
                     int messageCleanFreq, int messageExpireTime, int messageSyncFreq) {
        this.teamMsgExpireTimeSeconds = teamMsgExpireTimeSeconds;
        this.teamColors = teamColors;
        this.maxPlayerInvalidTime = maxPlayerInvalidTime;
        this.maxBotInvalidTime = maxBotInvalidTime;
        this.removeInvalidTeam = removeInvalidTeam;
        this.allowRemainingBot = allowRemainingBot;
        this.keepTeamAfterGame = keepTeamAfterGame;
        this.teleportAfterGame = teleportAfterGame;
        this.teleportWinnerAfterGame = teleportWinnerAfterGame;
        this.winnerFireworkId = winnerFireworkId;
        this.winnerParticleId = winnerParticleId;
        this.messageCleanFreq = messageCleanFreq;
        this.messageExpireTime = messageExpireTime;
        this.messageSyncFreq = messageSyncFreq;
    }

    @Override
    public String getType() {
        return "gameEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GameEntryTag.TEAM_MSG_EXPIRE_SECONDS, teamMsgExpireTimeSeconds);
        jsonObject.add(GameEntryTag.TEAM_COLORS, JsonUtils.writeStringListToJson(teamColors));

        jsonObject.addProperty(GameEntryTag.MAX_PLAYER_INVALID_TIME, maxPlayerInvalidTime);
        jsonObject.addProperty(GameEntryTag.MAX_BOT_INVALID_TIME, maxBotInvalidTime);
        jsonObject.addProperty(GameEntryTag.REMOVE_INVALID_TEAM, removeInvalidTeam);
        jsonObject.addProperty(GameEntryTag.ALLOW_REMAINING_BOT, allowRemainingBot);
        jsonObject.addProperty(GameEntryTag.KEEP_TEAM_AFTER_GAME, keepTeamAfterGame);
        jsonObject.addProperty(GameEntryTag.TELEPORT_AFTER_GAME, teleportAfterGame);
        jsonObject.addProperty(GameEntryTag.TELEPORT_WINNER_AFTER_GAME, teleportWinnerAfterGame);
        jsonObject.addProperty(GameEntryTag.WINNER_FIREWORK_ID, winnerFireworkId);
        jsonObject.addProperty(GameEntryTag.WINNER_PARTICLE_ID, winnerParticleId);

        jsonObject.addProperty(GameEntryTag.MESSAGE_CLEAN_FREQUENCY, messageCleanFreq);
        jsonObject.addProperty(GameEntryTag.MESSAGE_EXPIRE_TIME, messageExpireTime);
        jsonObject.addProperty(GameEntryTag.MESSAGE_FORCE_SYNC_FREQUENCY, messageSyncFreq);
        return jsonObject;
    }

    @NotNull
    public static GameEntry fromJson(JsonObject jsonObject) {
        int teamMsgExpireTimeSeconds = JsonUtils.getJsonInt(jsonObject, GameEntryTag.TEAM_MSG_EXPIRE_SECONDS, 300 * 1000);
        List<String> teamColors = JsonUtils.getJsonStringList(jsonObject, GameEntryTag.TEAM_COLORS);

        int maxInvalidTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MAX_PLAYER_INVALID_TIME, 20 * 60);
        int maxBotInvalidTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MAX_BOT_INVALID_TIME, 20 * 10);
        boolean removeInvalidTeam = JsonUtils.getJsonBool(jsonObject, GameEntryTag.REMOVE_INVALID_TEAM, false);
        boolean allowRemainingBot = JsonUtils.getJsonBool(jsonObject, GameEntryTag.ALLOW_REMAINING_BOT, false);
        boolean keepTeamAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.KEEP_TEAM_AFTER_GAME, false);
        boolean teleportAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.TELEPORT_AFTER_GAME, false);
        boolean teleportWinnerAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.TELEPORT_WINNER_AFTER_GAME, false);
        int winnerFireworkId = JsonUtils.getJsonInt(jsonObject, GameEntryTag.WINNER_FIREWORK_ID, 0);
        int winnerParticleId = JsonUtils.getJsonInt(jsonObject, GameEntryTag.WINNER_PARTICLE_ID, 0);

        int messageCleanFreq = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MESSAGE_CLEAN_FREQUENCY, 20 * 7);
        int messageExpireTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MESSAGE_EXPIRE_TIME, 20 * 5);
        int messageSyncFreq = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MESSAGE_FORCE_SYNC_FREQUENCY, 20 * 5);

        return new GameEntry(teamMsgExpireTimeSeconds, teamColors,
                maxInvalidTime, maxBotInvalidTime, removeInvalidTeam,
                allowRemainingBot, keepTeamAfterGame, teleportAfterGame, teleportWinnerAfterGame, winnerFireworkId, winnerParticleId,
                messageCleanFreq, messageExpireTime, messageSyncFreq);
    }

    @Override
    public void applyDefault() {
        AbstractMessageManager.setCleanFrequency(messageCleanFreq);
        AbstractMessageManager.setExpireTime(messageExpireTime);
        AbstractMessageManager.setForceSyncFrequency(messageSyncFreq);
    }
}
