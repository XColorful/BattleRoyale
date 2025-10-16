package xiao.battleroyale.compat.forge.event;

import net.minecraftforge.common.MinecraftForge;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.IEventPoster;
import xiao.battleroyale.compat.forge.event.game.finish.*;
import xiao.battleroyale.compat.forge.event.game.game.*;
import xiao.battleroyale.compat.forge.event.game.spawn.*;
import xiao.battleroyale.compat.forge.event.game.starter.*;
import xiao.battleroyale.compat.forge.event.game.team.*;
import xiao.battleroyale.compat.forge.event.game.tick.*;
import xiao.battleroyale.compat.forge.event.game.zone.*;
import xiao.battleroyale.compat.forge.event.loot.generate.CustomGenerateEvent;

public class ForgeEventPoster implements IEventPoster {

    @Override
    public boolean postEvent(ICustomEventData customEventData) {
        CustomEventType customEventType = customEventData.getEventType();
        switch (customEventType) {
            // finish
            case GAME_COMPLETE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameCompleteEvent.createEvent(customEventData));
            }
            case GAME_COMPLETE_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameCompleteFinishEvent.createEvent(customEventData));
            }
            case GAME_STOP_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameStopEvent.createEvent(customEventData));
            }
            case GAME_STOP_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameStopFinishEvent.createEvent(customEventData));
            }
            case SERVER_STOP_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(ServerStopEvent.createEvent(customEventData));
            }
            case SERVER_STOP_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(ServerStopFinishEvent.createEvent(customEventData));
            }
            // game
            case GAME_PLAYER_DEATH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GamePlayerDeathEvent.createEvent(customEventData));
            }
            case GAME_PLAYER_DEATH_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GamePlayerDeathFinishEvent.createEvent(customEventData));
            }
            case GAME_PLAYER_DOWN_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GamePlayerDownEvent.createEvent(customEventData));
            }
            case GAME_PLAYER_DOWN_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GamePlayerDownFinishEvent.createEvent(customEventData));
            }
            case GAME_PLAYER_REVIVE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GamePlayerReviveEvent.createEvent(customEventData));
            }
            case GAME_PLAYER_REVIVE_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GamePlayerReviveFinishEvent.createEvent(customEventData));
            }
            case GAME_SPECTATE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameSpectateEvent.createEvent(customEventData));
            }
            // spawn
            case GAME_LOBBY_TELEPORT_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLobbyTeleportEvent.createEvent(customEventData));
            }
            case GAME_LOBBY_TELEPORT_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLobbyTeleportFinishEvent.createEvent(customEventData));
            }
            // starter
            case GAME_INIT_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameInitEvent.createEvent(customEventData));
            }
            case GAME_INIT_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameInitFinishEvent.createEvent(customEventData));
            }
            case GAME_LOAD_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLoadEvent.createEvent(customEventData));
            }
            case GAME_LOAD_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLoadFinishEvent.createEvent(customEventData));
            }
            case GAME_START_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameStartEvent.createEvent(customEventData));
            }
            case GAME_START_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameStartFinishEvent.createEvent(customEventData));
            }
            // team
            case INVITE_PLAYER_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(InvitePlayerEvent.createEvent(customEventData));
            }
            case INVITE_PLAYER_COMPLETE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(InvitePlayerCompleteEvent.createEvent(customEventData));
            }
            case REQUEST_PLAYER_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(RequestPlayerEvent.createEvent(customEventData));
            }
            case REQUEST_PLAYER_COMPLETE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(RequestPlayerCompleteEvent.createEvent(customEventData));
            }
            // tick
            case GAME_LOOT_BFS_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLootBfsEvent.createEvent(customEventData));
            }
            case GAME_LOOT_BFS_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLootBfsFinishEvent.createEvent(customEventData));
            }
            case GAME_LOOT_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLootEvent.createEvent(customEventData));
            }
            case GAME_LOOT_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameLootFinishEvent.createEvent(customEventData));
            }
            case GAME_TICK_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameTickEvent.createEvent(customEventData));
            }
            case GAME_TICK_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(GameTickFinishEvent.createEvent(customEventData));
            }
            case ZONE_TICK_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(ZoneTickEvent.createEvent(customEventData));
            }
            case ZONE_TICK_FINISH_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(ZoneTickFinishEvent.createEvent(customEventData));
            }
            // zone
            case AIRDROP_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(AirdropEvent.createEvent(customEventData));
            }
            case CUSTOM_ZONE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(CustomZoneEvent.createEvent(customEventData));
            }
            case ZONE_COMPLETE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(ZoneCompleteEvent.createEvent(customEventData));
            }
            case ZONE_CREATED_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(ZoneCreatedEvent.createEvent(customEventData));
            }
            // generate
            case CUSTOM_GENERATE_EVENT -> {
                return MinecraftForge.EVENT_BUS.post(CustomGenerateEvent.createEvent(customEventData));
            }
            default -> {
                return false;
            }
        }
    }
}
