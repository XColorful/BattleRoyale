package xiao.battleroyale.config.common.game.zone.zonefunc;

import com.google.gson.JsonObject;
import xiao.battleroyale.api.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.func.ZoneFuncTag;
import xiao.battleroyale.config.common.game.zone.zonefunc.event.AirdropFuncEntry;
import xiao.battleroyale.config.common.game.zone.zonefunc.event.EventFuncEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum ZoneFuncType {
    // simple
    SAFE(ZoneFuncTag.SAFE, SafeFuncEntry::fromJson),
    UNSAFE(ZoneFuncTag.UNSAFE, UnsafeFuncEntry::fromJson),
    FIREWORK(ZoneFuncTag.FIREWORK, FireworkFuncEntry::fromJson),
    MUTEKI(ZoneFuncTag.MUTEKI, MutekiFuncEntry::fromJson),
    BOOST(ZoneFuncTag.BOOST, BoostFuncEntry::fromJson),
    PARTICLE(ZoneFuncTag.PARTICLE, ParticleFuncEntry::fromJson),
    EFFECT(ZoneFuncTag.EFFECT, EffectFuncEntry::fromJson),
    MESSAGE(ZoneFuncTag.MESSAGE, MessageFuncEntry::fromJson),
    INVENTORY(ZoneFuncTag.INVENTORY, InventoryFuncEntry::fromJson),
    NO_FUNC(ZoneFuncTag.NO_FUNC, NoFuncEntry::fromJson),
    // special
    EVENT(ZoneFuncTag.EVENT, EventFuncEntry::fromJson),
    AIRDROP(ZoneFuncTag.AIRDROP, AirdropFuncEntry::fromJson),
    DANGER(ZoneFuncTag.DANGER, DangerFuncEntry::fromJson);
    
    private final String name;
    private final Function<JsonObject, ? extends IZoneFuncEntry> deserializer;

    ZoneFuncType(String name, Function<JsonObject, ? extends IZoneFuncEntry> deserializer) {
        this.name = name;
        this.deserializer = deserializer;
    }

    public String getName() {
        return name;
    }

    public Function<JsonObject, ? extends IZoneFuncEntry> getDeserializer() {
        return deserializer;
    }

    private static final Map<String, ZoneFuncType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (ZoneFuncType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static ZoneFuncType fromName(String name) {
        return NAME_TO_TYPE.get(name);
    }
}
