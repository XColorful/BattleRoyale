package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.MutekiFunc;
import xiao.battleroyale.util.JsonUtils;

public class MutekiFuncEntry extends AbstractFuncEntry {

    public int mutekiTime;

    public MutekiFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset, int mutekiTime) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.mutekiTime = Math.max(mutekiTime, 0);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.MUTEKI;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new MutekiFunc(moveDelay, moveTime, tickFreq, tickOffset, mutekiTime);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.MUTEKI_TIME, mutekiTime);

        return jsonObject;
    }

    public static MutekiFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        int mutekiTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MUTEKI_TIME, 0);

        return new MutekiFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, mutekiTime);
    }
}
