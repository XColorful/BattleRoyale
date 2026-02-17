package xiao.battleroyale.common.game.process.deathmatch;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.register.RegisterManagerEvent;

public class DMRegister implements ICustomEventHandler {

    private static class DMRegisterHolder {
        private static final DMRegister INSTANCE = new DMRegister();
    }

    public static DMRegister get() {
        return DMRegisterHolder.INSTANCE;
    }

    private DMRegister() {}

    @Override
    public String getEventHandlerName() {
        return String.format("%s:DMRegister", BattleRoyale.MOD_ID);
    }

    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        if (customEventType == CustomEventType.REGISTER_MANAGER_EVENT) {
            registerDMManager((RegisterManagerEvent) event);
        } else {
            onReceiveWrongEvent(customEventType);
        }
    }

    private void registerDMManager(RegisterManagerEvent event) {
        String protocol = event.getProtocol();
        if (protocol == null || protocol.isEmpty()) {
            return;
        }
        String[] parts = protocol.split(":", 2);
        if (parts.length != 2) {
            return;
        }
        String namespace = parts[0];
        String managerName = parts[1];
        if (namespace.equals(BattleRoyale.MOD_ID) || namespace.equals(BattleRoyale.MOD_NAME_SHORT)) {
            if (managerName.equals("DMGameProcessManager")) {
                boolean registered = BattleRoyale.getGameManager().setGameProcessManager(DMGameProcessManager.get());
                event.setCanceled(registered);
            }
        }
    }
}
