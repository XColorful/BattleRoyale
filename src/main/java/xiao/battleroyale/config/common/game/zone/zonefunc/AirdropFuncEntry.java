package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.AirdropFunc;

public class AirdropFuncEntry extends AbstractFuncEntry {

    public AirdropFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.AIRDROP;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new AirdropFunc(moveDelay, moveTime, tickFreq, tickOffset);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.TICK_FREQUENCY, tickFreq);
        jsonObject.addProperty(ZoneFuncTag.TICK_OFFSET, tickOffset);

        return jsonObject;
    }

    public static AirdropFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = jsonObject.has(ZoneFuncTag.MOVE_DELAY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_DELAY).getAsInt() : 0;
        int moveTime = jsonObject.has(ZoneFuncTag.MOVE_TIME) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.TYPE_NAME).getAsInt() : 0;
        int tickFreq = jsonObject.has(ZoneFuncTag.TICK_FREQUENCY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.TICK_FREQUENCY).getAsInt() : 20;
        int tickOffset = jsonObject.has(ZoneFuncTag.TICK_OFFSET) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.TICK_OFFSET).getAsInt() : -1;

        return new AirdropFuncEntry(moveDelay, moveTime, tickFreq, tickOffset);
    }
}
