package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunShootEvent;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunShootEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunShootEvent extends NeoEvent implements IGunShootEvent {

    protected GunShootEvent gunShootEvent;

    public TaczGunShootEvent(GunShootEvent gunShootEvent) {
        super(gunShootEvent);
        this.gunShootEvent = gunShootEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunShootEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return gunShootEvent.getShooter();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (gunShootEvent.getLogicalSide() == LogicalSide.CLIENT) return null;
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