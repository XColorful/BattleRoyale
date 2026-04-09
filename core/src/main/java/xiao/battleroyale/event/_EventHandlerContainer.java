package xiao.battleroyale.event;

import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.util.ClassUtils;

public class _EventHandlerContainer {

    protected static final EventPriority[] PRIORITY_ORDER = EventPriority.values();
    public final PrioritizedHandlerSet eventHandlers;
    public final PrioritizedHandlerSet statsEventHandlers;

    public _EventHandlerContainer() {
        @SuppressWarnings("unchecked")
        ClassUtils.ArraySet<ICustomEventHandler>[] handlers = new ClassUtils.ArraySet[PRIORITY_ORDER.length];
        @SuppressWarnings("unchecked")
        ClassUtils.ArraySet<ICustomEventHandler>[] statsHandlers = new ClassUtils.ArraySet[PRIORITY_ORDER.length];
        for (int i = 0; i < PRIORITY_ORDER.length; i++) {
            handlers[i] = new ClassUtils.ArraySet<>();
            statsHandlers[i] = new ClassUtils.ArraySet<>();
        }
        eventHandlers = new PrioritizedHandlerSet(handlers);
        statsEventHandlers = new PrioritizedHandlerSet(statsHandlers);
    }

    public boolean isEmpty() {
        return eventHandlers.size() + statsEventHandlers.size() == 0;
    }

    public static class PrioritizedHandlerSet {
        private final ClassUtils.ArraySet<ICustomEventHandler>[] sets;

        public PrioritizedHandlerSet(ClassUtils.ArraySet<ICustomEventHandler>[] sets) {
            this.sets = sets;
        }

        public ClassUtils.ArraySet<ICustomEventHandler>[] getHandlersInOrder() {
            return sets;
        }

        private int getIndex(EventPriority priority) {
            return priority.ordinal();
        }

        public boolean contains(ICustomEventHandler eventHandler) {
            for (ClassUtils.ArraySet<ICustomEventHandler> set : sets) {
                if (set.contains(eventHandler)) {
                    return true;
                }
            }
            return false;
        }

        public boolean add(ICustomEventHandler eventHandler, EventPriority priority) {
            if (contains(eventHandler)) {
                return false;
            }
            int index = getIndex(priority);
            return sets[index].add(eventHandler);
        }

        public boolean remove(ICustomEventHandler eventHandler, EventPriority priority) {
            int index = getIndex(priority);
            return sets[index].remove(eventHandler);
        }

        public int size() {
            int size = 0;
            for (ClassUtils.ArraySet<ICustomEventHandler> set : sets) {
                size += set.size();
            }
            return size;
        }
    }
}
