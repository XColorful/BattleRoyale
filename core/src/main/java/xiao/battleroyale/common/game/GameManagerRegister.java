package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.register.RegisterManagerEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.lobby.GameLobbyManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.process.battleroyale.BRGameProcessManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;

public class GameManagerRegister implements ICustomEventHandler {

    private static class GameManagerRegisterHolder {
        private static final GameManagerRegister INSTANCE = new GameManagerRegister();
    }

    public static GameManagerRegister get() {
        return GameManagerRegisterHolder.INSTANCE;
    }

    private GameManagerRegister() {}

    @Override
    public String getEventHandlerName() {
        return String.format("%s:GameManagerRegister", BattleRoyale.MOD_ID);
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
            if (managerName.equals("GameManager")) {
                BattleRoyale.setGameManager(GameManager.get());
                IGameManager gameManager = BattleRoyale.getGameManager();
                boolean registered = gameManager.setGameProcessManager(BRGameProcessManager.get())
                        && gameManager.setGameruleManager(GameruleManager.get())
                        && gameManager.setGameLootManager(GameLootManager.get())
                        && gameManager.setSpawnManager(SpawnManager.get())
                        && gameManager.setGameLobbyManager(GameLobbyManager.get())
                        && gameManager.setStatsManager(StatsManager.get())
                        && gameManager.setTeamManager(TeamManager.get())
                        && gameManager.setZoneManager(ZoneManager.get());
                event.setCanceled(registered);
            }
        }
    }
}
