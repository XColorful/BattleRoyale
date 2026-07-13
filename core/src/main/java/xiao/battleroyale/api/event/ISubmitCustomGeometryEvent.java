package xiao.battleroyale.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.phys.Vec3;

public interface ISubmitCustomGeometryEvent extends IEvent {

    PoseStack getPoseStack();

    Vec3 getCamera_getPosition();

    float getPartialTick();

    SubmitNodeCollector getSubmitNodeCollector();
}
