package xiao.battleroyale.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.loot.LootGenerationManager;

public class LootGenerationEventHandler {

    private static LootGenerationEventHandler instance;

    // 私有构造函数，确保只能通过 getInstance() 获取单例
    private LootGenerationEventHandler() {
    }

    /**
     * 获取 LootGenerationEventHandler 的单例实例。
     * @return LootGenerationEventHandler 的单例实例
     */
    public static LootGenerationEventHandler getInstance() {
        if (instance == null) {
            instance = new LootGenerationEventHandler();
        }
        return instance;
    }

    /**
     * 注册此事件处理器到 Forge 事件总线。
     */
    public static void register() {
        LootGenerationEventHandler handler = getInstance();
        MinecraftForge.EVENT_BUS.register(handler);
        BattleRoyale.LOGGER.info("LootGenerationEventHandler registered for tick events.");
    }

    /**
     * 从 Forge 事件总线取消注册此事件处理器。
     */
    public static void unregister() {
        LootGenerationEventHandler handler = getInstance();
        MinecraftForge.EVENT_BUS.unregister(handler);
        // 清空单例引用，以便下次重新开始时是干净的
        instance = null;
        BattleRoyale.LOGGER.info("LootGenerationEventHandler unregistered from tick events.");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // 将实际的 Tick 处理逻辑委托给 LootGenerationManager
            // LootGenerationManager 会在任务完成或中断时通知此处理器取消注册
            boolean taskCompletedOrInterrupted = LootGenerationManager.get().onTick(event);
            if (taskCompletedOrInterrupted) {
                // 任务完成后，LootGenerationManager 会返回 true，此时通知自身取消注册
                unregister();
            }
        }
    }
}