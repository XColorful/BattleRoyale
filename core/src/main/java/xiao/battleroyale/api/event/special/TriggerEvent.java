package xiao.battleroyale.api.event.special;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class TriggerEvent extends CustomEvent {

    private final @Nullable CommandSourceStack source;
    private final String triggerString;
    private final Vec3 triggerPos;
    private final int triggerInt;
    private final double triggerDouble;
    private final boolean triggerBool;

    public TriggerEvent(@Nullable CommandSourceStack source, String triggerString) {
        this(source, triggerString, Vec3.ZERO, 0, 0, false);
    }
    public TriggerEvent(@Nullable CommandSourceStack source, String triggerString,
                        Vec3 triggerPos, int triggerInt, double triggerDouble, boolean triggerBool) {
        this.source = source;
        this.triggerString = triggerString;
        this.triggerPos = triggerPos;
        this.triggerInt = triggerInt;
        this.triggerDouble = triggerDouble;
        this.triggerBool = triggerBool;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.TRIGGER_EVENT;
    }

    public @Nullable CommandSourceStack getSource() {
        return source;
    }
    public @Nullable Entity getTriggerEntity() {
        return this.source != null ? this.source.getEntity() : null;
    }
    public @Nullable ServerLevel getTriggerLevel() {
        return this.source != null ? this.source.getLevel() : null;
    }
    public String getTriggerString() {
        return triggerString;
    }
    public Vec3 getTriggerPos() {
        return triggerPos;
    }
    public int getTriggerInt() {
        return triggerInt;
    }
    public double getTriggerDouble() {
        return triggerDouble;
    }
    public boolean getTriggerBool() {
        return triggerBool;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @Nullable Entity triggerEntity = this.getTriggerEntity();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                this.getTriggerPos(),
                triggerEntity != null ? triggerEntity.getRotationVector() : Vec2.ZERO,
                this.getTriggerLevel(),
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                BattleRoyale.getMinecraftServer(),
                triggerEntity
        );
    }
    public String getTextName() {
        return "CBR TriggerEvent";
    }
    public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
