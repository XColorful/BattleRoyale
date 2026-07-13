package xiao.battleroyale.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("neoforge26.2")
public interface ISubmitCustomGeometryEvent extends IEvent {

    PoseStack getPoseStack();

    Vec3 getCamera_getPosition();

    float getPartialTick();

    @ApiStatus.AvailableSince("neoforge26.2")
    Object getSubmitNodeCollector();
}
