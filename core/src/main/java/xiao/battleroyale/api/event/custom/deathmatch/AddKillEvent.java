package xiao.battleroyale.api.event.custom.deathmatch;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.game.process.IGameProcessManager;
import xiao.battleroyale.api.game.process.deathmatch.IDeathMatchProcessManager;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.util.GameUtils;

public abstract class AddKillEvent extends CustomEvent {

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

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                BattleRoyale.getGameManager().getServerLevel(),
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                BattleRoyale.getMinecraftServer(),
                null
        );
    }

    public static class AddPlayerKillEvent extends AddKillEvent {
        protected final @NotNull GamePlayer gamePlayer;
        protected final @Nullable LivingEntity livingEntity;

        public AddPlayerKillEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GamePlayer gamePlayer) {
            super(manager, preMaxKill, addKill);
            this.gamePlayer = gamePlayer;
            this.livingEntity = GameUtils.getLivingEntity(BattleRoyale.getGameManager().getServerLevel(), gamePlayer.getPlayerUUID());
        }
        public @NotNull GamePlayer getGamePlayer() {
            return gamePlayer;
        }

        @Override
        public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
            CommandSourceStack sourceStack = super.createCommandSourceStack(source)
                    .withPosition(gamePlayer.getLastPos());
            return livingEntity != null ? sourceStack.withRotation(livingEntity.getRotationVector()).withEntity(livingEntity) : sourceStack;
        }

        @Override public String getTextName() {
            return livingEntity != null ? livingEntity.getName().getString() : String.format("%s %s AddKillEvent", manager.getManagerName(), gamePlayer.getNameWithId());
        }
        @Override public Component getDisplayName() {
            return livingEntity != null ? livingEntity.getDisplayName() : Component.literal(getTextName());
        }
    }
    public static class AddPlayerKillFinishEvent extends AddPlayerKillEvent {
        public AddPlayerKillFinishEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GamePlayer gamePlayer) {
            super(manager, preMaxKill, addKill, gamePlayer);
        }
        @Override public final boolean isCancelable() {
            return false;
        }

        @Override public String getTextName() {
            return livingEntity != null ? livingEntity.getName().getString() : String.format("%s %s AddPlayerKillFinishEvent", manager.getManagerName(), gamePlayer.getNameWithId());
        }
        @Override public Component getDisplayName() {
            return livingEntity != null ? livingEntity.getDisplayName() : Component.literal(getTextName());
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

        @Override public String getTextName() {
            return String.format("%s Add Team %s Kill Event", manager.getManagerName(), gameTeam.getGameTeamId());
        }
        @Override public Component getDisplayName() {
            return Component.literal(getTextName());
        }
    }
    public static class AddTeamKillFinishEvent extends AddTeamKillEvent {
        public AddTeamKillFinishEvent(@NotNull IDeathMatchProcessManager manager, int preMaxKill, int addKill, @NotNull GameTeam gameTeam) {
            super(manager, preMaxKill, addKill, gameTeam);
        }
        @Override public final boolean isCancelable() {
            return false;
        }

        @Override public String getTextName() {
            return String.format("%s Add Team %s Kill Finish Event", manager.getManagerName(), gameTeam.getGameTeamId());
        }
    }
}
