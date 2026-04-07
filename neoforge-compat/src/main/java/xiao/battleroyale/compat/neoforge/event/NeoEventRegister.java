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
            case PLAYER_RESPAWN_EVENT -> NeoPlayerRespawnEventManager.register(eventHandler, priority, receiveCanceled);
            case ENTITY_INTERACT_EVENT -> NeoEntityInteractEventManager.register(eventHandler, priority, receiveCanceled);
            case ENTITY_INTERACT_SPECIFIC_EVENT -> NeoEntityInteractSpecificEventManager.register(eventHandler, priority, receiveCanceled);
            case LEFT_CLICK_BLOCK_EVENT -> NeoLeftClickBlockEventManager.register(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_BLOCK_EVENT -> NeoRightClickBlockEventManager.register(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_ITEM_EVENT -> NeoRightClickItemEventManager.register(eventHandler, priority, receiveCanceled);
            case BLOCK_BREAK_EVENT -> NeoBlockBreakEventManager.register(eventHandler, priority, receiveCanceled);
            case BLOCK_TOOL_MODIFICATION_EVENT -> NeoBlockToolModificationEventManager.register(eventHandler, priority, receiveCanceled);
            case ENTITY_PLACE_BLOCK_EVENT -> NeoEntityPlaceBlockEventManager.register(eventHandler, priority, receiveCanceled);
            case PORTAL_SPAWN_EVENT -> NeoPortalSpawnEventManager.register(eventHandler, priority, receiveCanceled);
            case FARMLAND_TRAMPLE_EVENT -> NeoFarmlandTrampleEventManager.register(eventHandler, priority, receiveCanceled);
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
            case PLAYER_RESPAWN_EVENT -> NeoPlayerRespawnEventManager.unregister(eventHandler, priority, receiveCanceled);
            case ENTITY_INTERACT_EVENT -> NeoEntityInteractEventManager.unregister(eventHandler, priority, receiveCanceled);
            case ENTITY_INTERACT_SPECIFIC_EVENT -> NeoEntityInteractSpecificEventManager.unregister(eventHandler, priority, receiveCanceled);
            case LEFT_CLICK_BLOCK_EVENT -> NeoLeftClickBlockEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_BLOCK_EVENT -> NeoRightClickBlockEventManager.unregister(eventHandler, priority, receiveCanceled);
            case RIGHT_CLICK_ITEM_EVENT -> NeoRightClickItemEventManager.unregister(eventHandler, priority, receiveCanceled);
            case BLOCK_BREAK_EVENT -> NeoBlockBreakEventManager.unregister(eventHandler, priority, receiveCanceled);
            case BLOCK_TOOL_MODIFICATION_EVENT -> NeoBlockToolModificationEventManager.unregister(eventHandler, priority, receiveCanceled);
            case ENTITY_PLACE_BLOCK_EVENT -> NeoEntityPlaceBlockEventManager.unregister(eventHandler, priority, receiveCanceled);
            case PORTAL_SPAWN_EVENT -> NeoPortalSpawnEventManager.unregister(eventHandler, priority, receiveCanceled);
            case FARMLAND_TRAMPLE_EVENT -> NeoFarmlandTrampleEventManager.unregister(eventHandler, priority, receiveCanceled);
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