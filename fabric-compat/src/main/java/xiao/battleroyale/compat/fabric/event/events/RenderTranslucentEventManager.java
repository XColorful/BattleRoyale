package xiao.battleroyale.compat.fabric.event.events;

import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricRenderLevelStageEvent;
import xiao.battleroyale.api.event.RenderLevelStage;

public class RenderTranslucentEventManager {

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

    // 供外部调用
    public static void invoke(Matrix4f poseStack, Camera camera, float partialTick) {
        for (EventPriority priority : EventPriority.values()) {
            ((RenderTranslucentProxy)getProxy(priority)).onEvent(poseStack, camera, partialTick);
        }
    }

    private static abstract class RenderTranslucentProxy extends AbstractEventCommon {
        public RenderTranslucentProxy() { super(EventType.RENDER_TRANSLUCENT_EVENT); }

        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            // 固定为 AFTER_TRANSLUCENT_BLOCKS 阶段
            return new FabricRenderLevelStageEvent(RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS, (Matrix4f) args[0], (Camera) args[1], (Float)args[2]);
        }

        public void onEvent(Object... args) { super.onEvent(args); }
    }

    public static class Highest extends RenderTranslucentProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends RenderTranslucentProxy { static final High INSTANCE = new High(); }
    public static class Normal extends RenderTranslucentProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends RenderTranslucentProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends RenderTranslucentProxy { static final Lowest INSTANCE = new Lowest(); }
}