package xiao.battleroyale.event.custom;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.common.game.process.deathmatch.DMRegister;
import xiao.battleroyale.event.custom.client.SpecialRenderHandler;

public class CustomEventHandler {

    public static void registerAll(ICustomEventRegister customEventRegister) {
        register(customEventRegister, DMRegister.get(), CustomEventType.REGISTER_MANAGER_EVENT, EventPriority.NORMAL, false);

        if (BattleRoyale.getMcSide().isClientSide()) {
            registerClient(customEventRegister);
        }
    }

    // 如果专用服务器触发类加载问题再把相关接口里改成Object, 并让该部分完全仅客户端执行以代替BattleRoyale.getMcSide().isClientSide()
    public static void registerClient(ICustomEventRegister customEventRegister) {
        register(customEventRegister, SpecialRenderHandler.get(), CustomEventType.SPECIAL_ZONE_RENDER_EVENT, EventPriority.NORMAL, false);
    }

    private static void register(ICustomEventRegister customEventRegister, ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        if (customEventRegister.register(eventHandler, customEventType, priority, receiveCanceled)) {
            BattleRoyale.LOGGER.debug("{} registered to {}", eventHandler.getEventHandlerName(), customEventType);
        } else {
            BattleRoyale.LOGGER.debug("Failed to register {} to {}", eventHandler.getEventHandlerName(), customEventType);
        }
    }
}
