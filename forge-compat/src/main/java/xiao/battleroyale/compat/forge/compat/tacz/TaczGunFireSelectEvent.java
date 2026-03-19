package xiao.battleroyale.compat.forge.compat.tacz;

import com.tacz.guns.api.event.common.GunFireSelectEvent;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.compat.tacz.IGunFireSelectEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class TaczGunFireSelectEvent extends ForgeEvent implements IGunFireSelectEvent {

    protected GunFireSelectEvent gunFireSelectEvent;

    public TaczGunFireSelectEvent(GunFireSelectEvent gunFireSelectEvent) {
        super(gunFireSelectEvent);
        this.gunFireSelectEvent = gunFireSelectEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunFireSelectEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getShooter() {
        return gunFireSelectEvent.getShooter();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (gunFireSelectEvent.getLogicalSide() == LogicalSide.CLIENT) return null;
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
