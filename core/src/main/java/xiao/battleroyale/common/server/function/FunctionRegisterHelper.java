package xiao.battleroyale.common.server.function;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.api.minecraft.IMcRegistry;
import xiao.battleroyale.config.common.server.function.type.RegisterEntry;

import java.util.HashMap;
import java.util.Map;

public class FunctionRegisterHelper {

    public static final _String_to_eventClass string_to_eventClass = new _String_to_eventClass();
    public static class _String_to_eventClass {
        public Map<String, Class<? extends ICustomEvent>> cache = new HashMap<>();

        @SuppressWarnings("unchecked")
        public @Nullable Class<? extends ICustomEvent> get(String classString) {
            return cache.computeIfAbsent(classString, k -> {
                try {
                    Class<?> clazz = Class.forName(k);
                    if (ICustomEvent.class.isAssignableFrom(clazz)) {
                        return (Class<? extends ICustomEvent>) clazz;
                    }
                } catch (ClassNotFoundException ignored) {
                    BattleRoyale.LOGGER.warn("FunctionRegisterHelper: {} is not ICustomEvent", classString);
                } catch (Exception e) {
                    BattleRoyale.LOGGER.error("Error loading event class: {}", k, e);
                }
                return null;
            });
        }
    }

    // --------IConfigFunctionRegister--------

    protected static void registerAll(FunctionData configFunction, RegisterEntry entry) {
        ICustomEventRegister eventRegister = BattleRoyale.getEventRegister();
        IMcRegistry mcRegistry = BattleRoyale.getMcRegistry();

        for (RegisterEntry.RegisterDetail detail : entry.registerDetails) {
            ResourceLocation rl = mcRegistry.createResourceLocation(detail.rl);
            if (rl == null) {
                BattleRoyale.LOGGER.debug("FunctionRegisterHelper: skipped invalid rl string: {}", detail.rl);
                continue;
            }

            // 注册自定义事件类
            if (detail.eventClass != null) {
                @Nullable Class<? extends ICustomEvent> clazz = string_to_eventClass.get(detail.eventClass);
                if (clazz == null) {
                    BattleRoyale.LOGGER.warn("FunctionRegisterHelper: failed to get event class {}", detail.eventClass);
                    continue;
                }
                boolean success = configFunction.registerFunction(eventRegister, rl, detail.isTag, clazz, detail.priority, detail.receiveCanceled);
                if (success) {
                    BattleRoyale.LOGGER.debug("FunctionRegisterHelper: Registered {} to {}", rl, detail.customEventType);
                } else {
                    BattleRoyale.LOGGER.warn("FunctionRegisterHelper: Failed to register {} to {}, this maybe duplicate register or internal error", rl, detail.customEventType);
                }
                continue;
            }

            // 注册自定义事件
            if (detail.customEventType != null) {
                boolean success = configFunction.registerFunction(eventRegister, rl, detail.isTag, detail.customEventType, detail.priority, detail.receiveCanceled);
                if (success) {
                    BattleRoyale.LOGGER.debug("FunctionRegisterHelper: Registered {} to {}", rl, detail.customEventType);
                } else {
                    BattleRoyale.LOGGER.warn("FunctionRegisterHelper: Failed to register {} to {}, this maybe duplicate register or internal error", rl, detail.customEventType);
                }
            }
            // 注册事件
            else {
                boolean success = configFunction.registerFunction(eventRegister, rl, detail.isTag, detail.eventType, detail.priority, detail.receiveCanceled);
                if (success) {
                    BattleRoyale.LOGGER.debug("FunctionRegisterHelper: Registered {} to {}", rl, detail.eventType);
                } else {
                    BattleRoyale.LOGGER.warn("FunctionRegisterHelper: Failed to register {} to {}, this maybe duplicate register or internal error", rl, detail.eventType);
                }
            }
        }
    }

    // --------IFunctionRegisterApi--------

    protected static boolean registerEventClass(FunctionData apiFunction, String rlStr, boolean isTag, String eventClassStr, String priorityStr, boolean receiveCanceled) {
        ResourceLocation rl = BattleRoyale.getMcRegistry().createResourceLocation(rlStr);
        Class<? extends ICustomEvent> eventClass = string_to_eventClass.get(eventClassStr);
        EventPriority priority = EventPriority.fromString(priorityStr);
        if (rl == null || eventClass == null || priority == null) {
            return false;
        }
        return apiFunction.registerFunction(BattleRoyale.getEventRegister(), rl, isTag, eventClass, priority, receiveCanceled);
    }
    protected static boolean unregisterEventClass(FunctionData apiFunction, String rlStr, boolean isTag, String eventClassStr) {
        ResourceLocation rl = BattleRoyale.getMcRegistry().createResourceLocation(rlStr);
        Class<? extends ICustomEvent> eventClass = string_to_eventClass.get(eventClassStr);
        if (rl == null || eventClass == null) {
            return false;
        }
        return apiFunction.unregisterFunction(BattleRoyale.getEventRegister(), rl, isTag, eventClass);
    }
}
