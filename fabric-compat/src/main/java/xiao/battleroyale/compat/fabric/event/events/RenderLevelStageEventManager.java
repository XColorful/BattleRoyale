package xiao.battleroyale.compat.fabric.event.events;

import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.RenderLevelStage;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricRenderLevelStageEvent;

public class RenderLevelStageEventManager {

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

    public static void invoke(RenderLevelStage stage, Matrix4f poseStack, Camera camera, float partialTick) {
        for (EventPriority priority : EventPriority.values()) {
            ((RenderLevelStageProxy)getProxy(priority)).onEvent(stage, poseStack, camera, partialTick);
        }
    }

    private static abstract class RenderLevelStageProxy extends AbstractEventCommon {
        public RenderLevelStageProxy() { super(EventType.RENDER_LEVEL_STAGE_EVENT); }
        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            return new FabricRenderLevelStageEvent((RenderLevelStage)args[0], (Matrix4f) args[1], (Camera) args[2], (Float)args[3]);
        }
        public void onEvent(Object... args) { super.onEvent(args); }
    }

    public static class Highest extends RenderLevelStageProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends RenderLevelStageProxy { static final High INSTANCE = new High(); }
    public static class Normal extends RenderLevelStageProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends RenderLevelStageProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends RenderLevelStageProxy { static final Lowest INSTANCE = new Lowest(); }
}