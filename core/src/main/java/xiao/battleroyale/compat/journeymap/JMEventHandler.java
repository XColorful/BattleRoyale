package xiao.battleroyale.compat.journeymap;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.api.event.*;

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
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.register(get(), EventType.CLIENT_TICK_EVENT);
    }

    protected static void unregister() {
        IEventRegister eventRegister = BattleRoyale.getEventRegister();
        eventRegister.unregister(JMEventHandler.get(), EventType.CLIENT_TICK_EVENT);
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
        if (!BattleRoyale.getClientGameDataManager().getGameData().inGame()) { // 不在游戏中
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
