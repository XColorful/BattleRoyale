package xiao.battleroyale.event.handler.util;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IServerTickEvent;
import xiao.battleroyale.event.EventRegister;

import java.util.function.Consumer;

public class DelayedEvent<T> implements IEventHandler {

    @Override public String getEventHandlerName() {
        return String.format("DelayedEvent:n(%s)%s", this.ticksLeft, this.description);
    }

    private final Consumer<T> task;
    private final T parameter;
    private int ticksLeft;
    private final String description;

    /**
     * 创建一个延迟事件，并立即注册到事件总线
     * @param task      要执行的函数
     * @param parameter 传递给函数的参数
     * @param delay     延迟的tick数
     * @param description 写入log的说明
     */
    public DelayedEvent(Consumer<T> task, T parameter, int delay, String description) {
        this.task = task;
        this.parameter = parameter;
        this.ticksLeft = delay;
        this.description = description;
        EventRegister.register(this, EventType.SERVER_TICK_EVENT);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.SERVER_TICK_EVENT) {
            onServerTick((IServerTickEvent) event);
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }

    /**
     * 事件监听器：在每个服务器tick结束时触发
     */
    private void onServerTick(IServerTickEvent event) {
        if (--this.ticksLeft == 0) {
            run();
            BattleRoyale.LOGGER.debug("Process {}", this.ticksLeft);
        }
        if (this.ticksLeft <= 0) {
            EventRegister.unregister(this, EventType.SERVER_TICK_EVENT);
        }
    }

    /**
     * 执行任务，不手动try
     */
    private void run() {
        this.task.accept(this.parameter);
    }
}