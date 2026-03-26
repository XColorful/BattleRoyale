package xiao.battleroyale.common.server.function;

import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.util.CommandUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * 用于创建实例以注册事件的类
 */
public abstract class RegisterObject<T extends Enum<T>, K extends IEvent> implements IEventHandler, ICustomEventHandler {
    public final String handlerName;
    public final ResourceLocation rl;
    public final boolean isTag;
    public final T eventType;
    public final EventPriority priority;
    public final boolean receiveCanceled;
    public abstract boolean register(ICustomEventRegister eventRegister);
    public abstract boolean unregister(ICustomEventRegister eventRegister);
    public RegisterObject(ResourceLocation rl, boolean isTag, T eventType, EventPriority priority, boolean receiveCanceled) {
        this.rl = rl;
        this.isTag = isTag;
        this.eventType = eventType;
        this.priority = priority;
        this.receiveCanceled = receiveCanceled;
        this.handlerName = String.format("RegisterObject %s %s %s %s %s", LocalDateTime.now(), rl, eventType, priority, receiveCanceled);
    }
    @Override public String getEventHandlerName() {
        return handlerName;
    }
    @SuppressWarnings("unchecked")
    @Override public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        if (this.eventType == customEventType) {
            handleEventInternal((T) customEventType, (K) event);
        } else {
            onReceiveWrongEvent(customEventType);
        }
    }
    @SuppressWarnings("unchecked")
    @Override public void handleEvent(EventType eventType, IEvent event) {
        if (this.eventType == eventType) {
            try {
                handleEventInternal((T) eventType, (K) event);
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Encountered an exception in RegisterObject::handlerEvent: {}", e.getMessage());
            }
        } else {
            onReceiveWrongEvent(eventType);
        }
    }
    protected void handleEventInternal(T eventType, K event) {
        @Nullable CommandSourceStack sourceStack = event.createCommandSourceStack(CommandSource.NULL);
        if (sourceStack == null) {
            BattleRoyale.LOGGER.debug("RegisterObject: {} failed to get CommandSourceStack from {}", getEventHandlerName(), eventType);
            return;
        }

        final int[] result = {0};
        final int[] executedLines = {0};

        final CommandSourceStack finalSourceStack = sourceStack.withReturnValueConsumer(val -> {
            result[0] = val;
        });
        ServerFunctionManager serverFunctionManager = finalSourceStack.getServer().getFunctions();

        if (this.isTag) {
            Collection<CommandFunction> functions = serverFunctionManager.getTag(this.rl);
            for (CommandFunction function : functions) {
                boolean canceledBefore = event.isCanceled();
                if (!receiveCanceled && canceledBefore) return;
                CommandUtils.executeCommand(serverFunctionManager, function, finalSourceStack, executedLines);
                if (result[0] <= -1) {
                    event.setCanceled(true);
                    if (event.isCanceled()) {
                        BattleRoyale.LOGGER.debug("{} {}canceled by function (return value: {}) after executed {} functions",
                                event.getTextName(), canceledBefore ? "(already canceled before) " : "", result[0], executedLines[0]);
                        if (!receiveCanceled) return;
                    } else {
                        BattleRoyale.LOGGER.debug("{} (Cancelable: {}) not cancenled by function (return value: {}) after executed {} functions",
                                event.getTextName(), event.isCancelable(), result[0], executedLines[0]);
                    }
                }
            }
        } else {
            Optional<CommandFunction> function = serverFunctionManager.get(this.rl);
            if (function.isPresent()) {
                boolean canceledBefore = event.isCanceled();
                if (!receiveCanceled && canceledBefore) return;
                CommandUtils.executeCommand(serverFunctionManager, function.get(), finalSourceStack, executedLines);
                if (result[0] <= -1) {
                    event.setCanceled(true);
                    if (event.isCanceled()) {
                        BattleRoyale.LOGGER.debug("{} {}canceled by function (return value: {}) after executed {} functions",
                                event.getTextName(), canceledBefore ? "(already canceled before) " : "", result[0], executedLines[0]);
                        if (!receiveCanceled) return;
                    } else {
                        BattleRoyale.LOGGER.debug("{} (Cancelable: {}) not cancenled by function (return value: {}) after executed {} functions",
                                event.getTextName(), event.isCancelable(), result[0], executedLines[0]);
                    }
                }
            }
        }
    }

    // 模组事件
    public static class EventRegister extends RegisterObject<EventType, IEvent> {
        public EventRegister(ResourceLocation rl, boolean isTag, EventType eventType, EventPriority priority, boolean receiveCanceled) {
            super(rl, isTag, eventType, priority, receiveCanceled);
        }
        @Override public boolean register(ICustomEventRegister eventRegister) {
            return eventRegister.register(this, eventType, priority, receiveCanceled);
        }
        @Override public boolean unregister(ICustomEventRegister eventRegister) {
            return eventRegister.unregister(this, eventType, priority, receiveCanceled);
        }
        @Override public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
            unregister(BattleRoyale.getEventRegister());
        }
    }

    // 自定义事件
    public static class CustomEventRegister extends RegisterObject<CustomEventType, ICustomEvent> {
        public CustomEventRegister(ResourceLocation rl, boolean isTag, CustomEventType eventType, EventPriority priority, boolean receiveCanceled) {
            super(rl, isTag, eventType, priority, receiveCanceled);
        }
        @Override public boolean register(ICustomEventRegister eventRegister) {
            return eventRegister.register(this, eventType, priority, receiveCanceled);
        }
        @Override public boolean unregister(ICustomEventRegister eventRegister) {
            return eventRegister.unregister(this, eventType, priority, receiveCanceled);
        }
        @Override public void handleEvent(EventType eventType, IEvent event) {
            unregister(BattleRoyale.getEventRegister());
        }
    }

    // 自定义事件类
    public static class ClassEventRegister<T extends ICustomEvent> extends CustomEventRegister {
        public final Class<T> eventClass;
        public ClassEventRegister(ResourceLocation rl, boolean isTag, Class<T> clazz, EventPriority priority, boolean receiveCanceled) {
            super(rl, isTag, CustomEventType.CUSTOM_EVENT, priority, receiveCanceled);
            this.eventClass = clazz;
        }
        @Override public boolean register(ICustomEventRegister eventRegister) {
            return eventRegister.register(this, eventClass, priority, receiveCanceled);
        }
        @Override public boolean unregister(ICustomEventRegister eventRegister) {
            return eventRegister.unregister(this, eventClass, priority, receiveCanceled);
        }
        public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
            if (this.eventType == customEventType && eventClass.isInstance(event)) {
                handleEventInternal(customEventType, event);
            } else {
                onReceiveWrongEvent(customEventType);
            }
        }
    }
}
