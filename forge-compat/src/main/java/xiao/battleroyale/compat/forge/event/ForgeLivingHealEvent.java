package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ILivingHealEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeLivingHealEvent extends ForgeEvent implements ILivingHealEvent {

    protected LivingHealEvent livingHealEvent;

    public ForgeLivingHealEvent(Event event) {
        super(event);
        if (event instanceof LivingHealEvent eventIn) {
            this.livingHealEvent = eventIn;
        } else {
            throw new RuntimeException("Expected LivingHealEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.LIVING_HEAL_EVENT;
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingHealEvent.getEntity();
    }

    @Override
    public float getHealAmount() {
        return livingHealEvent.getAmount();
    }

    @Override
    public void setHealAmount(float amount) {
        this.livingHealEvent.setAmount(amount);
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