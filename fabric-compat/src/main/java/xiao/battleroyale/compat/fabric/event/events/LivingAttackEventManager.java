package xiao.battleroyale.compat.fabric.event.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricLivingAttackEvent;

public class LivingAttackEventManager {

    static {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            // 这里我们需要一个临时的 Event 对象来检测 canceled 状态
            // 因为 5 个代理类共用一个逻辑上的事件状态
            FabricLivingAttackEvent sharedEvent = new FabricLivingAttackEvent(entity, source, amount);

            for (EventPriority priority : EventPriority.values()) {
                // 将原始参数传入，通过索引读取驱动分发
                ((LivingAttackProxy)getProxy(priority)).onEvent(entity, source, amount, sharedEvent);
            }

            return !sharedEvent.isCanceled();
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

    private static abstract class LivingAttackProxy extends AbstractEventCommon {
        public LivingAttackProxy() {
            super(EventType.LIVING_ATTACK_EVENT);
        }

        // 这里的 args 分别是: [0]LivingEntity, [1]DamageSource, [2]Float, [3]SharedEvent适配器
        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            // 直接返回外部传入的共享适配器，以确保 setCanceled 能在 5 个优先级之间流转并最终反馈给 Fabric
            return (FabricEvent) args[3];
        }

        // 覆写 onEvent 以便正确传递共享适配器对象
        public void onEvent(LivingEntity entity, DamageSource source, float amount, FabricLivingAttackEvent shared) {
            super.onEvent(entity, source, amount, shared);
        }
    }

    public static class Highest extends LivingAttackProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends LivingAttackProxy { static final High INSTANCE = new High(); }
    public static class Normal extends LivingAttackProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends LivingAttackProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends LivingAttackProxy { static final Lowest INSTANCE = new Lowest(); }
}