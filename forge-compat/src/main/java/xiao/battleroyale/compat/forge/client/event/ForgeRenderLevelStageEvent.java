package xiao.battleroyale.compat.forge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;

public class ForgeRenderLevelStageEvent implements IRenderLevelStageEvent {

    private final RenderLevelStageEvent event;
    private final RenderLevelStage stage;

    public ForgeRenderLevelStageEvent(RenderLevelStageEvent event) {
        this.event = event;
        this.stage = ForgeRenderLevelStage.fromStage(event.getStage());
    }

    @Override
    public RenderLevelStage getStage() {
        return this.stage;
    }

    @Override
    public PoseStack getPoseStack() {
        return this.event.getPoseStack();
    }

    @Override
    public Vec3 getCamera_getPosition() {
        return this.event.getCamera().getPosition();
    }

    @Override
    public float getPartialTick() {
        return this.event.getPartialTick();
    }

    @Override
    public boolean isCanceled() {
        return this.event.isCanceled();
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.event.setCanceled(cancel);
    }

    @Override
    public Object getEvent() {
        return this.event;
    }
}
