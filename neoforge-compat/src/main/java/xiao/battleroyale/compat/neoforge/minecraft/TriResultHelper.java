package xiao.battleroyale.compat.neoforge.minecraft;

import net.neoforged.neoforge.common.util.TriState;
import xiao.battleroyale.api.minecraft.TriResult;

public class TriResultHelper {

    public static TriResult convert(TriState result) {
        return switch (result) {
            case TRUE -> TriResult.ALLOW;
            case FALSE -> TriResult.DENY;
            default -> TriResult.DEFAULT;
        };
    }
    public static TriState convert(TriResult result) {
        return switch (result) {
            case ALLOW -> TriState.TRUE;
            case DENY -> TriState.FALSE;
            default -> TriState.DEFAULT;
        };
    }
}