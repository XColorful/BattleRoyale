package xiao.battleroyale.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.event.IEvent;

public interface IRenderLevelStageEvent extends IEvent {

    RenderLevelStage getStage();

    PoseStack getPoseStack();

    Vec3 getCamera_getPosition();

    float getPartialTick();
}
