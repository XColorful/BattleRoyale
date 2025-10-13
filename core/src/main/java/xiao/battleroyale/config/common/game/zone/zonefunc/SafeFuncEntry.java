package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.SafeFunc;
import xiao.battleroyale.util.JsonUtils;

public class SafeFuncEntry extends AbstractFuncEntry {

    public float damage;

    public SafeFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset, float damage) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.damage = damage;
    }
    @Override public @NotNull SafeFuncEntry copy() {
        return new SafeFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, damage);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.SAFE;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new SafeFunc(moveDelay, moveTime, tickFreq, tickOffset, damage);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.DAMAGE, damage);

        return jsonObject;
    }

    public static SafeFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        double damage = JsonUtils.getJsonDouble(jsonObject, ZoneFuncTag.DAMAGE, 0);

        return new SafeFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, (float) damage);
    }
}
