package xiao.battleroyale.compat.fabric.event;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.event.RenderLevelStage;
import net.minecraft.client.Camera;

public class FabricRenderLevelStageEvent extends FabricEvent implements IRenderLevelStageEvent {
    private final RenderLevelStage stage;
    private final Matrix4f modelViewMatrix;
    private final Vec3 cameraPosition;
    private final float partialTick;

    public FabricRenderLevelStageEvent(RenderLevelStage stage, Matrix4f modelViewMatrix, Camera camera, float partialTick) {
        super(false);
        this.stage = stage;
        this.modelViewMatrix = modelViewMatrix;
        this.cameraPosition = camera.getPosition();
        this.partialTick = partialTick;
    }

    @Override
    public RenderLevelStage getStage() {
        return this.stage;
    }

    @Override
    public Matrix4f getModelViewMatrix() {
        return this.modelViewMatrix;
    }

    @Override
    public Vec3 getCamera_getPosition() {
        return this.cameraPosition;
    }

    @Override
    public float getPartialTick() {
        return this.partialTick;
    }
}