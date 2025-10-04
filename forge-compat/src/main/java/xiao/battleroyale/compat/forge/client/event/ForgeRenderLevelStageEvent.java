package xiao.battleroyale.compat.forge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class ForgeRenderLevelStageEvent extends ForgeEvent implements IRenderLevelStageEvent {

    private final RenderLevelStageEvent typedEvent;
    private final RenderLevelStage stage;

    public ForgeRenderLevelStageEvent(RenderLevelStageEvent event) {
        super(event);
        this.typedEvent = event;
        this.stage = ForgeRenderLevelStage.fromStage(event.getStage());
    }

    @Override
    public RenderLevelStage getStage() {
        return this.stage;
    }

    @Override
    public PoseStack getPoseStack() {
        PoseStack poseStack = new PoseStack();
        Matrix4f forgeMatrix = this.typedEvent.getProjectionMatrix(); // Forge 只给 Matrix4f
        poseStack.last().pose().set(forgeMatrix);
        return poseStack;
    }

    @Override
    public Vec3 getCamera_getPosition() {
        return this.typedEvent.getCamera().getPosition();
    }

    @Override
    public float getPartialTick() {
        return this.typedEvent.getPartialTick();
    }
}
