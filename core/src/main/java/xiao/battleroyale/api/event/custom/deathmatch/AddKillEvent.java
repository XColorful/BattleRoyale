package xiao.battleroyale.api.event.custom.deathmatch;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.game.process.IGameProcessManager;
import xiao.battleroyale.api.game.process.deathmatch.IDeathMatchProcessManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

public class AddKillEvent extends CustomEvent {

    protected final @NotNull IDeathMatchProcessManager manager;
    protected final int preMaxKill;
    protected final int addKill;

    public AddKillEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill) {
        this.manager = manager;
        this.preMaxKill = preMaxKill;
        this.addKill = addKill;
    }
    public @NotNull IGameProcessManager getGameProcessManager() {
        return getManager();
    }
    public @NotNull IDeathMatchProcessManager getManager() {
        return manager;
    }
    public int getAddKill() {
        return addKill;
    }
    public int getCurrentMaxKill() {
        return manager.getCurrentMaxKill();
    }

    public static class AddPlayerKillEvent extends AddKillEvent {
        protected final @NotNull GamePlayer gamePlayer;

        public AddPlayerKillEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GamePlayer gamePlayer) {
            super(manager, preMaxKill, addKill);
            this.gamePlayer = gamePlayer;
        }
        public @NotNull GamePlayer getGamePlayer() {
            return gamePlayer;
        }
    }
    public static class AddPlayerKillFinishEvent extends AddPlayerKillEvent {
        public AddPlayerKillFinishEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GamePlayer gamePlayer) {
            super(manager, preMaxKill, addKill, gamePlayer);
        }
        @Override public final boolean isCancelable() {
            return false;
        }
    }
    public static class AddTeamKillEvent extends AddKillEvent {
        protected final @NotNull GameTeam gameTeam;

        public AddTeamKillEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GameTeam gameTeam) {
            super(manager, preMaxKill, addKill);
            this.gameTeam = gameTeam;
        }
        public @NotNull GameTeam getGameTeam() {
            return gameTeam;
        }
    }
    public static class AddTeamKillFinishEvent extends AddTeamKillEvent {
        public AddTeamKillFinishEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GameTeam gameTeam) {
            super(manager, preMaxKill, addKill, gameTeam);
        }
        @Override public final boolean isCancelable() {
            return false;
        }
    }
}
