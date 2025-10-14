package xiao.battleroyale.compat.neoforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;

public class NeoRenderLevelStageEvent extends NeoEvent implements IRenderLevelStageEvent {

    private final RenderLevelStageEvent typedEvent;
    private final RenderLevelStage stage;

    public NeoRenderLevelStageEvent(RenderLevelStageEvent event) {
        super(event);
        this.typedEvent = event;
        this.stage = NeoRenderLevelStage.fromEventClass(event.getClass());
    }

    @Override
    public RenderLevelStage getStage() {
        return this.stage;
    }

    @Override
    public Matrix4f getModelViewMatrix() {
        return this.typedEvent.getPoseStack().last().pose();
    }

    @Override
    public Vec3 getCamera_getPosition() {
        return this.typedEvent.getLevelRenderState().cameraRenderState.pos;
    }

    @Override
    public float getPartialTick() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }
}