package xiao.battleroyale.config.common.game.zone.zonefunc.event;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.event.EventFunc;
import xiao.battleroyale.util.JsonUtils;

public class EventFuncEntry extends AbstractEventFuncEntry {

    public EventFuncEntry(int moveDelay, int moveTime, int funcFreq, int funcOffset, String protocol,
                          @Nullable CompoundTag tag) {
        super(moveDelay, moveTime, funcFreq, funcOffset, protocol, tag);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.EVENT;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new EventFunc(moveDelay, moveTime, tickFreq, tickOffset,
                protocol, tag);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.PROTOCOL, protocol);
        jsonObject.add(ZoneFuncTag.TAG, JsonUtils.writeTagToJson(tag));

        return jsonObject;
    }

    public static EventFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        String protocol = JsonUtils.getJsonString(jsonObject, ZoneFuncTag.PROTOCOL, "");
        CompoundTag tag = JsonUtils.getJsonTag(jsonObject, ZoneFuncTag.TAG, null);

        return new EventFuncEntry(moveDelay, moveTime, tickFreq, tickOffset,
                protocol, tag);
    }
}
