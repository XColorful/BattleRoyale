package xiao.battleroyale.config.common.server.function.defaultconfigs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.custom.deathmatch.AddKillEvent;
import xiao.battleroyale.api.event.custom.stats.GamePlayerRecordEvent;
import xiao.battleroyale.compat.cbraddon.CbrAddon;
import xiao.battleroyale.config.common.server.function.FunctionConfigManager;
import xiao.battleroyale.config.common.server.function.type.RegisterEntry;

import java.nio.file.Paths;
import java.util.Arrays;

import static xiao.battleroyale.util.JsonUtils.writeJsonToFile;

public class AllFunction {

    private static final String DEFAULT_FILE_NAME = "example_all_event_tags.json";

    public static void generateDefaultConfigs(String configDirPath) {
        JsonArray functionConfigJson = new JsonArray();
        functionConfigJson.add(generateAllEventTags());
        writeJsonToFile(Paths.get(configDirPath, DEFAULT_FILE_NAME).toString(), functionConfigJson);
    }

    private static JsonObject generateAllEventTags() {
        RegisterEntry registerEntry = new RegisterEntry(true,
                Arrays.asList(
                        // --------CustomEventType--------

                        // finish
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_complete", CustomEventType.GAME_COMPLETE_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_complete_finish", CustomEventType.GAME_COMPLETE_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_stop", CustomEventType.GAME_STOP_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_stop_finish", CustomEventType.GAME_STOP_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        // game
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_damage", CustomEventType.GAME_PLAYER_DAMAGE_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_damage_finish", CustomEventType.GAME_PLAYER_DAMAGE_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_death", CustomEventType.GAME_PLAYER_DEATH_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_death_finish", CustomEventType.GAME_PLAYER_DEATH_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_down", CustomEventType.GAME_PLAYER_DOWN_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_down_finish", CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_revive", CustomEventType.GAME_PLAYER_REVIVE_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_player_revive_finish", CustomEventType.GAME_PLAYER_REVIVE_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_spectate", CustomEventType.GAME_SPECTATE_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        // spawn
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_lobby_teleport", CustomEventType.GAME_LOBBY_TELEPORT_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_lobby_teleport_finish", CustomEventType.GAME_LOBBY_TELEPORT_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        // starter
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_init", CustomEventType.GAME_INIT_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_init_finish", CustomEventType.GAME_INIT_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_load", CustomEventType.GAME_LOAD_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_load_finish", CustomEventType.GAME_LOAD_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_start", CustomEventType.GAME_START_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_start_finish", CustomEventType.GAME_START_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        // team
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_invite_player", CustomEventType.INVITE_PLAYER_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_invite_player_complete", CustomEventType.INVITE_PLAYER_COMPLETE_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_request_player", CustomEventType.REQUEST_PLAYER_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_request_player_complete", CustomEventType.REQUEST_PLAYER_COMPLETE_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        // tick
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_loot_bfs", CustomEventType.GAME_LOOT_BFS_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_loot_bfs_finish", CustomEventType.GAME_LOOT_BFS_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_loot", CustomEventType.GAME_LOOT_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_loot_finish", CustomEventType.GAME_LOOT_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_tick", CustomEventType.GAME_TICK_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_game_tick_finish", CustomEventType.GAME_TICK_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_zone_tick", CustomEventType.ZONE_TICK_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_zone_tick_finish", CustomEventType.ZONE_TICK_FINISH_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        // zone
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_zone_created", CustomEventType.ZONE_CREATED_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_zone_complete", CustomEventType.ZONE_COMPLETE_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_custom_zone", CustomEventType.CUSTOM_ZONE_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_airdrop_zone", CustomEventType.AIRDROP_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_entity_zone", CustomEventType.ENTITY_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        // generate
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_custom_generate", CustomEventType.CUSTOM_GENERATE_EVENT.getName(),
                                EventPriority.HIGHEST, true, null),
                        // special
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_register_manager", CustomEventType.REGISTER_MANAGER_EVENT.getName(),
                                EventPriority.LOWEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_trigger", CustomEventType.TRIGGER_EVENT.getName(),
                                EventPriority.LOWEST, false, null),

                        // --------EventType--------

                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_server_tick", EventType.SERVER_TICK_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_living_attack", EventType.LIVING_ATTACK_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_living_hurt", EventType.LIVING_HURT_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_living_damage", EventType.LIVING_DAMAGE_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_living_death", EventType.LIVING_DEATH_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_player_logged_in", EventType.PLAYER_LOGGED_IN_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_player_logged_out", EventType.PLAYER_LOGGED_OUT_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_entity_interact", EventType.ENTITY_INTERACT_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_entity_interact_specific", EventType.ENTITY_INTERACT_SPECIFIC_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_left_click_block", EventType.LEFT_CLICK_BLOCK_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_right_click_block", EventType.RIGHT_CLICK_BLOCK_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_right_click_item", EventType.RIGHT_CLICK_ITEM_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_block_break", EventType.BLOCK_BREAK_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_block_tool_modification", EventType.BLOCK_TOOL_MODIFICATION_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_entity_place_block", EventType.ENTITY_PLACE_BLOCK_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_portal_spawn", EventType.PORTAL_SPAWN_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:on_farmland_trample", EventType.FARMLAND_TRAMPLE_EVENT.getName(),
                                EventPriority.HIGHEST, false, null),

                        // --------CustomEventClass--------

                        new RegisterEntry.RegisterDetail(null, "battleroyale:deathmatch/on_add_player_kill", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, AddKillEvent.AddPlayerKillEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:deathmatch/on_add_player_kill_finish", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.LOWEST, false, AddKillEvent.AddPlayerKillFinishEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:deathmatch/on_add_team_kill", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, AddKillEvent.AddTeamKillEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:deathmatch/on_add_team_kill_finish", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.LOWEST, false, AddKillEvent.AddTeamKillFinishEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_damage_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.DamageRecordEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_hurt_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.HurtRecordEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_down_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.DownRecordEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_knock_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.KnockRecordEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_revive_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.ReviveRecordEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_death_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.DeathRecordEvent.class.getName()),
                        new RegisterEntry.RegisterDetail(null, "battleroyale:stats/on_kill_record", CustomEventType.CUSTOM_EVENT.getName(),
                                EventPriority.HIGHEST, true, GamePlayerRecordEvent.KillRecordEvent.class.getName())
                )
        );

        boolean cbraddonLoaded = BattleRoyale.getMcRegistry().isModLoaded(CbrAddon.get().getModId());
        FunctionConfigManager.FunctionConfig functionConfig = new FunctionConfigManager.FunctionConfig(0, "Register all event tags", "#FFFFFF", cbraddonLoaded, registerEntry);

        return functionConfig.toJson();
    }
}
