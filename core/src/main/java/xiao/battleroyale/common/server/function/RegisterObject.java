package xiao.battleroyale.common.server.function;

import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;

import java.time.LocalDateTime;

/**
 * 用于创建实例以注册事件的类
 */
public abstract class RegisterObject<T extends Enum<T>> implements IEventHandler, ICustomEventHandler {
    public final String handlerName;
    public final ResourceLocation rl;
    public final T eventType;
    public final EventPriority priority;
    public final boolean receiveCanceled;
    public abstract boolean register(ICustomEventRegister eventRegister);
    public abstract boolean unregister(ICustomEventRegister eventRegister);
    public RegisterObject(ResourceLocation rl, T eventType, EventPriority priority, boolean receiveCanceled) {
        this.rl = rl;
        this.eventType = eventType;
        this.priority = priority;
        this.receiveCanceled = receiveCanceled;
        this.handlerName = String.format("RegisterObject %s %s %s %s %s", LocalDateTime.now(), rl, eventType, priority, receiveCanceled);
    }
    @Override public String getEventHandlerName() {
        return handlerName;
    }
    @Override public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        unregister(BattleRoyale.getEventRegister());
    }
    @Override public void handleEvent(EventType eventType, IEvent event) {
        unregister(BattleRoyale.getEventRegister());
    }

    // 模组事件
    public static class EventRegister extends RegisterObject<EventType> {
        public EventRegister(ResourceLocation rl, EventType eventType, EventPriority priority, boolean receiveCanceled) {
            super(rl, eventType, priority, receiveCanceled);
        }
        @Override public boolean register(ICustomEventRegister eventRegister) {
            return eventRegister.register(this, eventType, priority, receiveCanceled);
        }
        @Override public boolean unregister(ICustomEventRegister eventRegister) {
            return eventRegister.unregister(this, eventType, priority, receiveCanceled);
        }
        @Override
        public void handleEvent(EventType eventType, IEvent event) {
            if (eventType == this.eventType) {
                ;
            } else {
                onReceiveWrongEvent(eventType);
            }
        }
    }

    // 自定义事件
    public static class CustomEventRegister extends RegisterObject<CustomEventType> {
        public CustomEventRegister(ResourceLocation rl, CustomEventType eventType, EventPriority priority, boolean receiveCanceled) {
            super(rl, eventType, priority, receiveCanceled);
        }
        @Override public boolean register(ICustomEventRegister eventRegister) {
            return eventRegister.register(this, eventType, priority, receiveCanceled);
        }
        @Override public boolean unregister(ICustomEventRegister eventRegister) {
            return eventRegister.unregister(this, eventType, priority, receiveCanceled);
        }
        @Override
        public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
            if (customEventType == this.eventType) {
                ;
            } else {
                onReceiveWrongEvent(eventType);
            }
        }
    }

    // 自定义事件类
    public static class ClassEventRegister<T extends ICustomEvent> extends CustomEventRegister {
        public final Class<T> eventClass;
        public ClassEventRegister(ResourceLocation rl, Class<T> clazz, EventPriority priority, boolean receiveCanceled) {
            super(rl, CustomEventType.CUSTOM_EVENT, priority, receiveCanceled);
            this.eventClass = clazz;
        }
        @Override public boolean register(ICustomEventRegister eventRegister) {
            return eventRegister.register(this, eventClass, priority, receiveCanceled);
        }
        @Override public boolean unregister(ICustomEventRegister eventRegister) {
            return eventRegister.unregister(this, eventClass, priority, receiveCanceled);
        }
    }
}
