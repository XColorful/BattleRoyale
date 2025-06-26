package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.gamerule.GameEntryTag;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.util.JsonUtils;

public class GameEntry implements IGameruleEntry {

    public final int maxPlayerInvalidTime;
    public final int maxBotInvalidTime;
    public final boolean removeInvalidTeam;
    public final boolean allowRemainingBot;
    public final boolean keepTeamAfterGame;
    public final boolean teleportAfterGame;
    public final boolean teleportWinnerAfterGame;
    public final int winnerFireworkId;
    public final int winnerParticleId;

    public GameEntry() {
        this(20 * 60, 20 * 10, false, true, true, true, false, 0, 0);
    }

    public GameEntry(int maxPlayerInvalidTime, int maxBotInvalidTime, boolean removeInvalidTeam, boolean allowRemainingBot, boolean keepTeamAfterGame, boolean teleportAfterGame, boolean teleportWinnerAfterGame, int winnerFireworkId, int winnerParticleId) {
        this.maxPlayerInvalidTime = maxPlayerInvalidTime;
        this.maxBotInvalidTime = maxBotInvalidTime;
        this.removeInvalidTeam = removeInvalidTeam;
        this.allowRemainingBot = allowRemainingBot;
        this.keepTeamAfterGame = keepTeamAfterGame;
        this.teleportAfterGame = teleportAfterGame;
        this.teleportWinnerAfterGame = teleportWinnerAfterGame;
        this.winnerFireworkId = winnerFireworkId;
        this.winnerParticleId = winnerParticleId;
    }

    @Override
    public String getType() {
        return "gameEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GameEntryTag.MAX_PLAYER_INVALID_TIME, maxPlayerInvalidTime);
        jsonObject.addProperty(GameEntryTag.MAX_BOT_INVALID_TIME, maxBotInvalidTime);
        jsonObject.addProperty(GameEntryTag.REMOVE_INVALID_TEAM, removeInvalidTeam);
        jsonObject.addProperty(GameEntryTag.ALLOW_REMAINING_BOT, allowRemainingBot);
        jsonObject.addProperty(GameEntryTag.KEEP_TEAM_AFTER_GAME, keepTeamAfterGame);
        jsonObject.addProperty(GameEntryTag.TELEPORT_AFTER_GAME, teleportAfterGame);
        jsonObject.addProperty(GameEntryTag.TELEPORT_WINNER_AFTER_GAME, teleportWinnerAfterGame);
        jsonObject.addProperty(GameEntryTag.WINNER_FIREWORK_ID, winnerFireworkId);
        jsonObject.addProperty(GameEntryTag.WINNER_PARTICLE_ID, winnerParticleId);
        return jsonObject;
    }

    @NotNull
    public static GameEntry fromJson(JsonObject jsonObject) {
        int maxInvalidTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MAX_PLAYER_INVALID_TIME, 20 * 60);
        int maxBotInvalidTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MAX_BOT_INVALID_TIME, 20 * 10);
        boolean removeInvalidTeam = JsonUtils.getJsonBoolean(jsonObject, GameEntryTag.REMOVE_INVALID_TEAM, false);
        boolean allowRemainingBot = JsonUtils.getJsonBoolean(jsonObject, GameEntryTag.ALLOW_REMAINING_BOT, false);
        boolean keepTeamAfterGame = JsonUtils.getJsonBoolean(jsonObject, GameEntryTag.KEEP_TEAM_AFTER_GAME, false);
        boolean teleportAfterGame = JsonUtils.getJsonBoolean(jsonObject, GameEntryTag.TELEPORT_AFTER_GAME, false);
        boolean teleportWinnerAfterGame = JsonUtils.getJsonBoolean(jsonObject, GameEntryTag.TELEPORT_WINNER_AFTER_GAME, false);
        int winnerFireworkId = JsonUtils.getJsonInt(jsonObject, GameEntryTag.WINNER_FIREWORK_ID, 0);
        int winnerParticleId = JsonUtils.getJsonInt(jsonObject, GameEntryTag.WINNER_PARTICLE_ID, 0);

        return new GameEntry(maxInvalidTime, maxBotInvalidTime, removeInvalidTeam, allowRemainingBot, keepTeamAfterGame, teleportAfterGame, teleportWinnerAfterGame, winnerFireworkId, winnerParticleId);
    }
}
