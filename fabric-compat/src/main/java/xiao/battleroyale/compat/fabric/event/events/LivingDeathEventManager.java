package xiao.battleroyale.compat.fabric.event.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricLivingDeathEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class LivingDeathEventManager {

    static {
        // 在静态块中一次性注册 Fabric 监听
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            for (EventPriority priority : EventPriority.values()) {
                ((LivingDeathProxy)getProxy(priority)).onEvent(entity, source);
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

    private static abstract class LivingDeathProxy extends AbstractEventCommon {
        public LivingDeathProxy() {
            super(EventType.LIVING_DEATH_EVENT);
        }

        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            return new FabricLivingDeathEvent((LivingEntity)args[0], (DamageSource)args[1]);
        }
    }

    public static class Highest extends LivingDeathProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends LivingDeathProxy { static final High INSTANCE = new High(); }
    public static class Normal extends LivingDeathProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends LivingDeathProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends LivingDeathProxy { static final Lowest INSTANCE = new Lowest(); }
}