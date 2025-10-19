package xiao.battleroyale.compat.journeymap;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IClientTickEvent;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.event.EventRegister;

public class JMEventHandler implements IEventHandler {

    private static class JMEventHandlerHolder {
        private static final JMEventHandler INSTANCE = new JMEventHandler();
    }

    public static JMEventHandler get() {
        return JMEventHandlerHolder.INSTANCE;
    }

    private JMEventHandler() {}

    public static final String MOD_JM_ID = BattleRoyale.MOD_ID;

    @Override
    public String getEventHandlerName() {
        return "JMEventHandler";
    }

    protected static void register() {
        EventRegister.register(get(), EventType.CLIENT_TICK_EVENT);
    }

    protected static void unregister() {
        EventRegister.unregister(JMEventHandler.get(), EventType.CLIENT_TICK_EVENT);
        BattleRoyale.getCompatApi().jmApi().removeAll(JMEventHandler.MOD_JM_ID);
    }

    @Override
    public void handleEvent(EventType eventType, IEvent event) {
        if (eventType == EventType.CLIENT_TICK_EVENT) {
            onClientTick((IClientTickEvent) event);
        } else {
            BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
        }
    }

    private void onClientTick(IClientTickEvent event) {
        IJmApi jmApi = BattleRoyale.getCompatApi().jmApi();
        if (!ClientGameDataManager.get().getGameData().inGame()) { // 不在游戏中
            if (!JMShapeDrawer.isCleared) {
                jmApi.removeAll(MOD_JM_ID);
                JMShapeDrawer.isCleared = true;
            }
        } else { // 在游戏中
            ResourceKey<Level> dimension = JMShapeDrawer.cachedDimension;
            if (dimension == null) {
                return;
            }
            JMShapeDrawer.onMappingStarted(jmApi, dimension);
        }
    }
}
