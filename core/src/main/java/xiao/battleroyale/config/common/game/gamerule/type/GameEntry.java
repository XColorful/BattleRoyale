package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.config.sub.IConfigAppliable;
import xiao.battleroyale.api.game.gamerule.GameEntryTag;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.compat.tacz.Tacz;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameEntry implements IGameruleEntry, IConfigAppliable {

    public boolean teleportWhenInitGame;
    public int teamMsgExpireTimeSeconds;
    public List<String> teamColors;
    public static final List<String> DEFAULT_TEAM_COLORS = Arrays.asList(
            // 彩色（深），红蓝开头
            ColorUtils.parseIntToStringRGB(ChatFormatting.DARK_RED.getColor()), // §4
            ColorUtils.parseIntToStringRGB(ChatFormatting.DARK_BLUE.getColor()), // §1
            ColorUtils.parseIntToStringRGB(ChatFormatting.GOLD.getColor()), // §6
            ColorUtils.parseIntToStringRGB(ChatFormatting.DARK_PURPLE.getColor()), // §5
            ColorUtils.parseIntToStringRGB(ChatFormatting.DARK_GREEN.getColor()), // §2
            ColorUtils.parseIntToStringRGB(ChatFormatting.DARK_AQUA.getColor()), // §3
            // 彩色（浅），红蓝开头
            ColorUtils.parseIntToStringRGB(ChatFormatting.RED.getColor()), // §12
            ColorUtils.parseIntToStringRGB(ChatFormatting.BLUE.getColor()), // §9
            ColorUtils.parseIntToStringRGB(ChatFormatting.YELLOW.getColor()), // §14
            ColorUtils.parseIntToStringRGB(ChatFormatting.LIGHT_PURPLE.getColor()), // §13
            ColorUtils.parseIntToStringRGB(ChatFormatting.GREEN.getColor()), // §10
            ColorUtils.parseIntToStringRGB(ChatFormatting.AQUA.getColor()), // §11
            // 黑白
            ColorUtils.parseIntToStringRGB(ChatFormatting.BLACK.getColor()), // §0
            ColorUtils.parseIntToStringRGB(ChatFormatting.GRAY.getColor()), // §7
            // 黑白
            ColorUtils.parseIntToStringRGB(ChatFormatting.DARK_GRAY.getColor()), // §8
            ColorUtils.parseIntToStringRGB(ChatFormatting.WHITE.getColor()) // §15
    );
    public static final List<Float> DEFAULT_DOWN_DAMAGE = Arrays.asList(0.3333F, 0.4444F, 0.6667F, 1.3333F, 2F, 4F, 8F, 16F, 32F);
    public boolean buildVanillaTeam;
    public boolean hideVanillaTeamName;

    public int maxPlayerInvalidTime;
    public int maxBotInvalidTime;
    public boolean removeInvalidTeam;

    public boolean healAllAtStart;
    public boolean friendlyFire;
    public boolean downFire;
    public List<Float> downDamageList;
    public int downDamageFrequency;

    public boolean downShoot;
    public boolean downReload;
    public boolean downFireSelect;
    public boolean downMelee;

    public boolean onlyGamePlayerSpectate;
    public boolean spectateAfterTeam;
    public boolean spectatorSeeAllTeams;
    public boolean teleportInterfererToLobby;
    public boolean forceEliminationTeleportToLobby;

    public boolean allowRemainingBot;
    public boolean keepTeamAfterGame;
    public boolean teleportAfterGame;
    public boolean teleportWinnerAfterGame;
    public int winnerFireworkId;
    public int winnerParticleId;
    public boolean initGameAfterGame;

    public int messageCleanFreq;
    public int messageExpireTime;
    public int messageSyncFreq;

    public GameEntry() {
        this(true, 300, DEFAULT_TEAM_COLORS, true, true,
                20 * 60, 20 * 10, false,
                true, false, false, DEFAULT_DOWN_DAMAGE, 20,
                false, false, false, false,
                false, true, true, true, true,
                true, true, true, false, 0, 0, false,
                20 * 7, 20 * 5, 20 * 5);
    }
    public GameEntry(boolean teleportWhenInitGame, int teamMsgExpireTimeSeconds, List<String> teamColors, boolean buildVanillaTeam, boolean hideVanillaTeamName,
                     int maxPlayerInvalidTime, int maxBotInvalidTime, boolean removeInvalidTeam,
                     boolean healAllAtStart, boolean friendlyFire, boolean downFire, List<Float> downDamageList, int downDamageFrequency,
                     boolean downShoot, boolean downReload, boolean downFireSelect, boolean downMelee,
                     boolean onlyGamePlayerSpectate, boolean spectateAfterTeam, boolean spectatorSeeAllTeams, boolean teleportInterfererToLobby, boolean forceEliminationTeleportToLobby,
                     boolean allowRemainingBot, boolean keepTeamAfterGame, boolean teleportAfterGame, boolean teleportWinnerAfterGame, int winnerFireworkId, int winnerParticleId, boolean initGameAfterGame,
                     int messageCleanFreq, int messageExpireTime, int messageSyncFreq) {
        this.teleportWhenInitGame = teleportWhenInitGame;
        this.teamMsgExpireTimeSeconds = teamMsgExpireTimeSeconds;
        this.teamColors = teamColors;
        this.buildVanillaTeam = buildVanillaTeam;
        this.hideVanillaTeamName = hideVanillaTeamName;
        this.maxPlayerInvalidTime = maxPlayerInvalidTime;
        this.maxBotInvalidTime = maxBotInvalidTime;
        this.removeInvalidTeam = removeInvalidTeam;
        this.healAllAtStart = healAllAtStart;
        this.friendlyFire = friendlyFire;
        this.downFire = downFire;
        this.downDamageList = downDamageList;
        this.downDamageFrequency = downDamageFrequency;
        this.downShoot = downShoot;
        this.downReload = downReload;
        this.downFireSelect = downFireSelect;
        this.downMelee = downMelee;
        this.onlyGamePlayerSpectate = onlyGamePlayerSpectate;
        this.spectateAfterTeam = spectateAfterTeam;
        this.spectatorSeeAllTeams = spectatorSeeAllTeams;
        this.teleportInterfererToLobby = teleportInterfererToLobby;
        this.forceEliminationTeleportToLobby = forceEliminationTeleportToLobby;
        this.allowRemainingBot = allowRemainingBot;
        this.keepTeamAfterGame = keepTeamAfterGame;
        this.teleportAfterGame = teleportAfterGame;
        this.teleportWinnerAfterGame = teleportWinnerAfterGame;
        this.winnerFireworkId = winnerFireworkId;
        this.winnerParticleId = winnerParticleId;
        this.initGameAfterGame = initGameAfterGame;
        this.messageCleanFreq = messageCleanFreq;
        this.messageExpireTime = messageExpireTime;
        this.messageSyncFreq = messageSyncFreq;
    }
    @Override public @NotNull GameEntry copy() {
        return new GameEntry(teleportWhenInitGame, teamMsgExpireTimeSeconds, new ArrayList<>(teamColors), buildVanillaTeam, hideVanillaTeamName,
                maxPlayerInvalidTime, maxBotInvalidTime, removeInvalidTeam,
                healAllAtStart, friendlyFire, downFire, new ArrayList<>(downDamageList), downDamageFrequency,
                downShoot, downReload, downFireSelect, downMelee,
                onlyGamePlayerSpectate, spectateAfterTeam, spectatorSeeAllTeams, teleportInterfererToLobby, forceEliminationTeleportToLobby,
                allowRemainingBot, keepTeamAfterGame, teleportAfterGame, teleportWinnerAfterGame, winnerFireworkId, winnerParticleId, initGameAfterGame,
                messageCleanFreq, messageExpireTime, messageSyncFreq);
    }

    @Override
    public String getType() {
        return "gameEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(GameEntryTag.TELEPORT_WHEN_INIT_GAME, teleportWhenInitGame);
        jsonObject.addProperty(GameEntryTag.TEAM_MSG_EXPIRE_SECONDS, teamMsgExpireTimeSeconds);
        jsonObject.add(GameEntryTag.TEAM_COLORS, JsonUtils.writeStringListToJson(teamColors));
        jsonObject.addProperty(GameEntryTag.BUILD_VANILLA_TEAM, buildVanillaTeam);
        jsonObject.addProperty(GameEntryTag.HIDE_VANILLA_TEAM_NAME, hideVanillaTeamName);

        jsonObject.addProperty(GameEntryTag.MAX_PLAYER_INVALID_TIME, maxPlayerInvalidTime);
        jsonObject.addProperty(GameEntryTag.MAX_BOT_INVALID_TIME, maxBotInvalidTime);
        jsonObject.addProperty(GameEntryTag.REMOVE_INVALID_TEAM, removeInvalidTeam);

        jsonObject.addProperty(GameEntryTag.HEAL_ALL_AT_START, healAllAtStart);
        jsonObject.addProperty(GameEntryTag.FRIENDLY_FIRE, friendlyFire);
        jsonObject.addProperty(GameEntryTag.DOWN_FIRE, downFire);
        jsonObject.add(GameEntryTag.DOWN_DAMAGE_LIST, JsonUtils.writeFloatListToJson(downDamageList));
        jsonObject.addProperty(GameEntryTag.DOWN_DAMAGE_FREQUENCY, downDamageFrequency);

        jsonObject.addProperty(GameEntryTag.DOWN_SHOOT, downShoot);
        jsonObject.addProperty(GameEntryTag.DOWN_RELOAD, downReload);
        jsonObject.addProperty(GameEntryTag.DOWN_FIRE_SELECT, downFireSelect);
        jsonObject.addProperty(GameEntryTag.DOWN_MELEE, downMelee);

        jsonObject.addProperty(GameEntryTag.ONLY_GAME_PLAYER_SPECTATE, onlyGamePlayerSpectate);
        jsonObject.addProperty(GameEntryTag.SPECTATE_AFTER_TEAM, spectateAfterTeam);
        jsonObject.addProperty(GameEntryTag.SPECTATOR_SEE_ALL_TEAMS, spectatorSeeAllTeams);
        jsonObject.addProperty(GameEntryTag.TELEPORT_INTERFERER_TO_LOBBY, teleportInterfererToLobby);
        jsonObject.addProperty(GameEntryTag.FORCE_ELIMINATION_TELEPORT_TO_LOBBY, forceEliminationTeleportToLobby);

        jsonObject.addProperty(GameEntryTag.ALLOW_REMAINING_BOT, allowRemainingBot);
        jsonObject.addProperty(GameEntryTag.KEEP_TEAM_AFTER_GAME, keepTeamAfterGame);
        jsonObject.addProperty(GameEntryTag.TELEPORT_AFTER_GAME, teleportAfterGame);
        jsonObject.addProperty(GameEntryTag.TELEPORT_WINNER_AFTER_GAME, teleportWinnerAfterGame);
        jsonObject.addProperty(GameEntryTag.WINNER_FIREWORK_ID, winnerFireworkId);
        jsonObject.addProperty(GameEntryTag.WINNER_PARTICLE_ID, winnerParticleId);
        jsonObject.addProperty(GameEntryTag.INIT_GAME_AFTER_GAME, initGameAfterGame);

        jsonObject.addProperty(GameEntryTag.MESSAGE_CLEAN_FREQUENCY, messageCleanFreq);
        jsonObject.addProperty(GameEntryTag.MESSAGE_EXPIRE_TIME, messageExpireTime);
        jsonObject.addProperty(GameEntryTag.MESSAGE_FORCE_SYNC_FREQUENCY, messageSyncFreq);
        return jsonObject;
    }

    @NotNull
    public static GameEntry fromJson(JsonObject jsonObject) {
        boolean teleportWhenInitGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.TELEPORT_WHEN_INIT_GAME, true);
        int teamMsgExpireTimeSeconds = JsonUtils.getJsonInt(jsonObject, GameEntryTag.TEAM_MSG_EXPIRE_SECONDS, 300);
        List<String> teamColors = JsonUtils.getJsonStringList(jsonObject, GameEntryTag.TEAM_COLORS);
        boolean buildVanillaTeam = JsonUtils.getJsonBool(jsonObject, GameEntryTag.BUILD_VANILLA_TEAM, true);
        boolean hideVanillaTeamName = JsonUtils.getJsonBool(jsonObject, GameEntryTag.HIDE_VANILLA_TEAM_NAME, true);

        int maxPlayerInvalidTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MAX_PLAYER_INVALID_TIME, 20 * 60);
        int maxBotInvalidTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MAX_BOT_INVALID_TIME, 20 * 10);
        boolean removeInvalidTeam = JsonUtils.getJsonBool(jsonObject, GameEntryTag.REMOVE_INVALID_TEAM, false);

        boolean healAllAtStart = JsonUtils.getJsonBool(jsonObject, GameEntryTag.HEAL_ALL_AT_START, true);
        boolean friendlyFire = JsonUtils.getJsonBool(jsonObject, GameEntryTag.FRIENDLY_FIRE, false);
        boolean downFire = JsonUtils.getJsonBool(jsonObject, GameEntryTag.DOWN_FIRE, false);
        List<Float> downDamageList = JsonUtils.getJsonFloatList(jsonObject, GameEntryTag.DOWN_DAMAGE_LIST);
        int downDamageFrequency = JsonUtils.getJsonInt(jsonObject, GameEntryTag.DOWN_DAMAGE_FREQUENCY, 20);

        boolean downShoot = JsonUtils.getJsonBool(jsonObject, GameEntryTag.DOWN_SHOOT, false);
        boolean downReload = JsonUtils.getJsonBool(jsonObject, GameEntryTag.DOWN_RELOAD, false);
        boolean downFireSelect = JsonUtils.getJsonBool(jsonObject, GameEntryTag.DOWN_FIRE_SELECT, false);
        boolean downMelee = JsonUtils.getJsonBool(jsonObject, GameEntryTag.DOWN_MELEE, false);

        boolean onlyGamePlayerSpectate = JsonUtils.getJsonBool(jsonObject, GameEntryTag.ONLY_GAME_PLAYER_SPECTATE, false);
        boolean spectateAfterTeam = JsonUtils.getJsonBool(jsonObject, GameEntryTag.SPECTATE_AFTER_TEAM, true);
        boolean spectatorSeeAllTeams = JsonUtils.getJsonBool(jsonObject, GameEntryTag.SPECTATOR_SEE_ALL_TEAMS, true);
        boolean teleportInterfererToLobby = JsonUtils.getJsonBool(jsonObject, GameEntryTag.TELEPORT_INTERFERER_TO_LOBBY, true);
        boolean forceEliminationTeleportToLobby = JsonUtils.getJsonBool(jsonObject, GameEntryTag.FORCE_ELIMINATION_TELEPORT_TO_LOBBY, true);

        boolean allowRemainingBot = JsonUtils.getJsonBool(jsonObject, GameEntryTag.ALLOW_REMAINING_BOT, false);
        boolean keepTeamAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.KEEP_TEAM_AFTER_GAME, false);
        boolean teleportAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.TELEPORT_AFTER_GAME, false);
        boolean teleportWinnerAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.TELEPORT_WINNER_AFTER_GAME, false);
        int winnerFireworkId = JsonUtils.getJsonInt(jsonObject, GameEntryTag.WINNER_FIREWORK_ID, 0);
        int winnerParticleId = JsonUtils.getJsonInt(jsonObject, GameEntryTag.WINNER_PARTICLE_ID, 0);
        boolean initGameAfterGame = JsonUtils.getJsonBool(jsonObject, GameEntryTag.INIT_GAME_AFTER_GAME, false);

        int messageCleanFreq = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MESSAGE_CLEAN_FREQUENCY, 20 * 7);
        int messageExpireTime = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MESSAGE_EXPIRE_TIME, 20 * 5);
        int messageSyncFreq = JsonUtils.getJsonInt(jsonObject, GameEntryTag.MESSAGE_FORCE_SYNC_FREQUENCY, 20 * 5);

        return new GameEntry(teleportWhenInitGame, teamMsgExpireTimeSeconds, teamColors, buildVanillaTeam, hideVanillaTeamName,
                maxPlayerInvalidTime, maxBotInvalidTime, removeInvalidTeam,
                healAllAtStart, friendlyFire, downFire, downDamageList, downDamageFrequency,
                downShoot, downReload, downFireSelect, downMelee,
                onlyGamePlayerSpectate, spectateAfterTeam, spectatorSeeAllTeams, teleportInterfererToLobby, forceEliminationTeleportToLobby,
                allowRemainingBot, keepTeamAfterGame, teleportAfterGame, teleportWinnerAfterGame, winnerFireworkId, winnerParticleId, initGameAfterGame,
                messageCleanFreq, messageExpireTime, messageSyncFreq);
    }

    @Override
    public void applyDefault() {
        AbstractMessageManager.setCleanFrequency(messageCleanFreq);
        AbstractMessageManager.setExpireTime(messageExpireTime);
        AbstractMessageManager.setForceSyncFrequency(messageSyncFreq);
        Tacz.setGameConfig(downShoot, downReload, downFireSelect, downMelee);
    }
}
