package xiao.battleroyale.api.event.custom.stats;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.game.stats.IStatsManager;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.common.game.stats.record.*;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.GameUtils;

public abstract class GamePlayerRecordEvent<T extends AbstractGamePlayerEventRecord<T>> extends CustomEvent {

    protected final @NotNull IStatsManager statsManager;
    protected final @NotNull T eventRecord;
    protected final @Nullable LivingEntity livingEntity;

    public GamePlayerRecordEvent(@NotNull IStatsManager statsManager, @NotNull T eventRecord) {
        this.statsManager = statsManager;
        this.eventRecord = eventRecord;
        this.livingEntity = GameUtils.getLivingEntity(BattleRoyale.getGameManager().getServerLevel(), this.eventRecord.getGamePlayer().getPlayerUUID());
    }

    public @NotNull IStatsManager getStatsManager() {
        return statsManager;
    }
    public GamePlayer getGamePlayer() {
        return eventRecord.getGamePlayer();
    }
    public @Nullable LivingEntity getLivingEntity() {
        return livingEntity;
    }

    @Override
    public CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @Nullable LivingEntity livingEntity = this.getLivingEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                this.getGamePlayer().getLastPos(),
                livingEntity != null ? livingEntity.getRotationVector() : Vec2.ZERO,
                BattleRoyale.getGameManager().getServerLevel(),
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                BattleRoyale.getMinecraftServer(),
                livingEntity
        );
    }
    @Override public String getTextName() {
        return this.getGamePlayer().getNameWithId();
    }
    @Override public Component getDisplayName() {
        LivingEntity livingEntity = this.getLivingEntity();
        return livingEntity != null ? livingEntity.getDisplayName() : Component.literal(getTextName());
    }

    public static class DamageRecordEvent extends GamePlayerRecordEvent<DamageRecord> {
        public DamageRecordEvent(@NotNull IStatsManager statsManager, @NotNull DamageRecord eventRecord) {
            super(statsManager, eventRecord);
        }
    }
    public static class HurtRecordEvent extends GamePlayerRecordEvent<HurtRecord> {
        public HurtRecordEvent(@NotNull IStatsManager statsManager, @NotNull HurtRecord hurtRecord) {
            super(statsManager, hurtRecord);
        }
    }
    public static class DownRecordEvent extends GamePlayerRecordEvent<DownRecord> {
        public DownRecordEvent(@NotNull IStatsManager statsManager, @NotNull DownRecord eventRecord) {
            super(statsManager, eventRecord);
        }
    }
    public static class KnockRecordEvent extends GamePlayerRecordEvent<KnockRecord> {
        public KnockRecordEvent(@NotNull IStatsManager statsManager, @NotNull KnockRecord knockRecord) {
            super(statsManager, knockRecord);
        }
    }
    public static class ReviveRecordEvent extends GamePlayerRecordEvent<ReviveRecord> {
        public ReviveRecordEvent(@NotNull IStatsManager statsManager, @NotNull ReviveRecord eventRecord) {
            super(statsManager, eventRecord);
        }
    }
    public static class DeathRecordEvent extends GamePlayerRecordEvent<DeathRecord> {
        public DeathRecordEvent(@NotNull IStatsManager statsManager, @NotNull DeathRecord eventRecord) {
            super(statsManager, eventRecord);
        }
    }
    public static class KillRecordEvent extends GamePlayerRecordEvent<KillRecord> {
        public KillRecordEvent(@NotNull IStatsManager statsManager, @NotNull KillRecord killRecord) {
            super(statsManager, killRecord);
        }
    }
}
