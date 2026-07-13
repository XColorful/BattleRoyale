package xiao.battleroyale.compat.forge.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ISubmitCustomGeometryEvent;

@ApiStatus.AvailableSince("neoforge26.2")
public class ForgeSubmitCustomGeometryEvent extends ForgeEvent implements ISubmitCustomGeometryEvent {

    private final Event typedEvent;

    public ForgeSubmitCustomGeometryEvent(Event event) {
        super(event);
        this.typedEvent = event;
    }

    @Override
    public EventType getType() {
        return EventType.SUBMIT_CUSTOM_GEOMETRY_EVENT;
    }

    @Override
    public PoseStack getPoseStack() {
        return null;
    }

    @Override
    public Vec3 getCamera_getPosition() {
        return null;
    }

    @Override
    public float getPartialTick() {
        return 0;
    }

    @Override
    public Object getSubmitNodeCollector() {
        return null;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return null;
    }

    @Override public String getTextName() {
        return "ForgeSubmitCustomGeometryEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
