package xiao.battleroyale.compat.fabric.client.init;

import xiao.battleroyale.api.client.init.IModEntityRender;
import xiao.battleroyale.client.init.ModEntityRender;

public class FabricModEntityRender {
    private static final IModEntityRender MOD_ENTITY_RENDER = ModEntityRender.get();

    public static void init() {
        MOD_ENTITY_RENDER.onRegisterEntityRenderers();
    }
}