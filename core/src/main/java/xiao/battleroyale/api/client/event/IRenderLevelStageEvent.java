package xiao.battleroyale.api.client.event;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import xiao.battleroyale.api.event.IEvent;

public interface IRenderLevelStageEvent extends IEvent {

    RenderLevelStage getStage();

    Matrix4f getModelViewMatrix();

    Vec3 getCamera_getPosition();

    float getPartialTick();
}
