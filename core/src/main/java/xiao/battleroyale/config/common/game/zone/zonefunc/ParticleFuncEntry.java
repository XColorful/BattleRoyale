package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.ParticleFunc;
import xiao.battleroyale.util.JsonUtils;

import java.util.List;

public class ParticleFuncEntry extends AbstractFuncEntry {

    public final List<Integer> particleIdList;
    public int select;
    public String channel;
    public int cooldown;

    public ParticleFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                             List<Integer> particleIdList, int select, String channel, int cooldown) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.particleIdList = particleIdList;
        this.select = Math.max(select, 0);
        this.channel = channel;
        this.cooldown = cooldown;
    }

    @Override
    public String getType() {
        return ZoneFuncTag.PARTICLE;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new ParticleFunc(moveDelay, moveTime, tickFreq, tickOffset, particleIdList, select, channel, cooldown);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.add(ZoneFuncTag.PARTICLES, JsonUtils.writeIntListToJson(particleIdList));
        jsonObject.addProperty(ZoneFuncTag.SELECT_COUNT, select);
        jsonObject.addProperty(ZoneFuncTag.CHANNEL, channel);
        jsonObject.addProperty(ZoneFuncTag.COOLDOWN, cooldown);

        return jsonObject;
    }

    public static ParticleFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        List<Integer> particleIdList = JsonUtils.getJsonIntList(jsonObject, ZoneFuncTag.PARTICLES);
        int select = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.SELECT_COUNT, 0);
        String channel = JsonUtils.getJsonString(jsonObject, ZoneFuncTag.CHANNEL, "");
        int cooldown = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.COOLDOWN, 20);

        return new ParticleFuncEntry(moveDelay, moveTime, tickFreq, tickOffset, particleIdList, select, channel, cooldown);
    }
}
