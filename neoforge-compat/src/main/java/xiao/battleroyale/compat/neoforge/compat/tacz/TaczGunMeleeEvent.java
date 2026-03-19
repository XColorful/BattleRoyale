package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunMeleeEvent;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunMeleeEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunMeleeEvent extends NeoEvent implements IGunMeleeEvent {

    protected GunMeleeEvent gunMeleeEvent;

    public TaczGunMeleeEvent(GunMeleeEvent gunMeleeEvent) {
        super(gunMeleeEvent);
        this.gunMeleeEvent = gunMeleeEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunMeleeEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return gunMeleeEvent.getShooter();
    }
}

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (gunMeleeEvent.getLogicalSide() == LogicalSide.CLIENT) return null;
        LivingEntity shooter = getShooter();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                shooter.position(),
                Vec2.ZERO,
                (ServerLevel) shooter.level(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                shooter.getServer(),
                shooter
        );
    }

    @Override public String getTextName() {
        return this.getShooter().getName().getString();
    }
    @Override public Component getDisplayName() {
        return this.getShooter().getDisplayName();
    }
}