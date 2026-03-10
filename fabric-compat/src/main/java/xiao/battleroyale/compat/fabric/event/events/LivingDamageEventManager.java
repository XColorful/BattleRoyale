package xiao.battleroyale.compat.fabric.event.events;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricLivingDamageEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class LivingDamageEventManager {

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

    // 供外部（如 Mixin）调用
    public static void invoke(LivingEntity entity, DamageSource source, float amount) {
        FabricLivingDamageEvent sharedEvent = new FabricLivingDamageEvent(entity, source, amount);
        for (EventPriority priority : EventPriority.values()) {
            ((LivingDamageProxy)getProxy(priority)).onEvent(sharedEvent);
        }
    }

    private static abstract class LivingDamageProxy extends AbstractEventCommon {
        public LivingDamageProxy() { super(EventType.LIVING_DAMAGE_EVENT); }

        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            return (FabricEvent) args[0];
        }

        public void onEvent(FabricLivingDamageEvent e) { super.onEvent(e); }
    }

    public static class Highest extends LivingDamageProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends LivingDamageProxy { static final High INSTANCE = new High(); }
    public static class Normal extends LivingDamageProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends LivingDamageProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends LivingDamageProxy { static final Lowest INSTANCE = new Lowest(); }
}