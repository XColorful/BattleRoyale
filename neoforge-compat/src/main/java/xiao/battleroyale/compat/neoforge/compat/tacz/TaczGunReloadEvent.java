package xiao.battleroyale.compat.neoforge.compat.tacz;

import com.tacz.guns.api.event.common.GunReloadEvent;
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
import xiao.battleroyale.api.compat.tacz.IGunReloadEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class TaczGunReloadEvent extends NeoEvent implements IGunReloadEvent {

    protected GunReloadEvent gunReloadEvent;

    public TaczGunReloadEvent(GunReloadEvent gunReloadEvent) {
        super(gunReloadEvent);
        this.gunReloadEvent = gunReloadEvent;
    }

    @Override
    public McSide getMcSide() {
        return this.gunReloadEvent.getLogicalSide() == LogicalSide.CLIENT ? McSide.CLIENT : McSide.DEDICATED_SERVER;
    }

    @Override
    public LivingEntity getEntity() {
        return gunReloadEvent.getEntity();
    }
}

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (gunReloadEvent.getLogicalSide() == LogicalSide.CLIENT) return null;
        LivingEntity entity = getEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                entity.position(),
                Vec2.ZERO,
                (ServerLevel) entity.level(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                entity.getServer(),
                entity
        );
    }

    @Override public String getTextName() {
        return this.getEntity().getName().getString();
    }
    @Override public Component getDisplayName() {
        return this.getEntity().getDisplayName();
    }
}