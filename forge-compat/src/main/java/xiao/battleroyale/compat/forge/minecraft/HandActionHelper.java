package xiao.battleroyale.compat.forge.minecraft;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action;
import xiao.battleroyale.api.minecraft.HandAction;

public class HandActionHelper {

    public static HandAction convert(Action action) {
        return switch (action) {
            case START -> HandAction.START;
            case STOP -> HandAction.STOP;
            case ABORT -> HandAction.ABORT;
            case CLIENT_HOLD -> HandAction.CLIENT_HOLD;
        };
    }
    public static Action convert(HandAction action) {
        return switch (action) {
            case START -> Action.START;
            case STOP -> Action.STOP;
            case ABORT -> Action.ABORT;
            case CLIENT_HOLD -> Action.CLIENT_HOLD;
        };
    }
}
