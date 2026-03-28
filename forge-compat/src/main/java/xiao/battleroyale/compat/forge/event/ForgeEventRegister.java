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
            case ENTITY_INTERACT_EVENT -> EntityInteractEventManager.register(eventHandler, priority, receiveCanceled);
            case ENTITY_INTERACT_SPECIFIC_EVENT -> EntityInteractSpecificEventManager.register(eventHandler, priority, receiveCanceled);
            case LEFT_CLICK_BLOCK_EVENT -> LeftClickBlockEventManager.register(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_BLOCK_EVENT -> RightClickBlockEventManager.register(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_ITEM_EVENT -> RightClickItemEventManager.register(eventHandler, priority, receiveCanceled);
            case BLOCK_BREAK_EVENT -> BlockBreakEventManager.register(eventHandler, priority, receiveCanceled);
            case BLOCK_TOOL_MODIFICATION_EVENT -> BlockToolModificationEventManager.register(eventHandler, priority, receiveCanceled);
            case ENTITY_PLACE_BLOCK_EVENT -> EntityPlaceBlockEventManager.register(eventHandler, priority, receiveCanceled);
            case PORTAL_SPAWN_EVENT -> PortalSpawnEventManager.register(eventHandler, priority, receiveCanceled);
            case FARMLAND_TRAMPLE_EVENT -> FarmlandTrampleEventManager.register(eventHandler, priority, receiveCanceled);
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
            case ENTITY_INTERACT_EVENT -> EntityInteractEventManager.unregister(eventHandler, priority, receiveCanceled);
            case ENTITY_INTERACT_SPECIFIC_EVENT -> EntityInteractSpecificEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LEFT_CLICK_BLOCK_EVENT -> LeftClickBlockEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_BLOCK_EVENT -> RightClickBlockEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_ITEM_EVENT -> RightClickItemEventManager.unregister(eventHandler, priority, receiveCanceled);
            case BLOCK_BREAK_EVENT -> BlockBreakEventManager.unregister(eventHandler, priority, receiveCanceled);
            case BLOCK_TOOL_MODIFICATION_EVENT -> BlockToolModificationEventManager.unregister(eventHandler, priority, receiveCanceled);
            case ENTITY_PLACE_BLOCK_EVENT -> EntityPlaceBlockEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PORTAL_SPAWN_EVENT -> PortalSpawnEventManager.unregister(eventHandler, priority, receiveCanceled);
            case FARMLAND_TRAMPLE_EVENT -> FarmlandTrampleEventManager.unregister(eventHandler, priority, receiveCanceled);
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