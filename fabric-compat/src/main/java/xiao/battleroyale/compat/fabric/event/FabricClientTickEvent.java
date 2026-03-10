package xiao.battleroyale.compat.fabric.event;

import xiao.battleroyale.api.event.IClientTickEvent;

public class FabricClientTickEvent extends FabricEvent implements IClientTickEvent {
    public FabricClientTickEvent() {
        super(false);
    }
}