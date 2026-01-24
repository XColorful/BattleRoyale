package xiao.battleroyale.common.game.zone.additional;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.render.level.IClientSimpleZoneRenderer;
import xiao.battleroyale.api.event.client.render.SpecialZoneRenderEvent;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.JsonUtils;

public class AdditionalRender extends AbstractZoneSpecial {

    protected String protocol;

    public AdditionalRender(String protocol, @NotNull JsonObject jsonTag) {
        super(jsonTag);
        this.protocol = protocol;
    }

    @Override
    public ZoneSpecialHandler getSpecialHandlerType() {
        return ZoneSpecialHandler.RENDER;
    }

    public static class AdditionalRenderTag {
        public static final String PROTOCOL = "prot";
        public static final String JSON_TAG_STRING = "jstr";
    }

    @Override
    public @NotNull CompoundTag addMessageTag(IGameZone gameZone) {
        CompoundTag tag = new CompoundTag();
        tag.putString(AdditionalRenderTag.PROTOCOL, protocol);
        tag.putString(AdditionalRenderTag.JSON_TAG_STRING, JsonUtils.toJsonString(jsonTag));
        return tag;
    }

    public static @Nullable AdditionalRender fromTag(@NotNull CompoundTag tag) {
        String protocol = tag.getString(AdditionalRenderTag.PROTOCOL);
        if (protocol.isEmpty()) return null;
        String jsonString = tag.getString(AdditionalRenderTag.JSON_TAG_STRING);
        JsonObject jsonTag = JsonUtils.fromJsonString(jsonString, JsonObject.class);
        if (jsonTag == null) {
            return null;
        }
        return new AdditionalRender(protocol, jsonTag);
    }

    @Override
    public void additionalZoneRender(IRenderLevelStageEvent event, IClientSimpleZoneRenderer clientZoneRenderer, ClientSingleZoneData zoneData) {
        if (BattleRoyale.getMcSide().isServerSide()) return;
        EventPoster.postEvent(new SpecialZoneRenderEvent(BattleRoyale.getClientGameDataManager(), protocol, jsonTag.deepCopy(), event, clientZoneRenderer, zoneData));
    }
}
