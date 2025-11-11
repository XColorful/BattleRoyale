package xiao.battleroyale.common.game.zone.additional;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.config.common.game.zone.zonespecial.ZoneSpecialType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum ZoneSpecialHandler {
    // default
    @ApiStatus.Internal NULL(ZoneSpecialType.NULL, null),
    // client
    RENDER(ZoneSpecialType.RENDER, AdditionalRender::fromTag);

    private final ZoneSpecialType specialType;
    private final Function<@NotNull CompoundTag, ? extends AbstractZoneSpecial> builder;


    ZoneSpecialHandler(ZoneSpecialType specialType, Function<@NotNull CompoundTag, ? extends AbstractZoneSpecial> builder) {
        this.specialType = specialType;
        this.builder = builder;
    }

    public ZoneSpecialType getSpecialType() {
        return specialType;
    }

    public Function<@NotNull CompoundTag, ? extends AbstractZoneSpecial> getBuilder() {
        return builder;
    }

    private static final Map<ZoneSpecialType, ZoneSpecialHandler> TYPE_TO_HANDLER = new HashMap<>();

    static {
        for (ZoneSpecialHandler handler : values()) {
            TYPE_TO_HANDLER.put(handler.specialType, handler);
        }
    }

    public static ZoneSpecialHandler fromSpecialType(ZoneSpecialType specialType) {
        return TYPE_TO_HANDLER.get(specialType);
    }
}
