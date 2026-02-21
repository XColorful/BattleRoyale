package xiao.battleroyale.compat.neoforge.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.compat.neoforge.event.events.*;

public class NeoEventRegister implements IEventRegister {

    @Override
    public boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return switch (eventType) {
            case SERVER_TICK_EVENT -> NeoServerTickEventManager.register(eventHandler, priority, receiveCanceled);
            case CLIENT_TICK_EVENT -> NeoClientTickEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_ATTACK_EVENT -> NeoLivingAttackEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_HURT_EVENT -> NeoLivingHurtEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> NeoLivingDamageEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> NeoLivingDeathEventManager.register(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_IN_EVENT -> NeoPlayerLoggedInEventManager.register(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_OUT_EVENT -> NeoPlayerLoggedOutEventManager.register(eventHandler, priority, receiveCanceled);
            case RENDER_LEVEL_STAGE_EVENT -> NeoRenderLevelStageEventManager.register(eventHandler, priority, receiveCanceled);
            case RENDER_TRANSLUCENT_EVENT -> NeoRenderTranslucentEventManager.register(eventHandler, priority, receiveCanceled);
            case RENDER_GUI_EVENT -> NeoRenderGuiEventManager.register(eventHandler, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.warn("Attempted to register handler for unassigned EventType: {}. Registration aborted.", eventType);
                yield false;
            }
        };
    }

    @Override
    public boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return switch (eventType) {
            case SERVER_TICK_EVENT -> NeoServerTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case CLIENT_TICK_EVENT -> NeoClientTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_ATTACK_EVENT -> NeoLivingAttackEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_HURT_EVENT -> NeoLivingHurtEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> NeoLivingDamageEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> NeoLivingDeathEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_IN_EVENT -> NeoPlayerLoggedInEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_OUT_EVENT -> NeoPlayerLoggedOutEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RENDER_LEVEL_STAGE_EVENT -> NeoRenderLevelStageEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RENDER_TRANSLUCENT_EVENT -> NeoRenderTranslucentEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RENDER_GUI_EVENT -> NeoRenderGuiEventManager.unregister(eventHandler, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.warn("Attempted to unregister handler for unassigned EventType: {}. Registration aborted.", eventType);
                yield false;
            }
        };
    }
}