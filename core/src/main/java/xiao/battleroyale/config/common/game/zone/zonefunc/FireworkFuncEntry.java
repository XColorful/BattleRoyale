package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.FireworkFunc;
import xiao.battleroyale.util.JsonUtils;

public class FireworkFuncEntry extends AbstractFuncEntry {

    public boolean trackPlayer;
    public int amount;
    public int interval;
    public int vRange;
    public int hRange;
    public boolean outside;

    public FireworkFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                             boolean trackPlayer, int amount, int interval, int vRange, int hRange, boolean outside) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.trackPlayer = trackPlayer;
        this.amount = Math.max(amount, 1);
        this.interval = Math.max(interval, 0);
        this.vRange = Math.max(vRange, 0);
        this.hRange = Math.max(hRange, 0);
        this.outside = outside;
    }

    @Override
    public String getType() {
        return ZoneFuncTag.FIREWORK;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new FireworkFunc(moveDelay, moveTime, tickFreq, tickOffset,
                trackPlayer, amount, interval, vRange, hRange, outside);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.FIREWORK_TRACK, trackPlayer);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_AMOUNT, amount);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_INTERVAL, interval);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_V_RANGE, vRange);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_H_RANGE, hRange);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_OUTSIDE, outside);

        return jsonObject;
    }

    public static FireworkFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        boolean trackPlayer = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.FIREWORK_TRACK, false);
        int amount = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FIREWORK_AMOUNT, 3);
        int interval = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FIREWORK_INTERVAL, 20);
        int vRange = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FIREWORK_V_RANGE, 5);
        int hRange = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.FIREWORK_H_RANGE, 3);
        boolean outside = JsonUtils.getJsonBool(jsonObject, ZoneFuncTag.FIREWORK_OUTSIDE, false);

        return new FireworkFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, trackPlayer, amount, interval, vRange, hRange, outside);
    }
}
