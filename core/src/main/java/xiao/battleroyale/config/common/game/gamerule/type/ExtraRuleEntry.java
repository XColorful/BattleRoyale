package xiao.battleroyale.config.common.game.gamerule.type;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.common.game.gamerule.ExtraRuleTag;
import xiao.battleroyale.api.config.common.game.gamerule.IGameruleEntry;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class ExtraRuleEntry implements IGameruleEntry {

    public StringUtils.ProtocolString protocol;
    public @NotNull JsonObject jsonTag;

    public ExtraRuleEntry() {
        this("", null);
    }
    public ExtraRuleEntry(String protocol, @Nullable JsonObject jsonTag) {
        this.protocol = new StringUtils.ProtocolString(protocol);
        this.jsonTag = jsonTag != null ? jsonTag : new JsonObject();
    }
    @Override public @NotNull ExtraRuleEntry copy() {
        return new ExtraRuleEntry(protocol.raw, jsonTag.deepCopy());
    }

    @Override
    public String getType() {
        return "extraRuleEntry";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ExtraRuleTag.PROTOCOL, protocol.raw);
        jsonObject.add(ExtraRuleTag.JSON_TAG, jsonTag);

        return jsonObject;
    }

    @NotNull
    public static ExtraRuleEntry fromJson(@NotNull JsonObject jsonObject) {
        String protocol = JsonUtils.getJsonString(jsonObject, ExtraRuleTag.PROTOCOL, "");
        JsonObject jsonTag = JsonUtils.getJsonObject(jsonObject, ExtraRuleTag.JSON_TAG, null);

        return new ExtraRuleEntry(protocol, jsonTag);
    }
}
