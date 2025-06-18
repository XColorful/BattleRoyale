package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.UnsafeFunc;

public class UnsafeFuncEntry extends AbstractFuncEntry {

    public UnsafeFuncEntry(double damage, int moveDelay, int moveTime) {
        super(damage, moveDelay, moveTime);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.UNSAFE;
    }

    @Override
    public ZoneFuncType getZoneFuncType() {
        return ZoneFuncType.UNSAFE;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new UnsafeFunc(damage, moveDelay, moveTime);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneFuncTag.TYPE_NAME, getType());
        jsonObject.addProperty(ZoneFuncTag.DAMAGE, damage);
        jsonObject.addProperty(ZoneFuncTag.MOVE_DELAY, moveDelay);
        jsonObject.addProperty(ZoneFuncTag.MOVE_TIME, moveTime);
        return jsonObject;
    }

    public static UnsafeFuncEntry fromJson(JsonObject jsonObject) {
        double damage = jsonObject.has(ZoneFuncTag.DAMAGE) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.DAMAGE).getAsDouble() : 0;
        if (damage < 0) {
            BattleRoyale.LOGGER.info("DamageFuncEntry damage is lower than 0 ({}), defaulting to 0", damage);
            damage = 0;
        }
        int moveDelay = jsonObject.has(ZoneFuncTag.MOVE_DELAY) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_DELAY).getAsInt() : 0;
        int moveTime = jsonObject.has(ZoneFuncTag.MOVE_TIME) ? jsonObject.getAsJsonPrimitive(ZoneFuncTag.MOVE_TIME).getAsInt() : 0;
        return new UnsafeFuncEntry(damage, moveDelay, moveTime);
    }
}
