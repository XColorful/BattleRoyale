package xiao.battleroyale.config.common.game.zone.zonespecial;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IAdditionalZone;
import xiao.battleroyale.api.game.zone.special.ZoneSpecialTag;
import xiao.battleroyale.common.game.zone.additional.AdditionalRender;
import xiao.battleroyale.util.JsonUtils;

public class SpecialRenderEntry extends AbstractSpecialEntry {

    public String protocol;
    public @NotNull JsonObject jsonTag;

    public SpecialRenderEntry(String protocol, @Nullable JsonObject jsonTag) {
        super();
        this.protocol = protocol;
        this.jsonTag = jsonTag != null ? jsonTag : new JsonObject();
    }
    @Override public @NotNull SpecialRenderEntry copy() {
        return new SpecialRenderEntry(protocol, jsonTag.deepCopy());
    }

    @Override
    public String getType() {
        return ZoneSpecialTag.RENDER;
    }

    @Override
    public ZoneSpecialType getZoneSpecialType() {
        return ZoneSpecialType.RENDER;
    }

    @Override
    public IAdditionalZone createAdditionalZone() {
        return new AdditionalRender(protocol, jsonTag);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(ZoneSpecialTag.PROTOCOL, protocol);
        jsonObject.add(ZoneSpecialTag.JSON_TAG, jsonTag);
        return jsonObject;
    }

    public static SpecialRenderEntry fromJson(JsonObject jsonObject) {
        String protocol = JsonUtils.getJsonString(jsonObject, ZoneSpecialTag.PROTOCOL, "");
        JsonObject jsonTag = JsonUtils.getJsonObject(jsonObject, ZoneSpecialTag.JSON_TAG, null);
        return new SpecialRenderEntry(protocol, jsonTag);
    }
}
