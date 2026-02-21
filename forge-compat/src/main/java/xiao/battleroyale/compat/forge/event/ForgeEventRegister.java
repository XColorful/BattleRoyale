package xiao.battleroyale.compat.forge.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEventRegister;
import xiao.battleroyale.compat.forge.event.events.*;

public class ForgeEventRegister implements IEventRegister {

    @Override
    public boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return switch (eventType) {
            case SERVER_TICK_EVENT -> ServerTickEventManager.register(eventHandler, priority, receiveCanceled);
            case CLIENT_TICK_EVENT -> ClientTickEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_ATTACK_EVENT -> LivingAttackEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_HURT_EVENT -> LivingHurtEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> LivingDamageEventManager.register(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> LivingDeathEventManager.register(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_IN_EVENT -> PlayerLoggedInEventManager.register(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_OUT_EVENT -> PlayerLoggedOutEventManager.register(eventHandler, priority, receiveCanceled);
            case RENDER_LEVEL_STAGE_EVENT -> RenderLevelStageEventManager.register(eventHandler, priority, receiveCanceled);
            case RENDER_TRANSLUCENT_EVENT -> RenderTranslucentEventManager.register(eventHandler, priority, receiveCanceled);
            case RENDER_GUI_EVENT -> RenderGuiEventManager.register(eventHandler, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.warn("Attempted to register handler for unassigned EventType: {}. Registration aborted.", eventType);
                yield false;
            }
        };
    }

    @Override
    public boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return switch (eventType) {
            case SERVER_TICK_EVENT -> ServerTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case CLIENT_TICK_EVENT -> ClientTickEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_ATTACK_EVENT -> LivingAttackEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_HURT_EVENT -> LivingHurtEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DAMAGE_EVENT -> LivingDamageEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LIVING_DEATH_EVENT -> LivingDeathEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_IN_EVENT -> PlayerLoggedInEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PLAYER_LOGGED_OUT_EVENT -> PlayerLoggedOutEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RENDER_LEVEL_STAGE_EVENT -> RenderLevelStageEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RENDER_TRANSLUCENT_EVENT -> RenderTranslucentEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RENDER_GUI_EVENT -> RenderGuiEventManager.unregister(eventHandler, priority, receiveCanceled);
            default -> {
                BattleRoyale.LOGGER.warn("Attempted to unregister handler for unassigned EventType: {}. Registration aborted.", eventType);
                yield false;
            }
        };
    }
}