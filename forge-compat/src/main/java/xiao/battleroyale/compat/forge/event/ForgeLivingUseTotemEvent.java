package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingUseTotemEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeLivingUseTotemEvent extends ForgeEvent implements ILivingUseTotemEvent {

    protected LivingUseTotemEvent livingUseTotemEvent;

    public ForgeLivingUseTotemEvent(Event event) {
        super(event);
        if (event instanceof LivingUseTotemEvent eventIn) {
            this.livingUseTotemEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingUseTotemEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.LIVING_USE_TOTEM_EVENT;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingUseTotemEvent.getEntity();
    }

    @Override
    public @NotNull DamageSource getSource() {
        return livingUseTotemEvent.getSource();
    }

    @Override
    public @NotNull ItemStack getTotem() {
        return livingUseTotemEvent.getTotem();
    }

    @Override
    public @NotNull InteractionHand getHandHolding() {
        return livingUseTotemEvent.getHandHolding();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @NotNull LivingEntity entity = this.getEntity();
        Level level = entity.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
                entity.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                entity
        );
    }

    @Override
    public String getTextName() {
        return this.getEntity().getName().getString();
    }

    @Override
    public Component getDisplayName() {
        return this.getEntity().getDisplayName();
    }
}