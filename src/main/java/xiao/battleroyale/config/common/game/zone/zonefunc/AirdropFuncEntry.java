package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.AirdropFunc;
import xiao.battleroyale.util.JsonUtils;

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
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        return new AirdropFuncEntry(moveDelay, moveTime, tickFreq, tickOffset);
    }
}
