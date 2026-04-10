package xiao.battleroyale.event;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.util.ClassUtils;

/**
 * @param <T> 处理器的类型 (IEventHandler 或 ICustomEventHandler)
 */
public class _EventHandlerContainer<T> {

    protected static final EventPriority[] PRIORITY_ORDER = EventPriority.values();
    public final PrioritizedHandlerSet<T> eventHandlers;
    public final PrioritizedHandlerSet<T> statsEventHandlers;

    public _EventHandlerContainer() {
        @SuppressWarnings("unchecked")
        ClassUtils.ArraySet<T>[] handlers = new ClassUtils.ArraySet[PRIORITY_ORDER.length];
        @SuppressWarnings("unchecked")
        ClassUtils.ArraySet<T>[] statsHandlers = new ClassUtils.ArraySet[PRIORITY_ORDER.length];
        for (int i = 0; i < PRIORITY_ORDER.length; i++) {
            handlers[i] = new ClassUtils.ArraySet<>();
            statsHandlers[i] = new ClassUtils.ArraySet<>();
        }
        eventHandlers = new PrioritizedHandlerSet<>(handlers);
        statsEventHandlers = new PrioritizedHandlerSet<>(statsHandlers);
    }

    public boolean isEmpty() {
        return eventHandlers.size() + statsEventHandlers.size() == 0;
    }

    public static class PrioritizedHandlerSet<T> {
        private final ClassUtils.ArraySet<T>[] sets;

        public PrioritizedHandlerSet(ClassUtils.ArraySet<T>[] sets) {
            this.sets = sets;
        }

        public ClassUtils.ArraySet<T>[] getHandlersInOrder() {
            return sets;
        }

        private int getIndex(EventPriority priority) {
            return priority.ordinal();
        }

        public boolean contains(T eventHandler) {
            for (ClassUtils.ArraySet<T> set : sets) {
                if (set.contains(eventHandler)) {
                    return true;
                }
            }
            return false;
        }

        public boolean add(T eventHandler, EventPriority priority) {
            if (contains(eventHandler)) {
                return false;
            }
            int index = getIndex(priority);
            return sets[index].add(eventHandler);
        }

        public boolean remove(T eventHandler, EventPriority priority) {
            int index = getIndex(priority);
            return sets[index].remove(eventHandler);
        }

        public int size() {
            int size = 0;
            for (ClassUtils.ArraySet<T> set : sets) {
                size += set.size();
            }
            return size;
        }
    }
}
