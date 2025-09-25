package xiao.battleroyale.config.common.server.utility.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigAppliable;
import xiao.battleroyale.api.server.utility.IUtilityEntry;
import xiao.battleroyale.api.server.utility.SurvivalEntryTag;
import xiao.battleroyale.common.server.utility.SurvivalLobby;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class SurvivalEntry implements IUtilityEntry, IConfigAppliable {

    // survivalLobby
    public final String levelKey;
    public final boolean allowGamePlayerTeleport;
    public final Vec3 lobbyCenter;
    public final Vec3 lobbyDimension;
    public final boolean lobbyMuteki;
    public final boolean lobbyHeal;
    public final boolean dropInventory;
    public final boolean dropGameItemOnly;
    public final boolean clearInventory;
    public final boolean clearGameItemOnly;

    public SurvivalEntry(String levelKey, boolean allowGamePlayerTeleport, Vec3 lobbyCenter, Vec3 lobbyDimension, boolean lobbyMuteki, boolean lobbyHeal,
                         boolean dropInventory, boolean dropGameItemOnly, boolean clearInventory, boolean clearGameItemOnly) {
        this.levelKey = levelKey;
        this.allowGamePlayerTeleport = allowGamePlayerTeleport;
        this.lobbyCenter = lobbyCenter;
        this.lobbyDimension = lobbyDimension;
        this.lobbyMuteki = lobbyMuteki;
        this.lobbyHeal = lobbyHeal;
        this.dropInventory = dropInventory;
        this.dropGameItemOnly = dropGameItemOnly;
        this.clearInventory = clearInventory;
        this.clearGameItemOnly = clearGameItemOnly;
    }

    @Override
    public String getType() {
        return "SurvivalEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(SurvivalEntryTag.SURVIVAL_LOBBY, generateSurvivalLobbyJson());
        return jsonObject;
    }

    @Nullable
    public static SurvivalEntry fromJson(JsonObject jsonObject) {
        JsonObject survivalLobbyObject = JsonUtils.getJsonObject(jsonObject, SurvivalEntryTag.SURVIVAL_LOBBY, null);
        if (survivalLobbyObject == null) {
            return null;
        }
        // survivalLobby
        boolean allowGamePlayerTeleport = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.ALLOW_GAME_PLAYER_TELEPORT, false);
        String levelDimension = JsonUtils.getJsonString(survivalLobbyObject, SurvivalEntryTag.LEVEL_KEY, "");
        Vec3 lobbyCenter = JsonUtils.getJsonVec(survivalLobbyObject, SurvivalEntryTag.LOBBY_CENTER, null);
        Vec3 lobbyDimension = JsonUtils.getJsonVec(survivalLobbyObject, SurvivalEntryTag.LOBBY_DIMENSION, null);
        if (lobbyCenter == null || lobbyDimension == null) {
            BattleRoyale.LOGGER.info("Invalid lobbyCenter or lobbyDimension for survivalEntry, skipped");
            return null;
        }
        boolean lobbyMuteki = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.LOBBY_MUTEKI, false);
        boolean lobbyHeal = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.LOBBY_HEAL, false);
        boolean dropInventory = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.DROP_INVENTORY, true);
        boolean dropGameItemOnly = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.DROP_GAME_ITEM_ONLY, true);
        boolean clearInventory = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.CLEAR_INVENTORY, true);
        boolean clearGameItemOnly = JsonUtils.getJsonBool(survivalLobbyObject, SurvivalEntryTag.CLEAR_GAME_ITEM_ONLY, true);

        return new SurvivalEntry(levelDimension, allowGamePlayerTeleport, lobbyCenter, lobbyDimension, lobbyMuteki, lobbyHeal,
                dropInventory, dropGameItemOnly, clearInventory, clearGameItemOnly);
    }

    @Override
    public void applyDefault() {
        SurvivalLobby.get().setLobby(levelKey, allowGamePlayerTeleport,
                lobbyCenter, lobbyDimension, lobbyMuteki, lobbyHeal,
                dropInventory, dropGameItemOnly, clearInventory, clearGameItemOnly);
    }

    @NotNull
    private JsonObject generateSurvivalLobbyJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SurvivalEntryTag.LEVEL_KEY, levelKey);
        jsonObject.addProperty(SurvivalEntryTag.ALLOW_GAME_PLAYER_TELEPORT, allowGamePlayerTeleport);
        jsonObject.addProperty(SurvivalEntryTag.LOBBY_CENTER, StringUtils.vectorToString(lobbyCenter));
        jsonObject.addProperty(SurvivalEntryTag.LOBBY_DIMENSION, StringUtils.vectorToString(lobbyDimension));
        jsonObject.addProperty(SurvivalEntryTag.LOBBY_MUTEKI, lobbyMuteki);
        jsonObject.addProperty(SurvivalEntryTag.LOBBY_HEAL, lobbyHeal);
        jsonObject.addProperty(SurvivalEntryTag.DROP_INVENTORY, dropInventory);
        jsonObject.addProperty(SurvivalEntryTag.DROP_GAME_ITEM_ONLY, dropGameItemOnly);
        jsonObject.addProperty(SurvivalEntryTag.CLEAR_INVENTORY, clearInventory);
        jsonObject.addProperty(SurvivalEntryTag.CLEAR_GAME_ITEM_ONLY, clearGameItemOnly);
        return jsonObject;
    }
}
