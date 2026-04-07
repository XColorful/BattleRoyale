package xiao.battleroyale.api.event.server.utility;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.api.server.IServerManager;

public class SurvivalLobbyTeleportEvent extends AbstractUtilityEvent {

    protected @NotNull final LivingEntity livingEntity;

    public SurvivalLobbyTeleportEvent(IServerManager serverManager, @NotNull LivingEntity livingEntity) {
        super(serverManager);
        this.livingEntity = livingEntity;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.SURVIVAL_LOBBY_TELEPORT_EVENT;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Level level = livingEntity.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                livingEntity.position(),
                livingEntity.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                livingEntity
        );
    }

    @Override public String getTextName() {
        return livingEntity.getName().getString();
    }

    @Override public Component getDisplayName() {
        return livingEntity.getDisplayName();
    }
}
