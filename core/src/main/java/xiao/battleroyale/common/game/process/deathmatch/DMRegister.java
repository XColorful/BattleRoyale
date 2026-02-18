package xiao.battleroyale.common.game.process.deathmatch;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.register.RegisterManagerEvent;
import xiao.battleroyale.util.StringUtils;

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
        StringUtils.ProtocolString protocolString = event.getProtocolString();
        if (protocolString.namespace.equals(BattleRoyale.MOD_ID) || protocolString.namespace.equals(BattleRoyale.MOD_NAME_SHORT)) {
            if (protocolString.name.equals("DMGameProcessManager")) {
                boolean registered = BattleRoyale.getGameManager().setGameProcessManager(DMGameProcessManager.get());
                event.setCanceled(registered);
            }
        }
    }
}
