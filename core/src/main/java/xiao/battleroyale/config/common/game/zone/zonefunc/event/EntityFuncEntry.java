package xiao.battleroyale.config.common.game.zone.zonefunc.event;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.zone.tickable.event.EntityFunc;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.NBTUtils;

public class EntityFuncEntry extends AbstractEventFuncEntry {

    public int lootId;
    public @Nullable String nbtString;
    public @NotNull CompoundTag nbt;

    public EntityFuncEntry(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                           String protocol, @Nullable CompoundTag tag,
                           int lootId, @Nullable String nbtString) {
        super(moveDelay, moveTime, tickFreq, tickOffset, protocol, tag);
        this.lootId = lootId;
        this.nbtString = nbtString;
        this.nbt = NBTUtils.stringToNBT(nbtString);
    }
    @Override public @NotNull EntityFuncEntry copy() {
        return new EntityFuncEntry(moveDelay, moveTime, tickFreq, tickOffset,
                protocol, tag.copy(),
                lootId, nbtString);
    }

    @Override
    public String getType() {
        return ZoneFuncTag.ENTITY;
    }

    @Override
    public ITickableZone createTickableZone() {
        return new EntityFunc(moveDelay, moveTime, tickFreq, tickOffset,
                protocol, tag.copy(),
                lootId, nbt);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        jsonObject.addProperty(ZoneFuncTag.PROTOCOL, protocol);
        jsonObject.add(ZoneFuncTag.TAG, JsonUtils.writeTagToJson(tag));

        jsonObject.addProperty(ZoneFuncTag.LOOT_ID, lootId);
        jsonObject.addProperty(ZoneFuncTag.NBT, nbtString);

        return jsonObject;
    }

    public static EntityFuncEntry fromJson(JsonObject jsonObject) {
        int moveDelay = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_DELAY, 0);
        int moveTime = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.MOVE_TIME, 0);
        int tickFreq = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_FREQUENCY, 20);
        int tickOffset = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.TICK_OFFSET, -1);

        String protocol = JsonUtils.getJsonString(jsonObject, ZoneFuncTag.PROTOCOL, "");
        CompoundTag tag = JsonUtils.getJsonTag(jsonObject, ZoneFuncTag.TAG, null);

        int lootId = JsonUtils.getJsonInt(jsonObject, ZoneFuncTag.LOOT_ID, 0);
        String nbtString = JsonUtils.getJsonString(jsonObject, ZoneFuncTag.NBT, "");

        return new EntityFuncEntry(moveDelay, moveTime, tickFreq, tickOffset,
                protocol, tag,
                lootId, nbtString);
    }
}
