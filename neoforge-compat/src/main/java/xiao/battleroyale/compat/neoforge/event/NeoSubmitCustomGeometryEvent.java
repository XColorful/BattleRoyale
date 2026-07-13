package xiao.battleroyale.compat.neoforge.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ISubmitCustomGeometryEvent;

@ApiStatus.AvailableSince("neoforge26.2")
public class NeoSubmitCustomGeometryEvent extends NeoEvent implements ISubmitCustomGeometryEvent {

    private final Event typedEvent;

    public NeoSubmitCustomGeometryEvent(Event event) {
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
        return "NeoSubmitCustomGeometryEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
