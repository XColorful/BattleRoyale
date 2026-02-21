package xiao.battleroyale.api.event;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public interface IRenderLevelStageEvent extends IEvent {

    RenderLevelStage getStage();

    Matrix4f getModelViewMatrix();

    Vec3 getCamera_getPosition();

    float getPartialTick();
}
