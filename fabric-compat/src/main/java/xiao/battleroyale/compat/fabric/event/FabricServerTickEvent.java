package xiao.battleroyale.compat.fabric.event;

import xiao.battleroyale.api.event.IServerTickEvent;

public class FabricServerTickEvent extends FabricEvent implements IServerTickEvent {
    public FabricServerTickEvent() {
        super(false);
    }
}