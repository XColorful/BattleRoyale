package xiao.battleroyale.compat.forge.minecraft;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.minecraft.TriResult;

public class TriResultHelper {

    public static TriResult convert(Event.Result result) {
        return switch (result) {
            case ALLOW -> TriResult.ALLOW;
            case DENY -> TriResult.DENY;
            default -> TriResult.DEFAULT;
        };
    }
    public static Event.Result convert(TriResult result) {
        return switch (result) {
            case ALLOW -> Event.Result.ALLOW;
            case DENY -> Event.Result.DENY;
            default -> Event.Result.DEFAULT;
        };
    }
}
