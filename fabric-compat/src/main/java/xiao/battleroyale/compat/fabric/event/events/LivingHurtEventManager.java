package xiao.battleroyale.compat.fabric.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricLivingHurtEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class LivingHurtEventManager {

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

    // 供外部（如 Mixin 或 Fabric Callback）调用的静态方法
    public static void invoke(LivingEntity entity, DamageSource source, float amount) {
        for (EventPriority priority : EventPriority.values()) {
            ((LivingHurtProxy)getProxy(priority)).onEvent(entity, source, amount);
        }
    }

    private static abstract class LivingHurtProxy extends AbstractEventCommon {
        public LivingHurtProxy() {
            super(EventType.LIVING_HURT_EVENT);
        }

        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            return new FabricLivingHurtEvent((LivingEntity)args[0], (DamageSource)args[1], (Float)args[2]);
        }
    }

    public static class Highest extends LivingHurtProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends LivingHurtProxy { static final High INSTANCE = new High(); }
    public static class Normal extends LivingHurtProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends LivingHurtProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends LivingHurtProxy { static final Lowest INSTANCE = new Lowest(); }
}