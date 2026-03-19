package xiao.battleroyale.common.server.function;

import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.data.data.AbstractNameData;

import java.util.HashMap;
import java.util.Map;

public class FunctionData extends AbstractNameData {

    private static final String DATA_NAME = "FunctionData";

    private final Map<ResourceLocation, RegisterObject<?>> registeredFunction;
    private final Map<ResourceLocation, RegisterObject<?>> registeredTag;

    public FunctionData() {
        super(DATA_NAME);
        this.registeredFunction = new HashMap<>();
        this.registeredTag = new HashMap<>();
    }

    public boolean registerFunction(ICustomEventRegister eventRegister,
                                    ResourceLocation rl, boolean isTag,
                                    EventType eventType, EventPriority priority, boolean receiveCanceled) {
        RegisterObject<?> registerObject = new RegisterObject.EventRegister(rl, eventType, priority, receiveCanceled);
        if (registerObject.register(eventRegister)) {
            Map<ResourceLocation, RegisterObject<?>> registry = isTag ? registeredTag : registeredFunction;
            registry.put(rl, registerObject);
            return true;
        } else {
            return false;
        }
    }
    public boolean registerFunction(ICustomEventRegister eventRegister,
                                    ResourceLocation rl, boolean isTag,
                                    CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        RegisterObject<?> registerObject = new RegisterObject.CustomEventRegister(rl, customEventType, priority, receiveCanceled);
        if (registerObject.register(eventRegister)) {
            Map<ResourceLocation, RegisterObject<?>> registry = isTag ? registeredTag : registeredFunction;
            registry.put(rl, registerObject);
            return true;
        } else {
            return false;
        }
    }
    public boolean registerFunction(ICustomEventRegister eventRegister,
                                    ResourceLocation rl, boolean isTag,
                                    Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled) {
        RegisterObject<?> registerObject = new RegisterObject.ClassEventRegister<>(rl, eventClass, priority, receiveCanceled);
        if (registerObject.register(eventRegister)) {
            Map<ResourceLocation, RegisterObject<?>> registry = isTag ? registeredTag : registeredFunction;
            registry.put(rl, registerObject);
            return true;
        } else {
            return false;
        }
    }

    public boolean unregisterFunction(ICustomEventRegister eventRegister,
                                      ResourceLocation rl, boolean isTag,
                                      EventType eventType) {
        Map<ResourceLocation, RegisterObject<?>> registry = isTag ? registeredTag : registeredFunction;
        RegisterObject<?> registerObject = registry.get(rl);
        if (registerObject instanceof RegisterObject.EventRegister eventRegisterObject) {
            if (eventRegisterObject.eventType != eventType) {
                BattleRoyale.LOGGER.warn("Attempt to unregister {} by {} (Expect {})", rl, eventType, eventRegisterObject.eventType);
                return false;
            }
            registerObject.unregister(eventRegister);
            registry.remove(rl);
            return true;
        } else {
            return false;
        }
    }
    public boolean unregisterFunction(ICustomEventRegister eventRegister,
                                      ResourceLocation rl, boolean isTag,
                                      CustomEventType customEventType) {
        Map<ResourceLocation, RegisterObject<?>> registry = isTag ? registeredTag : registeredFunction;
        RegisterObject<?> registerObject = registry.get(rl);
        if (registerObject instanceof RegisterObject.CustomEventRegister customEventRegisterObject) {
            if (customEventRegisterObject.eventType != customEventType) {
                BattleRoyale.LOGGER.warn("Attempt to unregister {} by {} (Expect {})", rl, customEventType, customEventRegisterObject.eventType);
                return false;
            }
            registerObject.unregister(eventRegister);
            registry.remove(rl);
            return true;
        } else {
            return false;
        }
    }
    public boolean unregisterFunction(ICustomEventRegister eventRegister,
                                      ResourceLocation rl, boolean isTag,
                                      Class<? extends ICustomEvent> eventClass) {
        Map<ResourceLocation, RegisterObject<?>> registry = isTag ? registeredTag : registeredFunction;
        RegisterObject<?> registerObject = registry.get(rl);
        if (registerObject instanceof RegisterObject.ClassEventRegister<?> classEventRegisterObject) {
            if (classEventRegisterObject.eventClass != eventClass) {
                BattleRoyale.LOGGER.warn("Attempt to unregister {} by {} (Expect {})", rl, eventClass, classEventRegisterObject.eventClass);
                return false;
            }
            registerObject.unregister(eventRegister);
            registry.remove(rl);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        ICustomEventRegister eventRegister = BattleRoyale.getEventRegister();
        for (RegisterObject<?> registerObject : registeredFunction.values()) {
            registerObject.unregister(eventRegister);
        }
        registeredFunction.clear();
        for (RegisterObject<?> registerObject : registeredTag.values()) {
            registerObject.unregister(eventRegister);
        }
        registeredTag.clear();
    }
}