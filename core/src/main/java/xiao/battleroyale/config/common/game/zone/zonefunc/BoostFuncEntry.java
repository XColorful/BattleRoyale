package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.BoostFunc;
import xiao.battleroyale.util.JsonUtils;

public class BoostFuncEntry extends AbstractFuncEntry {

    public int boost;

    public BoostFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset, int boost) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.boost = Math.max(boost, 0);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.BOOST;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new BoostFunc(moveDelay, moveTime, tickFreq, tickOffset, boost);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.BOOST, boost);

        return jsonObject;
    }

    public static BoostFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        int boost = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.BOOST, 40);

        return new BoostFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, boost);
    }
}
