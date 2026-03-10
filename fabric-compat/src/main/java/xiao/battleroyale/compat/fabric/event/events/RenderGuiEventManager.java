package xiao.battleroyale.compat.fabric.event.events;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricRenderGuiEvent;

public class RenderGuiEventManager {

    static {
        HudRenderCallback.EVENT.register((guiGraphics, tickCounter) -> {
            for (EventPriority priority : EventPriority.values()) {
                ((RenderGuiProxy)getProxy(priority)).onEvent(guiGraphics);
            }
        });
    }

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> Highest.INSTANCE;
            case HIGH -> High.INSTANCE;
            case NORMAL -> Normal.INSTANCE;
            case LOW -> Low.INSTANCE;
            case LOWEST -> Lowest.INSTANCE;
        };
    }

    private static abstract class RenderGuiProxy extends AbstractEventCommon {
        public RenderGuiProxy() { super(EventType.RENDER_GUI_EVENT); }
        @Override
        protected FabricEvent getFabricEventType(Object... args) { return new FabricRenderGuiEvent((GuiGraphics) args[0]); }
        public void onEvent(Object guiGraphics) { super.onEvent(guiGraphics); }
    }

    public static class Highest extends RenderGuiProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends RenderGuiProxy { static final High INSTANCE = new High(); }
    public static class Normal extends RenderGuiProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends RenderGuiProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends RenderGuiProxy { static final Lowest INSTANCE = new Lowest(); }
}