package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.FireworkFunc;

public class FireworkFuncEntry extends AbstractFuncEntry {

    private final boolean trackPlayer;
    private final int amount;
    private final int interval;
    private final int vRange;
    private final int hRange;
    private final boolean outside;

    public FireworkFuncEntry(int moveDelay, int moveTime,
                             boolean trackPlayer, int amount, int interval, int vRange, int hRange, boolean outside) {
        super(moveDelay, moveTime);
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
    public ZoneFuncType getZoneFuncType() {
        return ZoneFuncType.FIREWORK;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new FireworkFunc(moveDelay, moveTime, trackPlayer, amount, interval, vRange, hRange, outside);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_TRACK, trackPlayer);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_AMOUNT, amount);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_INTERVAL, interval);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_V_RANGE, vRange);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_H_RANGE, hRange);
        jsonObject.addProperty(ZoneFuncTag.FIREWORK_OUTSIDE, outside);
        return jsonObject;
    }

    public static FireworkFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = jsonObject.has(ZoneFuncTag.MOVE_DELAY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_DELAY).getAsInt() : 0;
        int moveTime = jsonObject.has(ZoneFuncTag.MOVE_TIME) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_TIME).getAsInt() : 0;
        boolean trackPlayer = jsonObject.has(ZoneFuncTag.FIREWORK_TRACK) && jsonObject.getAsJsonPrimitive(ZoneFuncTag.FIREWORK_TRACK).getAsBoolean();
        int amount = jsonObject.has(ZoneFuncTag.FIREWORK_AMOUNT) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.FIREWORK_AMOUNT).getAsInt() : 3;
        int interval = jsonObject.has(ZoneFuncTag.FIREWORK_INTERVAL) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.FIREWORK_INTERVAL).getAsInt() : 20;
        int vRange = jsonObject.has(ZoneFuncTag.FIREWORK_V_RANGE) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.FIREWORK_V_RANGE).getAsInt() : 5;
        int hRange = jsonObject.has(ZoneFuncTag.FIREWORK_H_RANGE) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.FIREWORK_H_RANGE).getAsInt() : 3;
        boolean outside = jsonObject.has(ZoneFuncTag.FIREWORK_OUTSIDE) && jsonObject.getAsJsonPrimitive(ZoneFuncTag.FIREWORK_OUTSIDE).getAsBoolean();

        return new FireworkFuncEntry(moveDelay, moveTime, trackPlayer, amount, interval, vRange, hRange, outside);
    }
}
