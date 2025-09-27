package xiao.battleroyale.event.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.BattleRoyale;

import java.util.function.Consumer;

public class DelayedEvent<T> {
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
        MinecraftForge.EVENT_BUS.register(this);
        BattleRoyale.LOGGER.debug("n({}): {}", this.ticksLeft, this.description);
    }

    /**
     * 事件监听器：在每个服务器tick结束时触发
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (--this.ticksLeft == 0) {
            run();
            BattleRoyale.LOGGER.debug("Process {}", this.ticksLeft);
        }
        if (this.ticksLeft <= 0) {
            MinecraftForge.EVENT_BUS.unregister(this);
            BattleRoyale.LOGGER.debug("Unregistered delayedTask: {}", this.description);
        }
    }

    /**
     * 执行任务，不手动try
     */
    private void run() {
        this.task.accept(this.parameter);
    }
}