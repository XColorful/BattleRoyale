package xiao.battleroyale.api.event.game.zone;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.event.EventDispatcher;

public class CustomZoneEvent extends AbstractSpecialZoneEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;

    public CustomZoneEvent(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext, String protocol,
                           @NotNull JsonObject jsonTag, @NotNull GamePlayer gamePlayer, @Nullable LivingEntity livingEntity) {
        super(gameManager, zoneTickContext, protocol, jsonTag);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.CUSTOM_ZONE_EVENT;
    }

    public @NotNull GamePlayer gamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(CommandSource source) {
        if (livingEntity == null) return super.createCommandSourceStack(source);
        return super.createCommandSourceStack(source)
                .withPosition(livingEntity.position())
                .withEntity(livingEntity);
    }

    @Override public String getTextName() {
        return this.livingEntity != null ? this.livingEntity.getName().getString() : String.format("CBR %s CustomZoneEvent", gamePlayer.getNameWithId());
    }
    @Override public Component getDisplayName() {
        return this.livingEntity != null ? this.livingEntity.getDisplayName() : Component.literal(getTextName());
    }

    private static final EventDispatcher _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(CustomZoneEvent.class);
    @Override public @NotNull EventDispatcher getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
